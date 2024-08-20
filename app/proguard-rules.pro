# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes Signature
-keep class com.google.gson.** { *; }
-keep class com.alphaomardiallo.handydocs.data.UriListTypeConverter { *; }

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile