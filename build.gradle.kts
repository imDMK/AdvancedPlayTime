group = "com.github.imdmk.spenttime"
version = "2.0.4"

subprojects {
    version = "2.0.4"

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://repo.eternalcode.pl/releases") // Eternalcode
        maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
        maven("https://repo.panda-lang.org/releases") // Litecommands
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // Libby-bukkit
    }

    apply(plugin = "java-library")
    apply(plugin = "checkstyle")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withJavadocJar()
        withSourcesJar()
    }

    extensions.configure<CheckstyleExtension> {
        toolVersion = "10.21.0"
        configFile = file("${rootDir}/checkstyle.xml")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked", "-parameters"))
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
