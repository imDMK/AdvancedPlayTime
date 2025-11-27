group = "com.github.imdmk.playtime"
version = "3.0.0"

subprojects {
    version = "3.0.0"

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
        maven("https://repo.eternalcode.pl/releases") // Eternalcode
        maven("https://storehouse.okaeri.eu/repository/maven-public/") // Okaeri
        maven("https://repo.panda-lang.org/releases") // Litecommands
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // Libby-bukkit
    }

    apply(plugin = "java-library")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<Javadoc>().configureEach {
        isFailOnError = false

        val opts = options as StandardJavadocDocletOptions
        opts.addStringOption("Xdoclint:none", "-quiet")
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
