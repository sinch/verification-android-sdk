-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.sinch.**$$serializer { *; }
-keepclassmembers class com.sinch.** {
    *** Companion;
}
-keepclasseswithmembers class com.sinch.** {
    kotlinx.serialization.KSerializer serializer(...);
}