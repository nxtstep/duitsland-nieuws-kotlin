# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /nxt/Android/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable,Signature

# Requery
-dontwarn java.lang.FunctionalInterface
-dontwarn java.util.**
-dontwarn java.time.**
-dontwarn javax.annotation.**
-dontwarn javax.cache.**
-dontwarn javax.naming.**
-dontwarn javax.transaction.**
-dontwarn java.sql.**
-dontwarn android.support.**
-dontwarn io.requery.cache.**
-dontwarn io.requery.rx.**
-dontwarn io.requery.reactivex.**
-dontwarn io.requery.reactor.**
-dontwarn io.requery.query.**
-dontwarn io.requery.android.sqlcipher.**
-dontwarn io.requery.android.sqlitex.**
-keepclassmembers enum io.requery.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Bindings
-dontwarn android.databinding.**

# Retrofit2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions