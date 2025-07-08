plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
}

android {
  namespace = "com.aura"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {

  implementation("androidx.core:core-ktx:1.16.0")
  implementation("androidx.appcompat:appcompat:1.7.1")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.annotation:annotation:1.9.1")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

  val lifecycle_version = "2.9.1"
  val arch_version = "2.2.0"

  // ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
  // ViewModel utilities for Compose
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
  // LiveData
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
  // Lifecycles only (without ViewModel or LiveData)
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
  // Lifecycle utilities for Compose
  implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")

  // Saved state module for ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

  // ViewModel integration with Navigation3
  implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:1.0.0-alpha03")

  // Annotation processor
  //noinspection LifecycleAnnotationProcessorWithJava8
  kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
  // alternately - if using Java8, use the following instead of lifecycle-compiler
  implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

  // optional - helpers for implementing LifecycleOwner in a Service
  implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")

  // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
  implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")

  // optional - ReactiveStreams support for LiveData
  implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")

  // optional - Test helpers for LiveData
  testImplementation("androidx.arch.core:core-testing:$arch_version")

  // optional - Test helpers for Lifecycle runtime
  testImplementation ("androidx.lifecycle:lifecycle-runtime-testing:$lifecycle_version")
}