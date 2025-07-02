plugins {
    id("java")
    id("idea")
    id("signing")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.tddworks.central-portal-publisher") version "0.0.5"
}

val ossrhUsername: String by project
val ossrhPassword: String by project

val version: String by project
val group: String by project
val artifact: String by project

project.group = group
project.version = version

repositories {
    mavenCentral()
    // paper-api
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    // paper api
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    // database stuff
    implementation("com.mysql:mysql-connector-j:9.3.0")
    implementation("org.xerial:sqlite-jdbc:3.50.2.0")
    implementation("com.zaxxer:HikariCP:6.3.0")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}
sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("resources")
        }
    }
    test {
        java {
            srcDir("test")
        }
    }
}
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}


tasks.register<Copy>("prepareServer") {
    dependsOn("build")
    from(tasks.jar.get().archiveFile.get().asFile.path)
    rename(tasks.jar.get().archiveFile.get().asFile.name, "${project.name}.jar")
    into("G:\\paper\\plugins")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    compileJava {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }
    compileTestJava { options.encoding = "UTF-8" }
    javadoc { options.encoding = "UTF-8" }
    build {
        dependsOn(shadowJar)
    }
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set(project.name)
                groupId = group
                artifactId = artifact
                version = version
                description.set("A simple Command library for PaperMC")
                url.set("https://github.com/Hugo5000/PaperMC-DatabaseLib")
                licenses {
                    license {
                        name.set("GNU General Public License version 3")
                        url.set("https://opensource.org/license/gpl-3-0/")
                    }
                }
                developers {
                    developer {
                        name.set("Hugo")
                        email.set("noreply@hugob.at")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Hugo5000/PaperMC-DatabaseLib.git")
                    developerConnection.set("scm:git:ssh://github.com/Hugo5000/PaperMC-DatabaseLib.git")
                    url.set("http://github.com/Hugo5000/PaperMC-DatabaseLib/tree/master")
                }
            }
            from(components["java"])
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = if (version.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

sonatypePortalPublisher {
    authentication {
        username = ossrhUsername
        password = ossrhPassword
    }
    settings {
        autoPublish = false
    }
}