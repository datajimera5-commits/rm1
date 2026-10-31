package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants

class FeedbackHelper(private val context: Context) {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playClickSound(isIncrement: Boolean) {
        try {
            toneGenerator?.let { tg ->
                val tone = if (isIncrement) {
                    ToneGenerator.TONE_PROP_BEEP
                } else {
                    ToneGenerator.TONE_PROP_ACK
                }
                tg.startTone(tone, 40)
            } ?: run {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                val sound = if (isIncrement) SoundEffectConstants.CLICK else SoundEffectConstants.NAVIGATION_LEFT
                audioManager?.playSoundEffect(sound)
            }
        } catch (_: Exception) {
            // Ignore if audio is unavailable
        }
    }

    @Suppress("DEPRECATION")
    fun vibrate(isIncrement: Boolean) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val duration = if (isIncrement) 30L else 45L
                        val amplitude = if (isIncrement) VibrationEffect.DEFAULT_AMPLITUDE else 180
                        v.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                    } else {
                        v.vibrate(if (isIncrement) 30L else 45L)
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration is restricted
        }
    }
}
