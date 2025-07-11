plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("com.google.dagger.hilt.android")
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
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
  testImplementation("app.cash.turbine:turbine:1.1.0")
  testImplementation("com.google.truth:truth:1.4.4")

  testImplementation("junit:junit:4.13.2")
  testImplementation("org.mockito:mockito-core:5.12.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
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

  implementation("com.squareup.moshi:moshi:1.15.2")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

  implementation("com.squareup.retrofit2:retrofit:3.0.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

  implementation("com.google.dagger:hilt-android:2.56.2")
  kapt("com.google.dagger:hilt-compiler:2.56.2")

  androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
  kaptAndroidTest("com.google.dagger:hilt-compiler:2.56.2")

  // For local unit tests
  testImplementation("com.google.dagger:hilt-android-testing:2.56.2")
  kaptTest("com.google.dagger:hilt-compiler:2.56.2")
}

kapt {
  correctErrorTypes = true
}