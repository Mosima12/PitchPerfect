plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    // ADD THIS LINE: KSP plugin (version must match your Kotlin version)
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}