import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.eternalcode.pl/releases") // Eternalcode
    maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
    maven("https://repo.panda-lang.org/releases") // Litecommands
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.github.placeholderapi:placeholderapi:2.11.6")

    implementation(project(":spenttime-bukkit-api"))

    // DI
    implementation("org.panda-lang.utilities:di:1.8.0")

    // Adventure
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")

    // Multification / utils
    implementation("com.eternalcode:multification-bukkit:1.2.2")
    implementation("com.eternalcode:multification-okaeri:1.2.2")
    implementation("com.eternalcode:gitcheck:1.0.0")

    // Cache / DB layer
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")

    // JDBC drivers
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")

    // Okaeri configs
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.9")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.5")

    // GUI, metrics, commands
    implementation("dev.triumphteam:triumph-gui:3.1.13")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.rollczi:litecommands-bukkit:3.10.6")
    implementation("dev.rollczi:litecommands-annotations:3.10.6")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("SpentTime v${project.version}.jar")

    mergeServiceFiles()

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "module-info.class",
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**"
    )

    val libPrefix = "com.github.imdmk.spenttime.lib"
    listOf(
        "com.zaxxer",            // Hikari
        "com.j256",              // ORMLite
        "com.github.benmanes",   // Caffeine
        "net.kyori",             // Adventure
        "dev.rollczi",           // litecommands
        "dev.triumphteam",       // triumph-gui
        "org.javassist",         // transitively
        "org.yaml",              // SnakeYAML (okaeri)
        "org.checkerframework",
        "org.bstats",
        "org.json",
        "eu.okaeri",
        "panda.std",
        "panda.utilities"
    ).forEach { pkg ->
        relocate(pkg, "$libPrefix.$pkg")
    }

    minimize()
}
