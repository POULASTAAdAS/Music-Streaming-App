plugins {
    alias(libs.plugins.kyoku.android.library)
    alias(libs.plugins.kyoku.okhttp.library)

    alias(libs.plugins.kyoku.android.dagger)
}

android {
    namespace = "com.poulastaa.auth.network"
}

dependencies {
    implementation(libs.okhttp.url.connection)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)

    implementation(projects.core.domain)
    implementation(projects.core.data)
}