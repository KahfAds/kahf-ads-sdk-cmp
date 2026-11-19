package com.kahfads.cmp.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import com.kahfads.cmp.sdk.KahfAdsSdk
import com.kahfads.cmp.sdk.KahfAdsSdkConfig
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}

actual suspend fun initializeSdk() {
    KahfAdsSdk.initialize(
        config = KahfAdsSdkConfig(
            fallbackPublisherId = "muslims-day",
            language = "en",
            primaryColor = Color(0xFF1B7A3D),
        )
    )
}