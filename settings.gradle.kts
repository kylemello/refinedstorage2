dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/refinedmods/refinedarchitect")
            credentials {
                username = "anything"
                password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
            }
        }
    }
    versionCatalogs {
        create("libs") {
            from("com.refinedmods.refinedarchitect:refinedarchitect-versioning:0.16.6")
        }
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.pkg.github.com/refinedmods/refinedarchitect")
            credentials {
                username = "anything"
                password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
            }
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }
    }
    plugins {
        id("refinedarchitect.root").version("0.16.6")
        id("refinedarchitect.base").version("0.16.6")
        id("refinedarchitect.common").version("0.16.6")
        id("refinedarchitect.neoforge").version("0.16.6")
        id("refinedarchitect.fabric").version("0.16.6")
    }
}

rootProject.name = "refinedstorage"
include("refinedstorage-core-api")
include("refinedstorage-resource-api")
include("refinedstorage-storage-api")
include("refinedstorage-query-parser")
include("refinedstorage-grid-api")
include("refinedstorage-network-api")
include("refinedstorage-network")
include("refinedstorage-platform-api")
include("refinedstorage-platform-fabric")
include("refinedstorage-platform-neoforge")
include("refinedstorage-platform-common")
include("refinedstorage-network-test")
