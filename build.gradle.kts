buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    java
    id("com.github.ben-manes.versions") version "0.36.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
}

apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    repositories {
        mavenCentral {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }
}

subprojects {
    group = "com.owain.externals"

    project.extra["PluginProvider"] = "Owain94"
    project.extra["ProjectUrl"] = "https://discord.gg/OpenOSRS"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    apply<JavaPlugin>()
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")

    dependencies {
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.6.0")

        compileOnly(group = "com.openosrs", name = "http-api", version = "3.5.4")
        compileOnly(group = "com.openosrs", name = "runelite-api", version = "3.5.4")
        compileOnly(group = "com.openosrs", name = "runelite-client", version = "3.5.4")

        compileOnly(group = "org.apache.commons", name = "commons-text", version = "1.9")
        compileOnly(group = "com.google.guava", name = "guava", version = "30.1-jre")
        compileOnly(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
        compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
        compileOnly(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        compileOnly(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.0")
        compileOnly(group = "org.pf4j", name = "pf4j", version = "3.6.0")
        compileOnly(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.0.9")
        compileOnly(group = "org.pushing-pixels", name = "radiance-substance", version = "3.5.1")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"

            exclude("**/Emoji.java")
        }

        named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
            checkForGradleUpdate = false

            resolutionStrategy {
                componentSelection {
                    all {
                        if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                            reject("Non stable")
                        }
                    }
                }
            }
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}

tasks {
    named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
        checkForGradleUpdate = false

        resolutionStrategy {
            componentSelection {
                all {
                    if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                        reject("Non stable")
                    }
                }
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}
