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
            url: "https://github.com/KahfAds/kahf-ads-sdk-cmp/releases/download/0.2.28/KahfAdsCmp-0.2.28.zip",
            checksum: "dd5dc54506fb6a7f723e9840386ce15e8f6c662db149c86659bb40e328d9a60a"
        )
    ]
)
