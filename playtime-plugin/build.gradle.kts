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

tasks.build {
    dependsOn(tasks.test)
    dependsOn(tasks.shadowJar)
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
            "org.bstats",
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