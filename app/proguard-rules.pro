# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep interface retrofit2.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-dontwarn com.google.gson.**
-keep class sun.misc.Unsafe { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# AndroidX Security
-keep class androidx.security.crypto.** { *; }

# Application Classes
-keep class com.security.shield.** { *; }
-keepclassmembers class com.security.shield.** { *; }

# Data Classes
-keep class com.security.shield.network.** { *; }
-keepclassmembers class com.security.shield.network.** { *; }

# Logger
-keep class com.security.shield.utils.LoggerUtil { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
