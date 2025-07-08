plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.demoapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.demoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true // Enable MultiDex if necessary
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Libraries
    implementation("androidx.appcompat:appcompat:1.5.0") // Ensure AppCompat dependency is included
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM for versioning
    implementation(platform(libs.androidx.compose.bom)) // BOM for Compose versioning
    implementation("androidx.compose.material:material:1.5.0") // Automatically uses the version from BOM
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation("androidx.compose.ui:ui-tooling:1.3.0") // Tooling for preview and debugging
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("com.google.accompanist:accompanist-pager:0.25.0")

    // Firebase & Google Services
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("com.google.gms:google-services:4.3.10")

    // Retrofit and Gson for network calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.8")

    // SQLite database for storing data
    implementation("androidx.sqlite:sqlite:2.2.0")

    // Coil for image loading
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("com.google.zxing:core:3.5.2")
    implementation("org.web3j:core:5.0.0") // Web3J for Ethereum-like chains
    implementation("org.web3j:crypto:5.0.0")


    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.google.firebase.firestore.ktx)

    // Unit and UI Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Use BOM for testing
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Web3J for blockchain integration
    implementation("org.web3j:core:5.0.0")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.6.0") // Keep the latest version
}
