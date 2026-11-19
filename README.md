# Kahf Ads Compose Multiplatform SDK

Show KahfAds banner and native ads from shared Compose code on Android and iOS.

## Features

- **One UI for Android and iOS**: Compose Multiplatform ad views
- **Four formats**: banner, large banner, medium rectangle, and native ads
- **Kahf Ads network**: ads served by Kahf Ads
- **Automatic fallback**: if Kahf Ads can't serve an ad, in-house ads are shown
- **Auto-refresh**: configurable interval; pauses when the app is in the background, the ad is off-screen, or the info dialog is open
- **Impression and click tracking**: handled by the SDK
- **Ad info overlay**: optional "Why this ad" / "Close ad" dialog
- **Theming**: dark mode support and your brand color on call-to-action buttons

## Requirements

- **Android**: minSdk 21, JVM target 11
- **iOS**: iOS 13.0+ (`iosArm64`, `iosSimulatorArm64`, `iosX64`)
- **Compose Multiplatform** in your shared module

## Installation

No credentials are needed. The SDK is served from a public Maven repository hosted in this repo
(group `co.uk.kahf.kahfAds.cmp`) and, for iOS, as a Swift package.

### Android / Kotlin Multiplatform

```kotlin
// settings.gradle.kts -> dependencyResolutionManagement.repositories
maven {
    url = uri("https://OWNER.github.io/kahf-ads-sdk-cmp/maven")
    content { includeGroupAndSubgroups("co.uk.kahf.kahfAds") }
}

// shared module build.gradle.kts -> commonMain.dependencies
implementation("co.uk.kahf.kahfAds.cmp:kahf-ads-cmp:0.2.28")
```

The `INTERNET` and `ACCESS_NETWORK_STATE` permissions are merged into your Android manifest
automatically.

### iOS (Swift Package Manager)

In Xcode choose **File > Add Package Dependencies** and enter
`https://github.com/OWNER/kahf-ads-sdk-cmp`, or add it to `Package.swift`:

```swift
.package(url: "https://github.com/OWNER/kahf-ads-sdk-cmp", from: "0.2.28")
```

If your iOS app is a Compose Multiplatform shared module, use the Gradle dependency above instead.

## Example app

[`example/`](example) is a runnable Compose Multiplatform app that uses the released SDK exactly as
you would. Open it in Android Studio, or run `./gradlew :app:assembleDebug` inside `example/`.

## 1. Initialize the SDK

Call `initialize` **once**, before showing any ad view.

### Android

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KahfAdsSdk.initialize(
            config = KahfAdsSdkConfig(fallbackPublisherId = "your-publisher-id"),
            context = this,
        )
    }
}
```

### iOS

Initialize from your shared module's iOS entry point, then host your Compose UI as usual:

```kotlin
// shared/src/iosMain/kotlin/MainViewController.kt
fun MainViewController(): UIViewController {
    KahfAdsSdk.initialize(
        config = KahfAdsSdkConfig(fallbackPublisherId = "your-publisher-id"),
    )
    return ComposeUIViewController { App() }
}
```

```swift
// iOS app (SwiftUI)
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### `KahfAdsSdkConfig`

| Field | Default | Description |
|---|---|---|
| `fallbackPublisherId` | required | Your publisher id, used to serve in-house ads |
| `language` | `null` | Language code (`"en"`, `"bn"`, `"ar"`, …) for localized and language-targeted ads. When `null`, the SDK infers it from the device timezone |
| `primaryColor` | `null` | Your brand color, used for the call-to-action button and the in-app browser bar |
| `isDebug` | `false` | `true` in debug builds: Kahf Ads serves test ads only, and SDK logging is turned on. See [Debug builds](#debug-builds) |

### Debug builds

Pass your app's build type as `isDebug`, so development builds get test ads and release builds get real ones:

```kotlin
// Android — requires buildFeatures { buildConfig = true } in your app module
KahfAdsSdk.initialize(
    config = KahfAdsSdkConfig(
        fallbackPublisherId = "your-publisher-id",
        isDebug = BuildConfig.DEBUG,
    ),
    context = this,
)
```

```kotlin
// iOS — shared/src/iosMain
@OptIn(ExperimentalNativeApi::class)
fun MainViewController(): UIViewController {
    KahfAdsSdk.initialize(
        config = KahfAdsSdkConfig(
            fallbackPublisherId = "your-publisher-id",
            isDebug = Platform.isDebugBinary, // true when Xcode builds the Debug configuration
        ),
    )
    return ComposeUIViewController { App() }
}
```

With `isDebug = true`:

- **Kahf Ads** returns test ads only. If the placement has no test ads, the slot gets no fill and falls back as usual.
- **In-house ads are unchanged.** They can still serve real ads in a debug build, so avoid
  clicking them during testing.
- **SDK logs** are printed to Logcat / the Xcode console.

> Never hard-code `isDebug = true`. A release build with it set shows only test ads, which earn nothing.

## 2. Choose a placement

Each ad slot has a `PlacementId` that tells the SDK which source may fill it.

```kotlin
// Kahf Ads. Without an id, the slot goes straight to in-house ads.
PlacementId.Network(kahfAdsId = "pl_your-kahf-ads-placement-id")

PlacementId.Inhouse("your-publisher-id")     // in-house ads only
```

Order in which sources are tried:

| Placement | Order |
|---|---|
| `Network(kahfAdsId = …)` | Kahf Ads, then in-house |
| `Network()` (no id) | in-house only |
| `Inhouse(id)` | in-house only |

## 3. Show an ad

```kotlin
@Composable
fun HomeScreen() {
    BannerAdView(                       // 320x50
        viewConfig = KahfAdsViewConfig(
            screenName = "home_top",
            placementId = PlacementId.Network(kahfAdsId = "pl_…"),
        ),
    )

    LargeBannerAdView(viewConfig = …)   // 320x100
    MediumRectangleAdView(viewConfig = …) // 300x250
    NativeAdView(viewConfig = …)        // headline, description, logo, CTA
}
```

Every ad view takes the same parameters:

| Parameter | Description |
|---|---|
| `viewConfig: KahfAdsViewConfig` | Placement and refresh settings (below) |
| `kahfAdsAdImpressionListener: AdImpressionListener?` | Events for ads from Kahf Ads |
| `fallbackAdImpressionListener: FallbackAdImpressionListener?` | Events for in-house ads |
| `adInfoConfig: AdInfoConfig?` | Optional info icon and dialog |

`NativeAdView` and `MediumRectangleAdView` also take `backgroundColor: Color?` for the section
below the image (logo, text and button). By default it is white, or `#1F3641` when the device is
in dark mode. When you set it, the text turns black or white so it stays readable:

```kotlin
NativeAdView(
    viewConfig = …,
    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
)
```

### `KahfAdsViewConfig`

| Field | Default | Description |
|---|---|---|
| `screenName` | required | Name of the slot. Ads are cached per screen name + placement + ad type, so give separate slots separate names |
| `placementId` | required | See [Choose a placement](#2-choose-a-placement) |
| `refreshIntervalInMillis` | `10_000` | Time between automatic refreshes. `0` or less turns refresh off |

The view stays collapsed until an ad has loaded and expands with an animation when it appears.

## 4. Listen to ad events

```kotlin
BannerAdView(
    viewConfig = …,
    kahfAdsAdImpressionListener = object : AdImpressionListener() {
        override fun onAdLoaded() { log("Kahf Ads ad shown") }
    },
    fallbackAdImpressionListener = object : FallbackAdImpressionListener() {
        override fun onFallbackAdLoaded(primaryAdError: KahfAdsError?, headline: String) { log("In-house ad: $headline") }
        override fun onFallbackAdFailedToLoad(message: String, cause: KahfAdsError?) { log("No ad at all: $message") }
    },
)
```

All listener methods are optional, so override only the ones you need.

**Which listener is called:**

| Event | Listener |
|---|---|
| Ad loaded (`onAdLoaded`) | Kahf Ads listener |
| Ad clicked (`onAdClicked`) | The network that served the ad |
| No network ad (`onAdFailedToLoad`) | Kahf Ads listener, for a `Network` placement with an id |
| Ad image failed to load (`onAdFailedToLoad`) | The network that served the ad |
| In-house ad loaded / clicked / failed | `fallbackAdImpressionListener` (`onFallbackAdLoaded`, `onFallbackAdClicked`, `onFallbackAdFailedToLoad`) |

`onAdLoaded` and `onFallbackAdLoaded` fire when the ad image is displayed, which is also when the
impression is reported. They fire again after each refresh.

### Handling clicks

`onAdClicked(urlToLoad)` / `onFallbackAdClicked(urlToLoad)` return:
- `false` (default): the SDK opens the URL in Chrome Custom Tabs (Android) or `SFSafariViewController` (iOS),
  or in the SDK's in-app browser when the ad asks for it.
- `true`: your app handles the URL and the SDK does nothing.

### Errors

`cause` is a `KahfAdsError`:

| Type | Meaning |
|---|---|
| `NoFillError` | The network had no ad for this request |
| `NetworkError` | Connectivity problem |
| `InvalidRequest` | The ad request failed validation |
| `ConfigurationError` | Invalid SDK configuration |
| `FallbackAdError` | In-house fallback failed; its `cause` holds the original network error |
| `InternalError` | Unexpected SDK failure |
| `UnknownError` | Anything else, including timeouts, server errors, and unreadable responses |

## 5. Ad info overlay (optional)

```kotlin
MediumRectangleAdView(
    viewConfig = …,
    adInfoConfig = AdInfoConfig(
        showInfoIcon = true,
        onInfoButtonClicked = { },
        onWhyThisAdClicked = { /* open your ad policy page */ },
        onCloseAdClicked = { /* hide the slot */ },
    ),
)
```

| Field | Description |
|---|---|
| `showInfoIcon` | Show the ⓘ icon on the ad (default `false`) |
| `onInfoButtonClicked` | Icon tapped; the dialog opens and refresh pauses while it is open |
| `onWhyThisAdClicked` | "Why this ad" tapped |
| `onCloseAdClicked` | "Close ad" tapped. The SDK closes the dialog but keeps showing the ad; hide the slot yourself if needed |

## Upgrading

### EPOM removed

EPOM support is gone. `PlacementId.Epom`, `PlacementId.Network(epomId = …)`,
`KahfAdsSdkConfig.epomTrafficPercentage` and the `epomAdImpressionListener` parameter no longer exist.
Move EPOM slots to `PlacementId.Network(kahfAdsId = "pl_…")` and use `kahfAdsAdImpressionListener`.

### To 0.2.23

All additions are optional; existing code compiles unchanged.

- New `KahfAdsSdkConfig.isDebug`. Set it from your build type to get Kahf Ads test ads and SDK logs in
  development. See [Debug builds](#debug-builds).
- `NativeAdView` and `MediumRectangleAdView` take a new `backgroundColor` for the section below the image.
- Kahf Ads requests now carry your app language (`KahfAdsSdkConfig.language`, or the one inferred from
  the device timezone), so language-targeted Kahf Ads campaigns can reach your users.
- Kahf Ads are now served from `serve.kahfads.com`. Earlier releases called `ad.kahf.to`, where current
  placement ids return `invalid_placement`, so Kahf Ads slots fell back to in-house. Upgrade to get
  Kahf Ads fill.

### To the release with Kahf Ads (after 0.2.19)

- `adImpressionListener` is replaced by **`kahfAdsAdImpressionListener`**.
- To serve Kahf Ads, switch a slot to `PlacementId.Network(kahfAdsId = …)`.
- Ads from a `PlacementId.Inhouse` placement are now reported through `fallbackAdImpressionListener`.

## Ad formats

| View | Ad type | Size (dp) |
|---|---|---|
| `BannerAdView` | `AdType.BANNER` | 320×50 |
| `LargeBannerAdView` | `AdType.LARGE_BANNER` | 320×100 |
| `MediumRectangleAdView` | `AdType.MEDIUM_RECTANGLE` | 300×250 |
| `NativeAdView` | `AdType.NATIVE` | 16:9 image plus headline, logo, and CTA |

Image ads whose image fails to load are shown in the native layout when the ad includes text.

## License

Copyright © Kahf Ads. All rights reserved.
