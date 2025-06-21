package com.dinarastepina.decomposedictionary.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val koin by lazy { initKoin().koin }

fun initKoin(appDeclaration: KoinAppDeclaration? = null) = startKoin {
    appDeclaration?.invoke(this)
    modules(
        presentationModule,
        dataModule,
        audioModule,
        platformModule())
}