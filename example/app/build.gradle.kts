import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KahfAdsCmpExample"
            isStatic = true
        }
    }
    
    
    sourceSets {
        commonMain.dependencies {
            implementation("co.uk.kahf.kahfAds.cmp:kahf-ads-cmp:${providers.gradleProperty("kahfAdsVersion").get()}")
            
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.animation)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            
            implementation(libs.kotlinx.coroutines.core)
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
        }
        
    }
}

android {
    namespace = "com.kahfads.cmp.example"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.kahfads.cmp.example"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

