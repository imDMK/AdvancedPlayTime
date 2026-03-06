plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.eternalcode.pl/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
    maven("https://repo.panda-lang.org/releases") // Litecommands
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.alessiodp.com/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
}