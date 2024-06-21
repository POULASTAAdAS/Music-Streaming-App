plugins {
    alias(libs.plugins.kyoku.android.feature.ui)
    alias(libs.plugins.kyoku.android.dagger)
}

android {
    namespace = "com.poulastaa.auth.presentation"
}

dependencies {
    implementation(libs.credentials)
    implementation(libs.credentialsPlayServicesAuth)
    implementation(libs.google.id)

    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}