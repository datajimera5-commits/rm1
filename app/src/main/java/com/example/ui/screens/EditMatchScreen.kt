package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchState
import com.example.model.PresetData
import com.example.model.getContrastTextColor
import com.example.ui.components.ColorPickerRow

@Composable
fun EditMatchScreen(
    matchState: MatchState,
    onSaveAndStart: (
        team1Name: String,
        team1Color: Long,
        team1Start: Int,
        team2Name: String,
        team2Color: Long,
        team2Start: Int,
        step: Int
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    var team1Name by remember { mutableStateOf(matchState.team1.name) }
    var team1Color by remember { mutableLongStateOf(matchState.team1.colorHex) }
    var team1StartStr by remember { mutableStateOf(matchState.team1.initialScore.toString()) }

    var team2Name by remember { mutableStateOf(matchState.team2.name) }
    var team2Color by remember { mutableLongStateOf(matchState.team2.colorHex) }
    var team2StartStr by remember { mutableStateOf(matchState.team2.initialScore.toString()) }

    var selectedStep by remember { mutableIntStateOf(matchState.step) }

    val darkBackground = Color(0xFF0F111A)
    val cardBackground = Color(0xFF1A1D2B)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(darkBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("edit_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Edit Teams & Match",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Customize names, colors, and initial scores",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Quick Swap button
        item {
            OutlinedButton(
                onClick = {
                    val tempName = team1Name
                    val tempColor = team1Color
                    val tempScore = team1StartStr

                    team1Name = team2Name
                    team1Color = team2Color
                    team1StartStr = team2StartStr

                    team2Name = tempName
                    team2Color = tempColor
                    team2StartStr = tempScore
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_swap_sides_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300E5FF))
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Swap Team 1 ⇄ Team 2",
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // TEAM 1 CONFIG CARD
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(team1Color).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(team1Color))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TEAM 1 (LEFT SIDE)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Live preview badge
                        Surface(
                            color = Color(team1Color),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = team1Name.ifEmpty { "Team 1" },
                                color = getContrastTextColor(team1Color),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Team 1 Name Field
                    OutlinedTextField(
                        value = team1Name,
                        onValueChange = { team1Name = it },
                        label = { Text("Team 1 Name (e.g. Messi, Red)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(team1Color),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(team1Color),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team1_name_input")
                    )

                    // Team 1 Initial Starting Score
                    OutlinedTextField(
                        value = team1StartStr,
                        onValueChange = { if (it.all { char -> char.isDigit() }) team1StartStr = it },
                        label = { Text("Starting Score (e.g. 789, 888, 0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(team1Color),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(team1Color),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team1_score_input")
                    )

                    // Team 1 Color Palette
                    Text(
                        text = "Choose Team 1 Color:",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    ColorPickerRow(
                        selectedColor = team1Color,
                        onColorSelected = { team1Color = it }
                    )
                }
            }
        }

        // TEAM 2 CONFIG CARD
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(team2Color).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(team2Color))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TEAM 2 (RIGHT SIDE)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }

                        // Live preview badge
                        Surface(
                            color = Color(team2Color),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = team2Name.ifEmpty { "Team 2" },
                                color = getContrastTextColor(team2Color),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Team 2 Name Field
                    OutlinedTextField(
                        value = team2Name,
                        onValueChange = { team2Name = it },
                        label = { Text("Team 2 Name (e.g. Ronaldo, Yellow)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(team2Color),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(team2Color),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team2_name_input")
                    )

                    // Team 2 Initial Starting Score
                    OutlinedTextField(
                        value = team2StartStr,
                        onValueChange = { if (it.all { char -> char.isDigit() }) team2StartStr = it },
                        label = { Text("Starting Score (e.g. 789, 888, 0)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(team2Color),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(team2Color),
                            unfocusedLabelColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team2_score_input")
                    )

                    // Team 2 Color Palette
                    Text(
                        text = "Choose Team 2 Color:",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    ColorPickerRow(
                        selectedColor = team2Color,
                        onColorSelected = { team2Color = it }
                    )
                }
            }
        }

        // SCORE INCREMENT STEP
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Score Step Per Tap:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 5, 10).forEach { stepVal ->
                            FilterChip(
                                selected = selectedStep == stepVal,
                                onClick = { selectedStep = stepVal },
                                label = { Text("+$stepVal") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF00E5FF),
                                    selectedLabelColor = Color(0xFF0F111A),
                                    containerColor = Color(0xFF262A3B),
                                    labelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // SAVE & START MATCH BUTTON
        item {
            Button(
                onClick = {
                    val s1 = team1StartStr.toIntOrNull() ?: 0
                    val s2 = team2StartStr.toIntOrNull() ?: 0
                    onSaveAndStart(
                        team1Name,
                        team1Color,
                        s1,
                        team2Name,
                        team2Color,
                        s2,
                        selectedStep
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_and_start_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E676)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF0A2E16),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE & LAUNCH SCOREBOARD",
                        color = Color(0xFF0A2E16),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
