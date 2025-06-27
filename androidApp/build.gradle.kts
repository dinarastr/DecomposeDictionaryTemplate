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
    signingConfigs {
        create("release") {
            storeFile = file("ulchidic.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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