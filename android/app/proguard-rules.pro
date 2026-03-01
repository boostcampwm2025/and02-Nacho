# ============================================================
# Attributes
# 크래시 발생 시 난독화된 로그를 원본 코드의 라인 번호와 매칭하려 분석하기 위함
# ============================================================
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature, InnerClasses, Exceptions, EnclosingMethod

# ============================================================
# Kotlin Serialization
# Kotlin Serialization 라이브러리는 컴파일 타임에 생성된 $serializer 클래스를 리플렉션으로 찾아 사용하므로 보존
# ============================================================
-keep,includedescriptorclasses class **$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * {
    static **$serializer INSTANCE;
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-dontnote kotlinx.serialization.AnnotationsKt
-dontwarn kotlinx.serialization.**

# ============================================================
# WorkManager
# 시스템이 백그라운드 작업을 실행할 때 Worker 클래스의 생성자를 리플렉션으로 호출하므로 보존
# ============================================================
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ============================================================
# Project: Network
# Retrofit 인터페이스는 리플렉션으로 호출되므로 메서드 보존
# ============================================================
-keepclassmembers interface com.andlife.network.api.** {
    <methods>;
}

# ============================================================
# SDK (Kakao, AppsFlyer, Naver Map, uCrop)
# 각 외부 라이브러리는 내부적으로 특정 클래스나 필드를 이름으로 참조하는 경우가 많음
# ============================================================
# Kakao SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }
-dontwarn com.kakao.**

# AppsFlyer SDK
-keep class com.appsflyer.AppsFlyerLib { *; }
-keep class com.appsflyer.AFInAppEventType { *; }
-keep class com.appsflyer.AFInAppEventParameterName { *; }
-dontwarn com.appsflyer.**

# Naver Map SDK
-dontwarn com.naver.maps.**
-dontnote com.naver.maps.**

# uCrop
-dontwarn com.yalantis.ucrop.**

# ============================================================
# Enum
# 리플렉션으로 values()/valueOf() 호출 대비
# ============================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
