plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "ru.dinarastepina.ulchidictionary"
    compileSdk = 35
    defaultConfig {
        applicationId = "ru.dinarastepina.ulchidictionary"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "2.1"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    
    // Add Decompose dependencies for Android
    implementation(libs.decompose)
    implementation(libs.decompose.extensions.android)
    implementation(libs.decompose.extensions.compose)
    
    // Add essenty lifecycle for manual lifecycle management
    implementation(libs.essenty.lifecycle)
    
    // Add Koin for Android
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.androidx.core.splashscreen)
    
    debugImplementation(libs.compose.ui.tooling)
}