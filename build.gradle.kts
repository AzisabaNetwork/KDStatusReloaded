plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.runPaper)
}

val targetJavaVersion = 8
version = System.getenv("VERSION") ?: "0.1.0-indev"

// plugin metadata
val plWebsiteUrl: String by project
val plDescription: String by project

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://raw.githubusercontent.com/Rayzr522/maven-repo/master/")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    // system
    compileOnly(libs.spigotApi)

    // database
    implementation(libs.hikaricp)
    implementation(libs.mysqlConnectorJ)
    implementation(libs.bundles.jdbi)
    implementation(libs.bundles.flyway)

    // check
    implementation(libs.jspecify)
    testImplementation(libs.junit)
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props =
            mapOf(
                "version" to version,
                "name" to name,
                "description" to plDescription,
                "websiteUrl" to plWebsiteUrl,
            )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        isEnableRelocation = true
        relocationPrefix = "net.azisaba.kdstatusreloaded.libs"
    }

    runServer {
        minecraftVersion("1.16.5")
    }

    compileJava {
        options.encoding = "UTF-8"

        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    javadoc {
        options.encoding = "UTF-8"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            artifact(tasks.jar)
        }
    }

    repositories {
        maven {
            name = "azisaba-repo"
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
            url =
                if (project.version.toString().endsWith("-SNAPSHOT")) {
                    uri("https://repo.azisaba.net/repository/maven-snapshots/")
                } else {
                    uri("https://repo.azisaba.net/repository/maven-releases/")
                }
        }
    }
}
