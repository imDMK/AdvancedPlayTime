plugins {
    id("java-library")
    id("checkstyle")
}

group = "com.github.imdmk.spenttime.api"
version = "2.0.4"

dependencies {
    api("org.jetbrains:annotations:26.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    withJavadocJar()
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
