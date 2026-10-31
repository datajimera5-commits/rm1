package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.model.MatchPreset
import com.example.model.MatchState
import com.example.model.PresetData
import com.example.model.ScoreAction
import com.example.model.TeamConfig
import com.example.util.FeedbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppScreen {
    HOME,
    SCOREBOARD,
    EDIT
}

data class ScoreTapFeedback(
    val teamIndex: Int,
    val text: String,
    val isIncrement: Boolean,
    val eventId: Long = System.currentTimeMillis()
)

class ScoreboardViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("vs_scoreboard_prefs", Context.MODE_PRIVATE)
    private val feedbackHelper = FeedbackHelper(application)

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _matchState = MutableStateFlow(loadInitialState())
    val matchState: StateFlow<MatchState> = _matchState.asStateFlow()

    private val _lastTapFeedback = MutableStateFlow<ScoreTapFeedback?>(null)
    val lastTapFeedback: StateFlow<ScoreTapFeedback?> = _lastTapFeedback.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    private fun loadInitialState(): MatchState {
        val t1Name = prefs.getString("t1_name", "Messi") ?: "Messi"
        val t1Color = prefs.getLong("t1_color", 0xFFD81B60) // Magenta pink
        val t1Initial = prefs.getInt("t1_initial", 982)

        val t2Name = prefs.getString("t2_name", "Ronaldo") ?: "Ronaldo"
        val t2Color = prefs.getLong("t2_color", 0xFF76FF03) // Neon lime
        val t2Initial = prefs.getInt("t2_initial", 980)

        val sound = prefs.getBoolean("sound_enabled", true)
        val vibrate = prefs.getBoolean("vibrate_enabled", true)
        val step = prefs.getInt("score_step", 1)

        val t1 = TeamConfig(t1Name, t1Color, t1Initial)
        val t2 = TeamConfig(t2Name, t2Color, t2Initial)

        return MatchState(
            team1 = t1,
            team2 = t2,
            score1 = t1Initial,
            score2 = t2Initial,
            step = step,
            soundEnabled = sound,
            vibrateEnabled = vibrate
        )
    }

    private fun persistConfig(state: MatchState) {
        prefs.edit()
            .putString("t1_name", state.team1.name)
            .putLong("t1_color", state.team1.colorHex)
            .putInt("t1_initial", state.team1.initialScore)
            .putString("t2_name", state.team2.name)
            .putLong("t2_color", state.team2.colorHex)
            .putInt("t2_initial", state.team2.initialScore)
            .putBoolean("sound_enabled", state.soundEnabled)
            .putBoolean("vibrate_enabled", state.vibrateEnabled)
            .putInt("score_step", state.step)
            .apply()
    }

    fun incrementScore(teamIndex: Int) {
        val currentState = _matchState.value
        val step = currentState.step
        val prev1 = currentState.score1
        val prev2 = currentState.score2

        val newScore1 = if (teamIndex == 1) prev1 + step else prev1
        val newScore2 = if (teamIndex == 2) prev2 + step else prev2

        val action = ScoreAction(teamIndex, step, prev1, prev2)
        val newHistory = (listOf(action) + currentState.history).take(50)

        _matchState.update {
            it.copy(
                score1 = newScore1,
                score2 = newScore2,
                history = newHistory
            )
        }

        triggerFeedback(isIncrement = true)
        _lastTapFeedback.value = ScoreTapFeedback(
            teamIndex = teamIndex,
            text = "+$step",
            isIncrement = true
        )
    }

    fun decrementScore(teamIndex: Int) {
        val currentState = _matchState.value
        val step = currentState.step
        val prev1 = currentState.score1
        val prev2 = currentState.score2

        val newScore1 = if (teamIndex == 1) prev1 - step else prev1
        val newScore2 = if (teamIndex == 2) prev2 - step else prev2

        val action = ScoreAction(teamIndex, -step, prev1, prev2)
        val newHistory = (listOf(action) + currentState.history).take(50)

        _matchState.update {
            it.copy(
                score1 = newScore1,
                score2 = newScore2,
                history = newHistory
            )
        }

        triggerFeedback(isIncrement = false)
        _lastTapFeedback.value = ScoreTapFeedback(
            teamIndex = teamIndex,
            text = "-$step",
            isIncrement = false
        )
    }

    fun undoLastAction() {
        val currentState = _matchState.value
        val history = currentState.history
        if (history.isEmpty()) return

        val lastAction = history.first()
        val remainingHistory = history.drop(1)

        _matchState.update {
            it.copy(
                score1 = lastAction.prevScore1,
                score2 = lastAction.prevScore2,
                history = remainingHistory
            )
        }
        triggerFeedback(isIncrement = false)
    }

    fun resetToInitialScores() {
        val state = _matchState.value
        _matchState.update {
            it.copy(
                score1 = it.team1.initialScore,
                score2 = it.team2.initialScore,
                history = emptyList()
            )
        }
        triggerFeedback(isIncrement = false)
    }

    fun resetToZero() {
        _matchState.update {
            it.copy(
                score1 = 0,
                score2 = 0,
                history = emptyList()
            )
        }
        triggerFeedback(isIncrement = false)
    }

    fun swapTeams() {
        _matchState.update { current ->
            val swapped = current.copy(
                team1 = current.team2,
                team2 = current.team1,
                score1 = current.score2,
                score2 = current.score1,
                history = emptyList()
            )
            persistConfig(swapped)
            swapped
        }
        triggerFeedback(isIncrement = true)
    }

    fun updateTeams(
        team1Name: String,
        team1Color: Long,
        team1Start: Int,
        team2Name: String,
        team2Color: Long,
        team2Start: Int,
        step: Int
    ) {
        val t1 = TeamConfig(team1Name.trim().ifEmpty { "Team 1" }, team1Color, team1Start)
        val t2 = TeamConfig(team2Name.trim().ifEmpty { "Team 2" }, team2Color, team2Start)

        _matchState.update { current ->
            val updated = current.copy(
                team1 = t1,
                team2 = t2,
                score1 = team1Start,
                score2 = team2Start,
                step = step,
                history = emptyList()
            )
            persistConfig(updated)
            updated
        }
        _currentScreen.value = AppScreen.SCOREBOARD
    }

    fun applyPreset(preset: MatchPreset) {
        val t1 = TeamConfig(preset.team1Name, preset.team1Color, preset.team1Score)
        val t2 = TeamConfig(preset.team2Name, preset.team2Color, preset.team2Score)

        _matchState.update { current ->
            val updated = current.copy(
                team1 = t1,
                team2 = t2,
                score1 = preset.team1Score,
                score2 = preset.team2Score,
                history = emptyList()
            )
            persistConfig(updated)
            updated
        }
    }

    fun toggleSound() {
        _matchState.update {
            val updated = it.copy(soundEnabled = !it.soundEnabled)
            persistConfig(updated)
            updated
        }
    }

    fun toggleVibrate() {
        _matchState.update {
            val updated = it.copy(vibrateEnabled = !it.vibrateEnabled)
            persistConfig(updated)
            updated
        }
    }

    private fun triggerFeedback(isIncrement: Boolean) {
        val state = _matchState.value
        if (state.soundEnabled) {
            feedbackHelper.playClickSound(isIncrement)
        }
        if (state.vibrateEnabled) {
            feedbackHelper.vibrate(isIncrement)
        }
    }
}
