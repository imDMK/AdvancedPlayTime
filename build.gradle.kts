plugins {
    id("java-library")
    id("checkstyle")
}

allprojects {
    repositories {
        mavenCentral()

        maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    }

    group = "com.github.imdmk"
    version = "2.0.4"

    apply(plugin = "java-library")
    apply(plugin = "checkstyle")

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.13.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("com.google.guava:guava-testlib:33.4.5-jre")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withJavadocJar()
        withSourcesJar()
    }

    checkstyle {
        toolVersion = "10.21.0"
        configFile = file("${rootDir}/checkstyle.xml")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
