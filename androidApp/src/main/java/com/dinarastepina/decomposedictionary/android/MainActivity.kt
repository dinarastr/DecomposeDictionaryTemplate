package com.dinarastepina.decomposedictionary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.retainedComponent
import com.dinarastepina.decomposedictionary.di.initKoin
import com.dinarastepina.decomposedictionary.presentation.App
import com.dinarastepina.decomposedictionary.presentation.components.root.RootComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (GlobalContext.getOrNull() == null) {
            initKoin {
                androidContext(this@MainActivity)
            }
        }
        
        val rootComponentFactory: RootComponent.Factory by GlobalContext.get().inject()
        val root = retainedComponent { context ->
            rootComponentFactory(context)
        }
        setContent {
            App(rootComponent = root)
        }
    }
}