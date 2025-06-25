package ru.dinarastepina.ulchidictionary.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Hide the soft keyboard on the current platform
 */
expect fun hideKeyboard()

/**
 * Get a keyboard controller function for the current platform
 */
@Composable
expect fun rememberKeyboardController(): () -> Unit

/**
 * Compose modifier that hides the keyboard when tapped outside of focused elements
 */
fun Modifier.hideKeyboardOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    val keyboardController = rememberKeyboardController()
    val interactionSource = remember { MutableInteractionSource() }
    
    this.clickable(
        interactionSource = interactionSource,
        indication = null // No ripple effect
    ) {
        focusManager.clearFocus()
        keyboardController()
    }
}

/**
 * Compose modifier that hides the keyboard when tapped, but is gentler and less likely to interfere with TextField focus
 */
fun Modifier.hideKeyboardOnTapGentle(): Modifier = composed {
    val keyboardController = rememberKeyboardController()
    val interactionSource = remember { MutableInteractionSource() }
    
    this.clickable(
        interactionSource = interactionSource,
        indication = null // No ripple effect
    ) {
        // Only hide keyboard without clearing focus
        keyboardController()
    }
} 