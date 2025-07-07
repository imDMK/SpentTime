import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.github.imdmk.spenttime.api"
version = "2.0.4"

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.7-R0.1-SNAPSHOT")

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
    implementation("org.jetbrains:annotations:26.0.2")
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
}
