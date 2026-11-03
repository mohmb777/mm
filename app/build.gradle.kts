plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.astra.telegramcopier"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.astra.telegramcopier"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
    }
}
