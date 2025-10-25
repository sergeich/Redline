plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

android {
    namespace = "me.sergeich.redline"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
}

mavenPublishing {

    coordinates("me.sergeich", "redline", "0.0.1")

    pom {
        name.set("Redline")
        description.set("Easy Redlines for Jetpack Compose - Visualize positions, sizes, spacings and alignment guides to verify your implementation against specs or to debug layout problems.")
        inceptionYear.set("2025")
        url.set("http://github.com/sergeich/Redline")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/mit/")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("sergeich")
                name.set("Sergei Glotov")
                url.set("https://github.com/sergeich/")
            }
        }
        scm {
            url.set("http://github.com/sergeich/Redline")
            connection.set("scm:git:git://github.com/sergeich/Redline.git")
            developerConnection.set("scm:git:ssh://git@github.com/sergeich/Redline.git")
        }
    }

    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
}
