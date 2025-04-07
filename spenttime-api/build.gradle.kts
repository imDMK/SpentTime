import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.github.imdmk.spenttime.api"
version = "2.0.0"

dependencies {
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
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
