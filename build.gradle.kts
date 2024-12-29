plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

val group by properties
val version by properties
description = "KDStatusReloaded"

val defaultEncoding: String = "UTF-8"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "SpigotMC maven"
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    maven {
        name = "Rayzr522 maven"
        url = uri("https://raw.githubusercontent.com/Rayzr522/maven-repo/master/")
    }
}

dependencies {
    // Project tools
    compileOnly(libs.org.jetbrains.annotations)

    // Database
    implementation(libs.com.mysql.mysql.connector.j)
    implementation(libs.com.zaxxer.hikaricp)

    // Plugin dependencies
    compileOnly(libs.org.spigotmc.spigot.api)
    implementation(libs.me.rayzr522.jsonmessage)

    // For testing
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
}

tasks.withType<JavaCompile> {
    options.encoding = defaultEncoding
}

tasks.withType<Javadoc> {
    options.encoding = defaultEncoding
}
