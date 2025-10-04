plugins {
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.intellij.platform") version "2.9.0"
}

group = "com.novdov.equalityenum"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        pycharmCommunity("2025.1") {
            useInstaller = false
        }
        bundledPlugin("PythonCore")
        pluginVerifier()
        zipSigner()
    }
    testImplementation(kotlin("test"))
}

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        name = "EqualityPropertyEnum Support"
        version = "1.0.0"

        ideaVersion {
            sinceBuild = "251"
            untilBuild = "253.*"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjdk-release=21")
    }
}

configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
}