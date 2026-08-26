plugins {
    `java-library`
}

group = "com.github.imdmk"
version = "3.0.2"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.JAVA_TOOLCHAIN))
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
    options.release.set(Versions.JAVA_RELEASE)
}
