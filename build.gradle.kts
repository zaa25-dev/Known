// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}