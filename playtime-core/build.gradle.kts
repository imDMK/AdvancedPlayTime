plugins {
    `playtime-java`
    `playtime-java-test`
    `playtime-repositories`
    `playtime-runtime-libraries`
}

dependencies {
    api(project(":playtime-api"))

    compileOnly("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")

    implementation("org.panda-lang.utilities:di:${Versions.PANDA_DI}")
    implementation("io.github.classgraph:classgraph:${Versions.CLASSGRAPH}")

    implementation("net.kyori:adventure-platform-bukkit:${Versions.KYORI_PLATFORM_BUKKIT}")
    implementation("net.kyori:adventure-text-minimessage:${Versions.KYORI_TEXT_MINIMESSAGE}")

    implementation("com.eternalcode:multification-bukkit:${Versions.MULTIFICATION_BUKKIT}")
    implementation("com.eternalcode:multification-okaeri:${Versions.MULTIFICATION_OKAERI}")

    implementation("com.github.ben-manes.caffeine:caffeine:${Versions.CAFFEINE}")
    implementation("com.zaxxer:HikariCP:${Versions.HIKARI_CP}")
    implementation("com.j256.ormlite:ormlite-jdbc:${Versions.ORMLITE}")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:${Versions.OKAERI_SNAKEYAML}")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_SERDES_COMMONS}")

    implementation("dev.triumphteam:triumph-gui:${Versions.TRIUMPH_GUI}")
    implementation("org.bstats:bstats-bukkit:${Versions.BSTATS_BUKKIT}")

    implementation("dev.rollczi:litecommands-bukkit:${Versions.LITECOMMANDS}")
    implementation("dev.rollczi:litecommands-annotations:${Versions.LITECOMMANDS}")

    playTimeLibraryDownload("com.mysql:mysql-connector-j:${Versions.MYSQL_CONNECTOR_J}")
    playTimeLibraryDownload("org.mariadb.jdbc:mariadb-java-client:${Versions.MARIADB_CONNECTOR_J}")
    playTimeLibraryDownload("org.xerial:sqlite-jdbc:${Versions.SQLITE_JDBC}")
    playTimeLibraryDownload("org.postgresql:postgresql:${Versions.POSTGRESQL_JDBC}")
    playTimeLibraryDownload("com.h2database:h2:${Versions.H2_JDBC}")
    playTimeLibraryDownload("com.microsoft.sqlserver:mssql-jdbc:${Versions.MSSQL_JDBC}")
}
