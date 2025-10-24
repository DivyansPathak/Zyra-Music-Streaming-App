package com.zyra.music.zyra.presentation.newPlayer.component

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

enum class ReorderHapticFeedbackType {
    START,
    MOVE,
    END,
}

open class ReorderHapticFeedback {
    open fun performHapticFeedback(type: ReorderHapticFeedbackType) {
        // no-op
    }
}

@Composable
 fun rememberReorderHapticFeedback(): ReorderHapticFeedback{
    val view = LocalView.current

    // Use remember {} so you create this object only once
    return remember(view) {
        // Create an anonymous object that inherits from ReorderHapticFeedback
        object : ReorderHapticFeedback() {

            // Override the function with the REAL Android haptic feedback code
            override fun performHapticFeedback(type: ReorderHapticFeedbackType) {
                val feedbackConstant = when (type) {
                    ReorderHapticFeedbackType.START -> HapticFeedbackConstants.LONG_PRESS
                    ReorderHapticFeedbackType.MOVE -> HapticFeedbackConstants.VIRTUAL_KEY
                    ReorderHapticFeedbackType.END -> HapticFeedbackConstants.KEYBOARD_TAP
                }
                // Trigger the actual haptic feedback
                view.performHapticFeedback(feedbackConstant)
            }
        }
    }
 }