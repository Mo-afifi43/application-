# Keep custom views that are inflated from XML layouts (their XML constructors
# are looked up reflectively by LayoutInflater).
-keepclassmembers class com.mhma.nibras.ui.widget.** {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep line numbers for readable crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
