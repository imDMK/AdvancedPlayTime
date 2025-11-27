dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":playtime-bukkit-api"))

    // DI
    implementation("org.panda-lang.utilities:di:1.8.0")

    // Adventure
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.21.0")

    // Dynamic dependency loader
    implementation("com.alessiodp.libby:libby-bukkit:2.0.0-SNAPSHOT")

    // Multification
    implementation("com.eternalcode:multification-bukkit:1.2.2")
    implementation("com.eternalcode:multification-okaeri:1.2.2")

    // Cache / DB layer
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")

    // Okaeri configs
    api("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.9")
    api("eu.okaeri:okaeri-configs-serdes-commons:5.0.5")

    // GUI, metrics, commands
    implementation("dev.triumphteam:triumph-gui:3.1.13")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.rollczi:litecommands-bukkit:3.10.6")
    implementation("dev.rollczi:litecommands-annotations:3.10.6")
}
