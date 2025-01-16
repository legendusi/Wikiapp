import org.gradle.kotlin.dsl.kapt
import org.gradle.kotlin.dsl.libs

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.wikiapp"
    compileSdk = 35

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()  // Use the reference to the Kotlin compiler extension version from versions.toml
    }

    defaultConfig {
        applicationId = "com.example.wikiapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM (Bill of Materials) for consistent Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // Jetpack Compose dependencies
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // Compose Hilt integration

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Accompanist for additional Compose features
    implementation(libs.accompanist.swiperefresh)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.jsoup)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase Authentication library
    implementation(libs.firebase.auth.ktx)

    // Debugging tools for Compose
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)

    implementation(libs.material.icons.extended)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.runtime.livedata)

    implementation (libs.datastore.preferences)

}

// Allow references to generated code for Hilt
kapt {
    correctErrorTypes = true
}
