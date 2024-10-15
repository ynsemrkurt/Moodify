import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("gradle.properties")))
}

android {
    namespace = "com.example.facedetection"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.facedetection"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "REDIRECT_URI", properties.getProperty("REDIRECT_URI"))
        buildConfigField("String", "CLIENT_ID", properties.getProperty("CLIENT_ID"))
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
        buildConfigField("String", "AD_UNIT_ID", properties.getProperty("AD_UNIT_ID"))
        resValue("string", "AD_VALUE", properties.getProperty("AD_VALUE"))
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.face.detection)
    implementation(libs.lottie)
    implementation(libs.auth)
    implementation(libs.androidx.browser)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.glide)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.circleimageview)
    implementation(libs.carouselrecyclerview)
    implementation(libs.play.services.ads)
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation(libs.shimmer)
}