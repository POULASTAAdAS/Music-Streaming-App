plugins {
    alias(libs.plugins.kyoku.android.library)
    alias(libs.plugins.kyoku.okhttp.library)

    alias(libs.plugins.kyoku.android.dagger)
}

android {
    namespace = "com.poulastaa.auth.data"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)

    implementation(projects.core.domain)
    implementation(projects.auth.domain)
    implementation(projects.core.data)
}