plugins {
    `java-library`
}

val playTimeLibraryDownload = configurations.create("playTimeLibraryDownload")

configurations.named("compileOnly") {
    extendsFrom(playTimeLibraryDownload)
}

abstract class GeneratePluginLibrariesJsonTask : DefaultTask() {

    @get:Input
    abstract val repositories: ListProperty<String>

    @get:Input
    abstract val dependencies: ListProperty<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {

        val repos = repositories.get()
        val deps = dependencies.get()

        val reposJson = repos.joinToString(
            prefix = "[",
            postfix = "]"
        ) { "\"$it\"" }

        val depsJson = deps.map { gav ->

            val (group, artifact, version) = gav.split(":")

            """
            {
              "group": "$group",
              "artifact": "$artifact",
              "version": "$version"
            }
            """.trimIndent()

        }

        val json = """
        {
          "repositories": $reposJson,
          "dependencies": [${depsJson.joinToString(",")}]
        }
        """.trimIndent()

        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(json)
    }
}

val generatePluginLibrariesJson = tasks.register<GeneratePluginLibrariesJsonTask>("generatePluginLibrariesJson") {

    repositories.set(
        project.repositories
            .filterIsInstance<MavenArtifactRepository>()
            .map { it.url.toString().removeSuffix("/") }
    )

    dependencies.set(
        configurations.getByName("playTimeLibraryDownload")
            .dependencies
            .map { "${it.group}:${it.name}:${it.version}" }
    )

    outputFile.set(layout.buildDirectory.file("generated/plugin-libraries.json"))
}

tasks.processResources {
    dependsOn(generatePluginLibrariesJson)

    from(generatePluginLibrariesJson.map { it.outputFile }) {
        into("")
    }
}