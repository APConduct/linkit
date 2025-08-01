import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Depend on the app module to use the calculator engine
    implementation(project(":app"))

    // Compose Multiplatform dependencies
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)

    // Optional: Material Icons
    implementation(compose.materialIconsExtended)
}

compose.desktop {
    application {
        mainClass = "org.linkit.gui.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LinkIt Calculator"
            packageVersion = "1.0.0"
            description = "Advanced Scientific Calculator"
            copyright = "© 2024 LinkIt Calculator"
            vendor = "LinkIt"

            macOS {
                bundleID = "org.linkit.calculator"
                iconFile.set(project.file("src/main/resources/icon.icns"))
            }

            windows {
                iconFile.set(project.file("src/main/resources/icon.ico"))
                menuGroup = "LinkIt"
                upgradeUuid = "b2c8b6e4-8c4e-4b5a-9c1d-3f2e1a0b9c8d"
            }

            linux { iconFile.set(project.file("src/main/resources/icon.png")) }
        }
    }
}

// Apply a specific Java toolchain to match the app module
java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }
