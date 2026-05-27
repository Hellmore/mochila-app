import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.language.jvm.tasks.ProcessResources

// Modulo Kotlin Multiplatform: Android + desktop JVM com Compose
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    jacoco
}

// Alvos de compilacao e dependencias por source set
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            
            implementation("org.xerial:sqlite-jdbc:3.45.3.0")

            
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmTest.dependencies {
            implementation("io.mockk:mockk:1.13.12")
            implementation(kotlin("test-junit"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

// Configuracao do app Android
android {
    namespace = "br.com.mochila"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "br.com.mochila"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Empacotamento do app desktop
compose.desktop {
    application {
        mainClass = "br.com.mochila.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "br.com.mochila"
            packageVersion = "1.0.0"
        }
    }
}

// Relatorio de cobertura de testes JVM
tasks.register<JacocoReport>("jacocoJvmReport") {
    dependsOn(tasks.named("jvmTest"))

    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }

    val commonSrc = kotlin.sourceSets["commonMain"].kotlin.sourceDirectories
    val jvmSrc    = kotlin.sourceSets["jvmMain"].kotlin.sourceDirectories
    sourceDirectories.setFrom(commonSrc + jvmSrc)

    classDirectories.setFrom(
        fileTree("${layout.buildDirectory.get()}/classes/kotlin/jvm/main") {
            exclude(
                "**/*Screen*",
                "**/ui/**",
                "**/theme/**",
                "**/component/**",
                "**/App*",
                "**/MainKt*",
                "**/MainActivity*",
                "**/generated/**",
                "**/mochila_app/**",
            )
        }
    )

    executionData.setFrom(
        fileTree(layout.buildDirectory.get()) { include("jacoco/jvmTest.exec") }
    )
}


// Copia SQL e recursos estaticos para o classpath em runtime
tasks.withType<ProcessResources> {
    from("src/commonMain/composeResources") {
        include("**/*.sql")
        include("**/*.txt")
        include("**/*.json")
        include("**/*.db")
        into("") 
    }
}
