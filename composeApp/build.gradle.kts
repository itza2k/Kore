import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    // Add compiler options to suppress expect/actual class warnings
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // SQLDelight JVM driver
            implementation(libs.sqldelight.driver.jvm)

            // Ktor CIO engine
            implementation(libs.ktor.client.cio)
        }
    }
}


sqldelight {
    databases {
        create("KoreDatabase") {
            packageName.set("com.itza2k.kore.db")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.itza2k.kore.MainKt"

        // Configure JVM options - simplified settings for better compatibility
        jvmArgs += listOf(
            "-Xmx512m", 
            "-Xms128m", 
            "-XX:+UseG1GC", 
            "-Djava.awt.headless=false",
            "-Dfile.encoding=UTF-8"
        )

        // Ensure JVM is properly bundled
        javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "com.itza2k.kore"
            packageVersion = "1.0.0"

            // Bundle JVM with the application - use a more compatible approach
            includeAllModules = false

            // Include only essential modules to reduce complexity
            modules("java.base")
            modules("java.sql")
            modules("java.desktop")
            modules("java.naming")
            modules("jdk.unsupported")
            modules("java.prefs")
            modules("java.xml")
            modules("java.logging")

            windows {
                // This controls the name in the Start Menu
                menuGroup = "Kore"

                // Ensure shortcuts are created
                shortcut = true
                menu = true

                // This is the name displayed in Add/Remove Programs
                upgradeUuid = "9007D6F4-70B0-46F7-A10C-E837D513F0CA"

                // Application name in various Windows interfaces
                dirChooser = true
                perUserInstall = false  // Install for all users to avoid permission issues

                // Ensure the executable is directly accessible
                exePackageVersion = packageVersion
                msiPackageVersion = packageVersion

                // Enable console window for debugging
                console = true

                // Add memory options directly to the executable
                installationPath = "Kore"
            }
        }
    }
}
