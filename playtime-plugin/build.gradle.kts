plugins {
    `playtime-java`
    `playtime-repositories`
    `playtime-shadow`

    id("xyz.jpenilla.run-paper") version "3.0.2"
}

dependencies {
    implementation("com.alessiodp.libby:libby-bukkit:${Versions.LIBBY_BUKKIT}")
    api(project(":playtime-core"))
}

playTimeShadow {
    pluginYml {
        name = "AdvancedPlayTime"
        version = project.version.toString()
        apiVersion = "1.21"
        softDepend = listOf("PlaceholderAPI")
        main = "com.github.imdmk.playtime.plugin.PlayTimePlugin"
        author = "imDMK (dominiks8318@gmail.com)"
        description = "An efficient plugin for calculating your time spent in the game with many features and configuration possibilities."
        website = "https://github.com/imDMK/AdvancedPlayTime"
    }

    shadowJar {
        archiveFileName.set("AdvancedPlayTime v${project.version} (MC 1.21.x).jar")

        exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA",
            "module-info.class",
            "org/intellij/lang/annotations/**",
            "org/jetbrains/annotations/**"
        )

        val relocationPrefix = "com.github.imdmk.playtime.libs"
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
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }
}