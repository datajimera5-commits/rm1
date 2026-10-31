package com.example.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchState
import com.example.ui.components.TeamScorePanel
import com.example.ui.components.VsBadge
import com.example.viewmodel.ScoreTapFeedback

@Composable
fun ScoreboardScreen(
    matchState: MatchState,
    lastTapFeedback: ScoreTapFeedback?,
    onIncrementTeam1: () -> Unit,
    onDecrementTeam1: () -> Unit,
    onIncrementTeam2: () -> Unit,
    onDecrementTeam2: () -> Unit,
    onResetToInitial: () -> Unit,
    onResetToZero: () -> Unit,
    onUndo: () -> Unit,
    onSwapTeams: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleOrientation: () -> Unit,
    onEditMatch: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Back gesture or device back returns directly to Home screen
    BackHandler {
        onBackToHome()
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("scoreboard_screen")
    ) {
        // Dual-team split arena layout (Left Team 1 / Right Team 2)
        Row(modifier = Modifier.fillMaxSize()) {
            // TEAM 1 PANEL (LEFT)
            TeamScorePanel(
                teamIndex = 1,
                name = matchState.team1.name,
                score = matchState.score1,
                colorHex = matchState.team1.colorHex,
                step = matchState.step,
                onIncrement = onIncrementTeam1,
                onDecrement = onDecrementTeam1,
                isWinnerOrLeader = matchState.score1 > matchState.score2,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            // Center vertical divider line
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x66FFFFFF),
                                Color.White,
                                Color(0x66FFFFFF)
                            )
                        )
                    )
            )

            // TEAM 2 PANEL (RIGHT)
            TeamScorePanel(
                teamIndex = 2,
                name = matchState.team2.name,
                score = matchState.score2,
                colorHex = matchState.team2.colorHex,
                step = matchState.step,
                onIncrement = onIncrementTeam2,
                onDecrement = onDecrementTeam2,
                isWinnerOrLeader = matchState.score2 > matchState.score1,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        // Center VS Badge - Strictly visual, does NOT capture or block any taps
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VsBadge(isLandscape = isLandscape)
        }

        // Animated Tap Feedback Bubble (+1 / -1)
        lastTapFeedback?.let { feedback ->
            TapFeedbackOverlay(feedback = feedback)
        }
    }
}

@Composable
fun TapFeedbackOverlay(feedback: ScoreTapFeedback) {
    val animY = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }

    LaunchedEffect(feedback.eventId) {
        animY.snapTo(0f)
        animAlpha.snapTo(1f)
        animY.animateTo(-40f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        animAlpha.animateTo(0f, animationSpec = tween(180))
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val alignment = if (feedback.teamIndex == 1) Alignment.CenterStart else Alignment.CenterEnd
        val paddingHorizontal = 56.dp

        Box(
            modifier = Modifier
                .align(alignment)
                .padding(horizontal = paddingHorizontal)
                .offset(y = animY.value.dp)
                .alpha(animAlpha.value)
        ) {
            Surface(
                color = if (feedback.isIncrement) Color(0xDD00E676) else Color(0xDDFF5252),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = feedback.text,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
