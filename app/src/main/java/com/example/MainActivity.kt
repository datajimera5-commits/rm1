package com.example

import android.app.Activity
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.EditMatchScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.ScoreboardViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScoreboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                ScoreboardApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ScoreboardApp(viewModel: ScoreboardViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val matchState by viewModel.matchState.collectAsStateWithLifecycle()
    val lastTapFeedback by viewModel.lastTapFeedback.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }

    // Auto rotate screen & Immersive Fullscreen Mode
    DisposableEffect(currentScreen) {
        activity?.let { act ->
            val window = act.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)

            if (currentScreen == AppScreen.SCOREBOARD) {
                // Auto switch to landscape when match starts
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                // True full screen: hide status bar (battery, wifi, clock) and nav bars
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                // Switch back to portrait on home and edit screens and restore status bar
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            activity?.let { act ->
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val window = act.window
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F111A))
    ) {
        Crossfade(
            targetState = currentScreen,
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color(0xFF0F111A)
                    ) { innerPadding ->
                        HomeScreen(
                            matchState = matchState,
                            onStartMatch = { viewModel.navigateTo(AppScreen.SCOREBOARD) },
                            onEditMatch = { viewModel.navigateTo(AppScreen.EDIT) },
                            onApplyPreset = { preset -> viewModel.applyPreset(preset) },
                            onSwapTeams = { viewModel.swapTeams() },
                            onResetScores = { viewModel.resetToInitialScores() },
                            onToggleSound = { viewModel.toggleSound() },
                            onToggleVibrate = { viewModel.toggleVibrate() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                AppScreen.SCOREBOARD -> {
                    // True full bleed edge-to-edge without status bar or padding
                    ScoreboardScreen(
                        matchState = matchState,
                        lastTapFeedback = lastTapFeedback,
                        onIncrementTeam1 = { viewModel.incrementScore(1) },
                        onDecrementTeam1 = { viewModel.decrementScore(1) },
                        onIncrementTeam2 = { viewModel.incrementScore(2) },
                        onDecrementTeam2 = { viewModel.decrementScore(2) },
                        onResetToInitial = { viewModel.resetToInitialScores() },
                        onResetToZero = { viewModel.resetToZero() },
                        onUndo = { viewModel.undoLastAction() },
                        onSwapTeams = { viewModel.swapTeams() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleOrientation = {
                            activity?.let { act ->
                                val isCurrentlyLandscape = act.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
                                        act.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                act.requestedOrientation = if (isCurrentlyLandscape) {
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                } else {
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                }
                            }
                        },
                        onEditMatch = { viewModel.navigateTo(AppScreen.EDIT) },
                        onBackToHome = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AppScreen.EDIT -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color(0xFF0F111A)
                    ) { innerPadding ->
                        EditMatchScreen(
                            matchState = matchState,
                            onSaveAndStart = { t1Name, t1Color, t1Start, t2Name, t2Color, t2Start, step ->
                                viewModel.updateTeams(
                                    team1Name = t1Name,
                                    team1Color = t1Color,
                                    team1Start = t1Start,
                                    team2Name = t2Name,
                                    team2Color = t2Color,
                                    team2Start = t2Start,
                                    step = step
                                )
                            },
                            onBack = { viewModel.navigateTo(AppScreen.HOME) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
