package com.kahfads.cmp.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.kahfads.cmp.sdk.KahfAdsSdk
import com.kahfads.cmp.sdk.KahfAdsSdkConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppWrapper()
        }
    }
}

@Composable
fun AndroidAppWrapper() {
    val context = LocalContext.current
    var isInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (!isInitialized) {
            KahfAdsSdk.initialize(
                config = KahfAdsSdkConfig(
                    fallbackPublisherId = "muslims-day",
                    language = "bn",
                    primaryColor = Color(0xFF1B7A3D),
                ),
                context = context.applicationContext
            )
            isInitialized = true
        }
    }
    
    if (isInitialized) {
        App()
    }
}

actual suspend fun initializeSdk() {
    // SDK is initialized in AndroidAppWrapper for Android
    // This function is kept for iOS compatibility
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}