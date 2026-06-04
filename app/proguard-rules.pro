# App ProGuard / R8 rules
-dontobfuscate

-optimizations !class/merging/*

# Keep Kotlin stdlib
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.collections.** {
    public static *** *(...);
    public *** *(...);
}

# Keep Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Rime engine (JNI integration)
-keep class com.kingzcheung.xime.rime.** { *; }
-keep class com.kingzcheung.xime.**Jni** { *; }

# Keep R8 metadata
-keepattributes SourceFile,LineNumberTable

# Suppress warnings for desktop-only APIs on Android
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Aggressive optimization
-processkotlinnullchecks remove
-allowaccessmodification
-mergeinterfacesaggressively