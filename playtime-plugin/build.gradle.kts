import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
    implementation(project(":playtime-core"))
}

tasks.build {
    dependsOn(tasks.test)
    dependsOn(tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("AdvancedPlayTime v${project.version} (MC 1.21).jar")

    mergeServiceFiles()

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "module-info.class",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**"
    )

    val relocationPrefix = "com.github.imdmk.playtime.lib"
    listOf(
        "com.alessiodp.libby",
        "com.eternalcode.multification",
        "com.github.benmanes.caffeine",
        "com.google.errorprone",
        "com.google.gson",
        "com.j256.ormlite",
        "com.zaxxer.hikari",
        "dev.rollczi.litecommands",
        "dev.triumphteam.gui",
        "eu.okaeri.configs",
        "javassist",
        "net.kyori",
        "org.bstats",
        "org.jspecify.annotations",
        "org.panda_lang.utilities",
        "org.yaml.snakeyaml",
        "panda.std",
        "panda.utilities"
    ).forEach { pkg ->
        relocate(pkg, "$relocationPrefix.$pkg")
    }
}

bukkit {
    name = "AdvancedPlayTime"
    version = project.version.toString()
    apiVersion = "1.21"
    softDepend = listOf("PlaceholderAPI")
    main = "com.github.imdmk.playtime.PlayTimePluginLoader"
    author = "imDMK (dominiks8318@gmail.com)"
    description = "An efficient plugin for calculating your time spent in the game with many features and configuration possibilities."
    website = "https://github.com/imDMK/AdvancedPlayTime"
}