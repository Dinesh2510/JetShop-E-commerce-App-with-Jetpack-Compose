plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}


android {
    namespace = "com.compose.jetshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.compose.jetshop"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    implementation(platform(libs.firebase.bom))

    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)

    // Lifecycle & ViewModel
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)

    // Coil
    implementation(libs.coil.compose)

    // Navigation
    implementation(libs.navigation.compose)

    // DataStore
    implementation(libs.datastore.preferences)

    // Firebase
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)

    // Lottie
    implementation(libs.lottie.compose)

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.places)

    implementation(libs.checkout)

}