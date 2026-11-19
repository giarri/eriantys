plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val clientConfig: Map<String, String> by rootProject.extra
fun cfg(key: String, fallback: String) =
    (findProperty(key) as String?) ?: clientConfig[key] ?: fallback

android {
    namespace = "it.polimi.ingsw.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "it.polimi.ingsw.android"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "DEFAULT_SERVER_HOST", "\"${cfg("server.host", cfg("android.server.host", "10.0.2.2"))}\"")
        buildConfigField("int", "DEFAULT_SERVER_PORT", cfg("server.port", "12345"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(platform(libs.compose.bom))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
}
