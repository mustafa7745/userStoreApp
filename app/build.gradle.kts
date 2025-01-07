plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "com.owaistelecom.telecom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.owaistelecom.telecom"
        minSdk = 23
        targetSdk = 34
        versionCode = 3
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.3"
//    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
//    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("io.coil-kt:coil-svg:2.4.0")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

//    implementation("com.google.android.libraries.places:4.1.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:6.2.0")



//    implementation(libs.play.services.location)
//    implementation(libs.audience.network.sdk)
//    implementation(libs.play.services.maps)
//    implementation(libs.maps.compose)
//    implementation (libs.android.maps.utils)
//    implementation ("com.google.android.libraries.places:places:4.0.0")
//////    implementation ("com.google.maps.android:android-maps-utils:3.8.2")
//    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
//    implementation("com.google.android.play:app-update:2.1.0")
//    implementation("com.google.android.play:review:2.0.1")
//    implementation("com.google.android.play:review-ktx:2.0.1")
}