package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VsBadge(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vs_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag("vs_badge_container")
            .scale(pulseScale)
    ) {
        // Outer glow layer
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFFFFD700),
                    ambientColor = Color(0xFFFF0055)
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x66FFD700),
                            Color(0x33FF0055),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Metallic multi-ring border
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2A2D3A),
                            Color(0xFF13151F),
                            Color(0xFF0F1016)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0xFFFF007A),
                            Color(0xFF00F0FF),
                            Color(0xFFFFD700)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Inner beveled circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E2235),
                                Color(0xFF0A0C14)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0x66FFFFFF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Shadow for 3D metallic feel
                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    color = Color(0x99000000),
                    modifier = Modifier.offset(x = 1.dp, y = 1.5.dp)
                )
                // Foreground glowing metallic text
                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    color = Color(0xFFFFD700),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
