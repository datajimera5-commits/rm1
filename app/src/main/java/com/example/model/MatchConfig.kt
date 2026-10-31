package com.example.model

import androidx.compose.ui.graphics.Color

data class TeamConfig(
    val name: String,
    val colorHex: Long,
    val initialScore: Int
)

data class ScoreAction(
    val teamIndex: Int, // 1 for Team 1, 2 for Team 2
    val delta: Int,     // +1 or -1
    val prevScore1: Int,
    val prevScore2: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class MatchState(
    val team1: TeamConfig,
    val team2: TeamConfig,
    val score1: Int,
    val score2: Int,
    val step: Int = 1,
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true,
    val history: List<ScoreAction> = emptyList()
)

data class MatchPreset(
    val id: String,
    val title: String,
    val team1Name: String,
    val team1Color: Long,
    val team1Score: Int,
    val team2Name: String,
    val team2Color: Long,
    val team2Score: Int
)

object PresetData {
    val PRESETS = listOf(
        MatchPreset(
            id = "messi_ronaldo",
            title = "Messi vs Ronaldo",
            team1Name = "Messi",
            team1Color = 0xFFD81B60, // Vibrant Magenta/Pink like photo
            team1Score = 982,
            team2Name = "Ronaldo",
            team2Color = 0xFF76FF03, // Bright Neon Green like photo
            team2Score = 980
        ),
        MatchPreset(
            id = "sketch_red_yellow",
            title = "Red vs Yellow",
            team1Name = "Team Red",
            team1Color = 0xFFE52521, // Red from sketch
            team1Score = 888,
            team2Name = "Team Yellow",
            team2Color = 0xFFFFC400, // Yellow from sketch
            team2Score = 888
        ),
        MatchPreset(
            id = "blue_orange",
            title = "Blue vs Orange",
            team1Name = "Blue Thunder",
            team1Color = 0xFF0288D1,
            team1Score = 100,
            team2Name = "Orange Fire",
            team2Color = 0xFFFF6D00,
            team2Score = 100
        ),
        MatchPreset(
            id = "purple_cyan",
            title = "Violet vs Cyan",
            team1Name = "Cyber Violet",
            team1Color = 0xFF7C4DFF,
            team1Score = 0,
            team2Name = "Neon Cyan",
            team2Color = 0xFF00E5FF,
            team2Score = 0
        )
    )

    val COLOR_OPTIONS = listOf(
        0xFFD81B60, // Magenta Pink
        0xFFE52521, // Crimson Red
        0xFFFFC400, // Golden Yellow
        0xFF76FF03, // Neon Lime Green
        0xFF00E676, // Emerald Green
        0xFF0288D1, // Vivid Blue
        0xFF00E5FF, // Cyber Cyan
        0xFF7C4DFF, // Electric Purple
        0xFFFF6D00, // Blaze Orange
        0xFFEC407A, // Rose
        0xFF263238, // Slate Black
        0xFFFFFFFF  // Pure White
    )
}

/**
 * Calculates high contrast text color (either dark slate or bright white)
 * based on relative luminance of the background color.
 */
fun getContrastTextColor(colorHex: Long): Color {
    val color = Color(colorHex)
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.55f) {
        Color(0xFF111827) // Dark charcoal
    } else {
        Color(0xFFFFFFFF) // Pure white
    }
}
