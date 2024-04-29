import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("checkstyle")
}

group = "com.github.imdmk"
version = "1.0.6"

repositories {
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.5-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")

    implementation("dev.triumphteam:triumph-gui:3.1.7")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.1")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.1")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.1")

    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")

    implementation("dev.rollczi:litecommands-bukkit:3.4.0")
    implementation("dev.rollczi:litecommands-annotations:3.4.0")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")

    implementation("com.eternalcode:gitcheck:1.0.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("com.google.guava:guava-testlib:33.1.0-jre")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

bukkit {
    name = "SpentTime"
    version = "${project.version}"
    apiVersion = "1.17"
    main = "com.github.imdmk.spenttime.SpentTimePlugin"
    author = "DMK"
    description = "An efficient plugin for your time spent in the game with many features and configuration possibilities."
    website = "https://github.com/imDMK/SpentTime"
}

checkstyle {
    toolVersion = "10.12.1"

    configFile = file("${rootDir}/checkstyle.xml")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name} v${project.version}.jar")

    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
    dependsOn("test")

    exclude(
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**",
            "META-INF/**",
    )

    val libPrefix = "com.github.imdmk.spenttime.lib"

    listOf(
            "dev.triumphteam",
            "dev.rollczi",
            "com.eternalcode",
            "com.google.gson",
            "com.j256",
            "com.zaxxer",
            "eu.okaeri",
            "net.kyori",
            "org.json",
            "org.yaml",
            "org.bstats",
            "panda",
            "javassist"
    ).forEach { lib ->
        relocate(lib, "$libPrefix.$lib")
    }
}