package ru.dinarastepina.ulchidictionary.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Android implementation to hide the soft keyboard
 */
actual fun hideKeyboard() {
    // This is called from non-Compose context, so we can't access LocalContext here
    // The actual implementation is in rememberKeyboardController
}

/**
 * Composable function to get keyboard hiding functionality for Android
 */
@Composable
actual fun rememberKeyboardController(): () -> Unit {
    val context = LocalContext.current
    val view = LocalView.current
    
    return remember {
        {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

/**
 * Android-specific function to hide keyboard with context
 */
fun hideKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    
    // Try to hide keyboard using current focus
    if (context is Activity) {
        val currentFocus = context.currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } else {
            // Fallback: hide keyboard forcefully
            inputMethodManager.hideSoftInputFromWindow(
                context.window.decorView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
} 