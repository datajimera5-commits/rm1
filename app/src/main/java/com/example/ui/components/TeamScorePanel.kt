package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.getContrastTextColor

@Composable
fun TeamScorePanel(
    teamIndex: Int,
    name: String,
    score: Int,
    colorHex: Long,
    step: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    isWinnerOrLeader: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val topHeaderPadding = if (isLandscape) 16.dp else 24.dp

    val backgroundColor = Color(colorHex)
    val textColor = getContrastTextColor(colorHex)

    // Font size scaling based on digits and orientation
    val scoreText = score.toString()
    val fontSize = if (isLandscape) {
        when {
            scoreText.length <= 2 -> 140.sp
            scoreText.length == 3 -> 116.sp
            scoreText.length == 4 -> 90.sp
            else -> 70.sp
        }
    } else {
        when {
            scoreText.length <= 2 -> 110.sp
            scoreText.length == 3 -> 90.sp
            scoreText.length == 4 -> 72.sp
            else -> 54.sp
        }
    }

    Box(
        modifier = modifier
            .background(backgroundColor)
            .testTag("team_${teamIndex}_panel")
            // Direct fullscreen tap detection: Top 50% = -1, Bottom 50% = +1
            .pointerInput(teamIndex, step) {
                detectTapGestures(
                    onTap = { offset ->
                        if (offset.y < size.height / 2f) {
                            onDecrement()
                        } else {
                            onIncrement()
                        }
                    }
                )
            }
    ) {
        // Team Name Header at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = topHeaderPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isWinnerOrLeader) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leader",
                        tint = if (textColor == Color.White) Color(0xFFFFD700) else Color(0xFFE65100),
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 6.dp)
                    )
                }
                Text(
                    text = name,
                    fontWeight = FontWeight.Black,
                    fontSize = if (isLandscape) 28.sp else 22.sp,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Giant Center Score display
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = score,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height / 2 } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height / 2 } + fadeOut()
                        )
                    } else {
                        (slideInVertically { height -> -height / 2 } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height / 2 } + fadeOut()
                        )
                    }
                },
                label = "score_anim"
            ) { targetScore ->
                Text(
                    text = targetScore.toString(),
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    letterSpacing = (-2).sp
                )
            }
        }
    }
}
