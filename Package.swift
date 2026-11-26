// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "KahfAdsCmp",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "KahfAdsCmp", targets: ["KahfAdsCmp"])
    ],
    targets: [
        .binaryTarget(
            name: "KahfAdsCmp",
            url: "https://github.com/KahfAds/kahf-ads-sdk-cmp/releases/download/0.2.29/KahfAdsCmp-0.2.29.zip",
            checksum: "a6b4b7fcdbaeb1d7107db218746c4e9babeddebb22e1e2c5ea74c42bd30a4559"
        )
    ]
)
