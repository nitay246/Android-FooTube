plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.advanced_system_programing"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.advanced_system_programing"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("com.google.android.material:material:1.9.0")

    // Room dependencies
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    // For Kotlin use kapt instead of annotationProcessor
    // kapt(libs.room.compiler)
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation ("com.auth0.android:jwtdecode:2.0.0")

    // Android annotations library
    implementation("androidx.annotation:annotation:1.8.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // Retrofit Converter for Gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // Gson library for JSON conversion
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation ("com.auth0.android:jwtdecode:2.0.0")

    implementation ("org.mongodb:mongodb-driver-sync:4.3.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
