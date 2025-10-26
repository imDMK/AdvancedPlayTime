repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
}

dependencies {
    api(project(":spenttime-api"))
    api("org.jetbrains:annotations:26.0.2")

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
}