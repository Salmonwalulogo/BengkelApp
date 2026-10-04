import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

val signingProperties = Properties().apply {
    val propertiesFile = rootProject.file("key.properties")
    if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { load(it) }
    }
}
val releaseStoreFile: String? = System.getenv("BENGKEL_KEYSTORE_PATH")
    ?: signingProperties.getProperty("storeFile")
val releaseStorePassword: String? = System.getenv("BENGKEL_KEYSTORE_PASSWORD")
    ?: signingProperties.getProperty("storePassword")
val releaseKeyAlias: String? = System.getenv("BENGKEL_KEY_ALIAS")
    ?: signingProperties.getProperty("keyAlias")
val releaseKeyPassword: String? = System.getenv("BENGKEL_KEY_PASSWORD")
    ?: signingProperties.getProperty("keyPassword")
val isReleaseBuildRequested = gradle.startParameter.taskNames.any {
    it.contains("release", ignoreCase = true)
}

if (isReleaseBuildRequested) {
    check(
        listOf(
            releaseStoreFile,
            releaseStorePassword,
            releaseKeyAlias,
            releaseKeyPassword,
        ).all { !it.isNullOrBlank() },
    ) {
        "Release signing is not configured. Set BENGKEL_KEYSTORE_PATH, BENGKEL_KEYSTORE_PASSWORD, BENGKEL_KEY_ALIAS, and BENGKEL_KEY_PASSWORD or provide android/key.properties."
    }
}

android {
    namespace = "com.example.bengkel_app"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    defaultConfig {
        // Replace this example application ID before publishing to an app store.
        applicationId = "com.example.bengkel_app"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    signingConfigs {
        create("release") {
            storeFile = releaseStoreFile?.let { file(it) }
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

flutter {
    source = "../.."
}
