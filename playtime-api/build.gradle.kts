dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2-1")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("org.assertj:assertj-core:3.25.2")
    testImplementation("org.mockito:mockito-core:5.20.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}