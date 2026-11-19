package com.kahfads.cmp.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kahfads.cmp.sdk.*
import com.kahfads.core.model.AdType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var isInitialized by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }
    
    // Initialize SDK on first composition
    LaunchedEffect(Unit) {
        try {
            initializeSdk()
            isInitialized = true
        } catch (e: Exception) {
            initError = e.message
        }
    }
    
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kahf Ads CMP Example") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            if (isInitialized) {
                AdExampleContent(
                    modifier = Modifier.padding(paddingValues)
                )
            } else if (initError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to initialize SDK: $initError",
                        color = Color.Red
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun AdExampleContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Small Banner Ad Example
        AdSection(
            title = "Small Banner Ad",
            adType = AdType.BANNER
        ) {
            BannerAdView(
                viewConfig = KahfAdsViewConfig(
                    screenName = "example_small_banner",
                    placementId = PlacementId.Network(
                        kahfAdsId = "pl_d3dda8442d7c4908",
                    ),
                    refreshIntervalInMillis = 3_000
                ),
                kahfAdsAdImpressionListener = createAdImpressionListener("Small Banner", "Kahf Ads"),
                adInfoConfig = AdInfoConfig(
                    showInfoIcon = true,
                    onWhyThisAdClicked = {
                        println("Why this ad clicked - Small Banner")
                    },
                    onCloseAdClicked = {
                        println("Close ad clicked - Small Banner")
                    }
                )
            )
        }
        
        // Medium Banner Ad Example
        AdSection(
            title = "Medium Banner Ad",
            adType = AdType.LARGE_BANNER
        ) {
            LargeBannerAdView(
                viewConfig = KahfAdsViewConfig(
                    screenName = "example_medium_banner",
                    placementId = PlacementId.Network(kahfAdsId = "pl_d3dda8442d7c4908"),
                    refreshIntervalInMillis = 30_000
                ),
                adInfoConfig = AdInfoConfig(
                    showInfoIcon = true,
                    onWhyThisAdClicked = {
                        println("Why this ad clicked - Medium Banner")
                    },
                    onCloseAdClicked = {
                        println("Close ad clicked - Medium Banner")
                    }
                )
            )
        }

        // Medium Banner Ad Example
        AdSection(
            title = "Large Banner Ad",
            adType = AdType.LARGE_BANNER
        ) {
            MediumRectangleAdView(
                viewConfig = KahfAdsViewConfig(
                    screenName = "example_medium_banner",
                    placementId = PlacementId.Network(kahfAdsId = "pl_d3dda8442d7c4908"),
                    refreshIntervalInMillis = 30_000
                ),
                adInfoConfig = AdInfoConfig(
                    showInfoIcon = true,
                    onWhyThisAdClicked = {
                        println("Why this ad clicked - Medium Banner")
                    },
                    onCloseAdClicked = {
                        println("Close ad clicked - Medium Banner")
                    }
                )
            )
        }
        
        // Large Banner Ad Example
        AdSection(
            title = "Large Banner Ad",
            adType = AdType.NATIVE
        ) {
            NativeAdView(
                viewConfig = KahfAdsViewConfig(
                    screenName = "example_large_banner",
                    placementId = PlacementId.Network(
                        kahfAdsId = "pl_ba8215e1fa3c4c98",
                    ),
                    refreshIntervalInMillis = 30_000
                ),
                kahfAdsAdImpressionListener = createAdImpressionListener("Large Banner", "Kahf Ads"),
                fallbackAdImpressionListener = createFallbackAdImpressionListener("Large Banner"),
                adInfoConfig = AdInfoConfig(
                    showInfoIcon = true,
                    onWhyThisAdClicked = {
                        println("Why this ad clicked - Large Banner")
                    },
                    onCloseAdClicked = {
                        println("Close ad clicked - Large Banner")
                    }
                )
            )
        }
        
        // Content placeholder
        repeat(5) { index ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Content Item ${index + 1}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This is sample content to demonstrate how ads integrate with your app content. " +
                                "The ads above will refresh automatically based on the configured interval.",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdSection(
    title: String,
    adType: AdType,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.05f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

fun createAdImpressionListener(adType: String, network: String): AdImpressionListener {
    return object : AdImpressionListener() {
        override fun onAdLoaded() {
            println("[$adType][$network] Ad loaded successfully")
        }
        
        override fun onAdFailedToLoad(message: String, cause: KahfAdsError?) {
            println("[$adType][$network] Ad failed to load: $message")
            cause?.let { println("[$adType][$network] Error: $it") }
        }
        
        override fun onAdClicked(urlToLoad: String): Boolean {
            println("[$adType][$network] Ad clicked: $urlToLoad")
            return false // Let SDK handle the URL
        }
    }
}

fun createFallbackAdImpressionListener(adType: String): FallbackAdImpressionListener {
    return object : FallbackAdImpressionListener() {
        override fun onFallbackAdLoaded(primaryAdError: KahfAdsError?, headline: String) {
            println("[$adType] Fallback ad loaded: $headline")
            primaryAdError?.let { println("[$adType] Primary ad error: $it") }
        }
        
        override fun onFallbackAdFailedToLoad(message: String, cause: KahfAdsError?) {
            println("[$adType] Fallback ad failed to load: $message")
            cause?.let { println("[$adType] Error: $it") }
        }
        
        override fun onFallbackAdClicked(urlToLoad: String): Boolean {
            println("[$adType] Fallback ad clicked: $urlToLoad")
            return false // Let SDK handle the URL
        }
    }
}

/**
 * Platform-specific SDK initialization
 */
expect suspend fun initializeSdk()