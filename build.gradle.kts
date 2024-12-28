import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("org.jetbrains.changelog") version "2.2.1"
    id("org.jetbrains.kotlinx.kover") version "0.9.0"
}

group = "com.emmett08.plugins"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        snapshots()
    }
}

dependencies {
    intellijPlatform {
        create(
            type = providers.gradleProperty("platformType").orElse("IU"),  // Default to Ultimate if not set
            version = providers.gradleProperty("platformVersion").orElse("2024.3.1.1")  // Default to latest
        )

        bundledPlugin("com.intellij.java")

        plugin("org.jetbrains.plugins.go", "243.22562.218")
        plugin("PsiViewer", "251.175")

        testFramework(TestFrameworkType.JUnit5)
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Bundled)

        pluginVerifier()
        zipSigner()
    }

    api(platform("org.junit:junit-bom:5.11.4"))
    api("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.junit.platform:junit-platform-launcher:1.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.11.4")
    testImplementation("org.junit.platform:junit-platform-commons:1.11.4")

    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")

    testImplementation("junit:junit:4.13.2")
}

intellijPlatform {
    pluginConfiguration {
        version.set(providers.gradleProperty("pluginVersion").orElse("1.0.0"))

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                )
            }
        }

        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild").orElse("243"))
            untilBuild.set(providers.gradleProperty("pluginUntilBuild").orElse("243.*"))
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN").orNull
        privateKey = providers.environmentVariable("PRIVATE_KEY").orNull
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD").orNull
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN").orNull
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Changelog configuration
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl").orNull
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").orNull ?: "8.11"
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
