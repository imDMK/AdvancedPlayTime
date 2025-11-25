import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.github.imdmk.spenttime.plugin"

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.2.1"
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":spenttime-core"))
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("SpentTime v${project.version} (MC 1.17.x-1.21.x).jar")

    mergeServiceFiles()

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "module-info.class",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**"
    )

    val relocationPrefix = "com.github.imdmk.spenttime.lib"
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
        //"org.slf4j",
        "org.yaml.snakeyaml",
        "panda.std",
        "panda.utilities"
    ).forEach { pkg ->
        relocate(pkg, "$relocationPrefix.$pkg")
    }

    minimize {
        exclude(dependency("com.github.ben-manes.caffeine:caffeine"))
    }
}

bukkit {
    name = "SpentTime"
    version = project.version.toString()
    apiVersion = "1.21"
    main = "com.github.imdmk.spenttime.SpentTimePluginLoader"
    author = "imDMK (dominiks8318@gmail.com)"
    description = "An efficient plugin for calculating your time spent in the game with many features and configuration possibilities."
    website = "https://github.com/imDMK/SpentTime"
}