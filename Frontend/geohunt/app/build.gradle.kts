import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.external.javadoc.JavadocMemberLevel

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jubair5.geohunt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jubair5.geohunt"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.volley)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation(libs.play.services.location)
    implementation("nl.dionsegijn:konfetti:1.3.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

android.applicationVariants.all {
    if (this.name == "debug") {
        val variant = this

        tasks.register<Javadoc>("generateAndroidJavadoc") {
            description = "Generates Javadoc for the main source code"
            group = "documentation"

            // 1. CRITICAL FIX: Wait for compilation so R.java exists
            dependsOn(variant.javaCompileProvider)

            // 2. Don't stop on errors (Javadoc is very sensitive)
            isFailOnError = false

            // 3. Use the compile task's source (Your code + R.java + BuildConfig.java)
            source(variant.javaCompileProvider.get().source)

            doFirst {
                classpath = files(
                    android.bootClasspath,
                    variant.javaCompileProvider.get().classpath
                )
            }

            val options = options as StandardJavadocDocletOptions
            options.memberLevel = JavadocMemberLevel.PRIVATE
            options.links("https://docs.oracle.com/javase/8/docs/api/")
            options.links("https://developer.android.com/reference/")
            // 4. Ensure special characters don't break the build
            options.encoding = "UTF-8"
            options.charSet = "UTF-8"
        }
    }
}