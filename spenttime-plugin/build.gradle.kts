import java.util.Properties

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")

    api("org.jetbrains:annotations:26.0.2")
    implementation(project(":spenttime-core"))
}

val dynamicLibraries = listOf(
    "org.postgresql:postgresql:42.7.4",
     "com.mysql:mysql-connector-j:8.4.0",
     "org.mariadb.jdbc:mariadb-java-client:3.4.1"
)

// 2) Repozytoria (możesz dodać mirror)
val dynamicRepositories = listOf(
    "https://repo1.maven.org/maven2"
)

// 3) Jawnie rejestrowane drivery (opcjonalnie)
val explicitJdbcDrivers = listOf(
    "org.postgresql.Driver"
)

// 4) Mapowanie URL → driver FQCN (jeśli chcesz dobrać po jdbcUrl)
val jdbcUrlToDriver = mapOf(
    "jdbc:postgresql:" to "org.postgresql.Driver",
    "jdbc:mysql:"      to "com.mysql.cj.jdbc.Driver",
    "jdbc:mariadb:"    to "org.mariadb.jdbc.Driver",
)

// 5) Wstrzyknięcie wartości do zasobu JAR-a (bez generowania .java)
tasks.processResources {
    val props = mapOf(
        "dynLibs" to dynamicLibraries.joinToString("|"),
        "dynRepos" to dynamicRepositories.joinToString("|"),
        "jdbcDrivers" to explicitJdbcDrivers.joinToString("|"),
        "jdbcUrl2Drv" to jdbcUrlToDriver.entries.joinToString("|") { "${it.key}>${it.value}" }
    )
    inputs.properties(props)

    filesMatching("dynamic-libs.properties") {
        expand(props)
    }
}

bukkit {
    name = "SpentTime"
    version = project.version.toString()
    apiVersion = "1.21"
    main = "com.github.imdmk.spenttime.SpentTimePlugin"
    author = "imDMK (dominiks8318@gmail.com)"
    description = "An efficient plugin for calculating your time spent in the game with many features and configuration possibilities."
    website = "https://github.com/imDMK/SpentTime"
}