plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.busfare_splitterv2"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.busfare_splitterv2"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // User added dependancies
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.airbnb.android:lottie:6.3.0")

    // backend connection dependancies
    // Retrofit for HTTP calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp for logging (optional but useful)
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    // Coroutines for async calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
}