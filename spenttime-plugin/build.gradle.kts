import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.2.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.eternalcode.pl/releases") // Eternalcode
    maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
    maven("https://repo.panda-lang.org/releases") // Litecommands
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")

    api("org.jetbrains:annotations:26.0.2")
    implementation(project(":spenttime-core"))
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<ShadowJar> {
    archiveFileName.set("SpentTime v${project.version} (MC 1.17.x-1.21.x).jar")

//    mergeServiceFiles()
//
//    exclude(
//        "META-INF/*.SF",
//        "META-INF/*.DSA",
//        "META-INF/*.RSA",
//        "module-info.class",
//        "org/intellij/lang/annotations/**",
//        "org/jetbrains/annotations/**"
//    )
//
//    val libPrefix = "com.github.imdmk.spenttime.lib"
//    listOf(
//        "com.zaxxer",            // Hikari
//        "com.j256",              // ORMLite
//        "com.github.benmanes",   // Caffeine
//        "net.kyori",             // Adventure
//        "dev.rollczi",           // litecommands
//        "dev.triumphteam",       // triumph-gui
//        "org.javassist",         // transitively
//        "org.yaml",              // SnakeYAML (okaeri)
//        "org.checkerframework",
//        "org.bstats",
//        "org.json",
//        "eu.okaeri",
//        "panda.std",
//        "panda.utilities"
//    ).forEach { pkg ->
//        relocate(pkg, "$libPrefix.$pkg")
//    }
//
//    minimize()
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