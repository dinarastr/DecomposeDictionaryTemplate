package com.dinarastepina.decomposedictionary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.retainedComponent
import com.dinarastepina.decomposedictionary.di.koin
import com.dinarastepina.decomposedictionary.presentation.App
import com.dinarastepina.decomposedictionary.presentation.components.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val rootComponentFactory: RootComponent.Factory by koin.inject()
        val roo = retainedComponent { context ->
            rootComponentFactory(context)
        }
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(rootComponent = roo)
                }
            }
        }
    }
}