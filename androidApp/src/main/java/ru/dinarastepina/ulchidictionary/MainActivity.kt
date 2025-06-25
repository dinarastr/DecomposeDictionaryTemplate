package ru.dinarastepina.ulchidictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.retainedComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import ru.dinarastepina.ulchidictionary.di.initKoin
import ru.dinarastepina.ulchidictionary.presentation.App
import ru.dinarastepina.ulchidictionary.presentation.components.root.RootComponent

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