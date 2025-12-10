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
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation(libs.play.services.location)
    implementation("nl.dionsegijn:konfetti:1.3.2")
    implementation("com.vanniktech:android-image-cropper:4.7.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

android.applicationVariants.all {
    if (this.name == "debug") {
        val variant = this

        tasks.register<Javadoc>("generateAndroidJavadoc") {
            description = "Generates clean Javadoc for the main source code"
            group = "documentation"

            dependsOn(variant.javaCompileProvider)
            isFailOnError = false

            // 1. CLEAN SOURCE: Only read files from src/main/java
            // This automatically excludes R.java, BuildConfig, and Dagger files
            source(android.sourceSets.getByName("main").java.srcDirs)

            doFirst {
                classpath = files(
                    android.bootClasspath,
                    variant.javaCompileProvider.get().classpath,
                    // 2. RESOLVE REFS: Add compiled classes to classpath
                    // This lets Javadoc 'know' what R.id.xyz is, without creating a page for R
                    variant.javaCompileProvider.get().destinationDirectory
                )
            }

            val options = options as StandardJavadocDocletOptions
            options.memberLevel = JavadocMemberLevel.PRIVATE
            options.links("https://docs.oracle.com/javase/8/docs/api/")
            options.links("https://developer.android.com/reference/")
            options.encoding = "UTF-8"

            // FIX: Assign a List, don't pass arguments
            options.noQualifier(listOf(
                "all"
            ))
        }
    }
}