package com.example

import androidx.compose.ui.graphics.Color
import com.example.model.PresetData
import com.example.model.getContrastTextColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testScoreIncrementAndDecrementCalculation() {
        var score = 888
        val step = 1

        // User's example: 888 down to 887, 886
        score -= step
        assertEquals(887, score)
        score -= step
        assertEquals(886, score)

        // User's example: up to 889, 890
        score += step * 4 // 886 + 4 = 890
        assertEquals(890, score)
    }

    @Test
    fun testContrastColor() {
        // Bright Yellow (from sketch) should yield dark text for maximum readability
        val yellowText = getContrastTextColor(0xFFFFC400)
        assertEquals(Color(0xFF111827), yellowText)

        // Neon Lime Green (from photo) should yield dark text
        val neonGreenText = getContrastTextColor(0xFF76FF03)
        assertEquals(Color(0xFF111827), neonGreenText)

        // Dark Magenta / Crimson Red should yield pure white text
        val magentaText = getContrastTextColor(0xFFD81B60)
        assertEquals(Color(0xFFFFFFFF), magentaText)

        val redText = getContrastTextColor(0xFFE52521)
        assertEquals(Color(0xFFFFFFFF), redText)
    }

    @Test
    fun testPresetsLoaded() {
        val presets = PresetData.PRESETS
        assertTrue(presets.isNotEmpty())
        val messiRonaldo = presets.find { it.id == "messi_ronaldo" }
        assertNotNull(messiRonaldo)
        assertEquals(982, messiRonaldo?.team1Score)
        assertEquals(980, messiRonaldo?.team2Score)
    }
}
