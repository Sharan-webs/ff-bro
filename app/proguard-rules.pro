-keep public class com.ff.aimbot.ui.MainActivity {
    public <methods>;
}

-keep public class com.ff.aimbot.core.AimbotAccessibilityService {
    public <methods>;
}

-keep public class com.ff.aimbot.ui.FloatingButtonService {
    public <methods>;
}

-keep class com.ff.aimbot.core.** { *; }

-dontwarn android.**

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt
