import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            
            // Optimize for memory usage and suppress warnings
            freeCompilerArgs += listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.ExperimentalMultiplatform",
                "-Xexpect-actual-classes"
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)

            // MVIKotlin dependencies
            implementation(libs.mvikotlin)
            implementation(libs.mvikotlin.main)
            implementation(libs.mvikotlin.logging)
            implementation(libs.mvikotlin.timetravel)
            implementation(libs.mvikotlin.extensions.coroutines)
            implementation(libs.essenty.lifecycle.coroutines)

            // Koin dependencies
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Room database
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Paging dependencies
            implementation(libs.paging.common)
            implementation(libs.paging.compose.common)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.androidx.datastore.preferences)
        }
        
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
        }
        
        iosMain.dependencies {
            // iOS specific dependencies if needed
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "ru.dinarastepina.ulchidictionary"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Kotlin/Native memory optimizations
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xexpect-actual-classes"
        )
    }
}


