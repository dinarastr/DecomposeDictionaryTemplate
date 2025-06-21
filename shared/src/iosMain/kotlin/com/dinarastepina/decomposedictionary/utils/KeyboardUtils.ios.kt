package com.dinarastepina.decomposedictionary.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.endEditing

/**
 * iOS implementation to hide the soft keyboard
 */
@OptIn(ExperimentalForeignApi::class)
actual fun hideKeyboard() {
    // Get the current window and end editing
    val application = UIApplication.sharedApplication
    val windows = application.windows
    
    // Find the key window (main window)
    val keyWindow = windows.firstOrNull { window ->
        (window as? UIWindow)?.isKeyWindow() == true
    } as? UIWindow
    
    // End editing on the key window to hide keyboard
    keyWindow?.endEditing(true)
}

/**
 * Composable function to get keyboard hiding functionality for iOS
 */
@Composable
actual fun rememberKeyboardController(): () -> Unit {
    return remember {
        { hideKeyboard() }
    }
}

/**
 * iOS-specific function to hide keyboard on a specific view
 */
@OptIn(ExperimentalForeignApi::class)
fun hideKeyboard(view: UIView) {
    view.endEditing(true)
}

/**
 * iOS-specific function to hide keyboard on all windows
 */
@OptIn(ExperimentalForeignApi::class)
fun hideKeyboardGlobally() {
    val application = UIApplication.sharedApplication
    val windows = application.windows
    
    // End editing on all windows
    windows.forEach { window ->
        (window as? UIWindow)?.endEditing(true)
    }
} 