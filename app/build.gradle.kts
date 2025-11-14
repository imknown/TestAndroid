plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

private val buildVersion = libs.versions

android {
    namespace = "net.imknown.testandroid"

    compileSdk {
        version = release(buildVersion.compileSdk.get().toInt()) {
            minorApiLevel = buildVersion.compileSdkMinor.get().toInt()
        }
    }
    buildToolsVersion = buildVersion.buildTools.get()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        targetSdk = libs.versions.targetSdk.get().toInt()

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.material)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)
}