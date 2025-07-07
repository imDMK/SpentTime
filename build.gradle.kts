plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("checkstyle")
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "checkstyle")

    repositories {
        mavenCentral()

        maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/")}
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots")}
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")}
    }

    dependencies {
        implementation("com.zaxxer:HikariCP:6.2.1")
        implementation("com.j256.ormlite:ormlite-jdbc:6.1")

        implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.9")
        implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.9")

        testImplementation(platform("org.junit:junit-bom:5.13.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        testImplementation("com.google.guava:guava-testlib:33.4.5-jre")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    checkstyle {
        toolVersion = "10.21.0"

        configFile = file("${rootDir}/checkstyle.xml")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
        options.encoding = "UTF-8"
        options.release = 17
    }
}
