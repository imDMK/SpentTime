import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "com.github.imdmk.spenttime.plugin"
version = "2.0.3"

repositories {
    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation(project(":spenttime-api"))

    implementation("net.kyori:adventure-platform-bukkit:4.4.0")
    implementation("net.kyori:adventure-text-minimessage:4.19.0")

    implementation("com.eternalcode:multification-bukkit:1.1.4")
    implementation("com.eternalcode:multification-okaeri:1.1.4")
    implementation("com.eternalcode:gitcheck:1.0.0")

    implementation("dev.triumphteam:triumph-gui:3.1.11")

    implementation("org.bstats:bstats-bukkit:3.1.0")

    implementation("dev.rollczi:litecommands-bukkit:3.9.7")
    implementation("dev.rollczi:litecommands-annotations:3.9.7")
}

bukkit {
    name = "SpentTime"
    version = "${project.version}"
    apiVersion = "1.17"
    main = "com.github.imdmk.spenttime.SpentTimePlugin"
    author = "DMK (dominiks8318@gmail.com)"
    description = "An efficient plugin for calculating your time spent in the game with many features and configuration possibilities."
    website = "https://github.com/imDMK/SpentTime"

}

tasks.withType<ShadowJar> {
    archiveFileName.set("SpentTime v${project.version}.jar")

    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
    dependsOn("test")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
    )

    val libPrefix = "com.github.imdmk.spenttime.plugin.lib"
    listOf(
        "com.eternalcode",
        "com.j256",
        "com.github.benmanes",
        "net.kyori",
        "dev.rollczi",
        "dev.triumphteam",
        "javassist",
        "org.yaml",
        "org.checkerframework",
        "org.bstats",
        "org.json",
        "eu.okaeri",
        "panda.std",
        "panda.utilities",
    ).forEach { lib ->
        relocate(lib, "$libPrefix.$lib")
    }
}
