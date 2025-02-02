plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id ("androidx.navigation.safeargs")


}

android {
    namespace = "com.example.slaughterhouse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.slaughterhouse"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding {
        enable= true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.8.0")
   // implementation("com.google.android.ads:mediation-test-suite:3.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.6.1")


    //Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // Coil
    implementation ("io.coil-kt:coil-compose:2.2.2")
    //Desugaring
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.1.7")
    implementation ("androidx.room:room-runtime:2.4.0")
    kapt ("androidx.room:room-compiler:2.4.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    implementation ("androidx.camera:camera-core:1.4.0-rc01")
    implementation ("androidx.camera:camera-camera2:1.4.0-rc01")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-rc01")
    implementation ("androidx.camera:camera-view:1.4.0-rc01")
    implementation ("androidx.camera:camera-extensions:1.4.0-rc01")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.zxing:core:3.3.3")

}