plugins {
    `playtime-java`
    `playtime-java-test`
    `playtime-repositories`
    `playtime-spigot-compat`
}

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")
}