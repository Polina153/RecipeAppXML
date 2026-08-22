// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.2.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.9.8" apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    id("androidx.room") version "2.8.4" apply false
}