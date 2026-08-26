import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.util.zip.ZipFile

plugins {
    `java-library`
}

/**
 * Keeps the plugin usable on the whole advertised server range.
 *
 * The shipped artifact is built against the newest Spigot API, so two things can
 * silently break on the oldest supported server: sources may reference API that did
 * not exist yet, and call sites may be emitted against a type whose shape changed
 * (`org.bukkit.Sound`, for instance, was an enum in 1.21 and is an interface today).
 *
 * `compileJavaSpigotMin` catches the first case, `checkSpigotBinaryCompatibility` the
 * second. Both run as part of `check`.
 */
val spigotMinApi: Configuration = configurations.create("spigotMinApi") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    spigotMinApi("org.spigotmc:spigot-api:${Versions.SPIGOT_API_MIN}")
}

/** A single method/field reference emitted into one of our own classes. */
data class MemberReference(
    val owner: String,
    val name: String,
    val descriptor: String,
    val field: Boolean,
    val viaInterface: Boolean
)

/** Everything relevant we know about one class of the reference API. */
data class ApiClass(
    val name: String,
    val isInterface: Boolean,
    val superName: String?,
    val interfaces: List<String>,
    val methods: Set<String>,
    val fields: Set<String>
)

abstract class CheckSpigotBinaryCompatibilityTask : DefaultTask() {

    private val bukkitPackage = "org/bukkit/"

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classDirectories: ConfigurableFileCollection

    @get:InputFiles
    @get:Classpath
    abstract val minimumApi: ConfigurableFileCollection

    @get:Input
    abstract val minimumApiVersion: Property<String>

    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun check() {

        val api = readApi()
        val classReferences = sortedSetOf<String>()
        val memberReferences = mutableSetOf<MemberReference>()

        readOwnClasses(classReferences, memberReferences)

        val problems = mutableListOf<String>()

        for (reference in classReferences) {
            if (!api.containsKey(reference)) {
                problems += "unknown type $reference"
            }
        }

        for (reference in memberReferences.sortedBy { "${it.owner}#${it.name}${it.descriptor}" }) {

            val owner = api[reference.owner]

            if (owner == null) {
                problems += "unknown type ${reference.owner}"
                continue
            }

            if (!hasMember(api, reference.owner, reference)) {
                problems += "unknown ${if (reference.field) "field" else "method"} " +
                        "${reference.owner}#${reference.name}${if (reference.field) "" else reference.descriptor}"
                continue
            }

            if (!reference.field && reference.viaInterface != owner.isInterface) {
                problems += "${reference.owner} is a ${if (owner.isInterface) "interface" else "class"} on " +
                        "${minimumApiVersion.get()}, but ${reference.name}${reference.descriptor} is compiled as " +
                        "${if (reference.viaInterface) "invokeinterface" else "invokevirtual/invokestatic"} — " +
                        "this throws IncompatibleClassChangeError at runtime"
            }
        }

        val distinct = problems.distinct()

        val file = report.get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            buildString {
                appendLine("Spigot API baseline: ${minimumApiVersion.get()}")
                appendLine("types referenced: ${classReferences.size}, members referenced: ${memberReferences.size}")
                appendLine()
                if (distinct.isEmpty()) appendLine("OK") else distinct.forEach { appendLine(it) }
            }
        )

        if (distinct.isNotEmpty()) {
            throw GradleException(
                distinct.joinToString(
                    prefix = "Not binary compatible with Spigot API ${minimumApiVersion.get()}:\n  - ",
                    separator = "\n  - "
                )
            )
        }
    }

    private fun readApi(): Map<String, ApiClass> {

        val api = mutableMapOf<String, ApiClass>()

        minimumApi.filter { it.isFile }.forEach { jar ->
            ZipFile(jar).use { zip ->
                zip.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach { entry ->
                        val reader = zip.getInputStream(entry).use { ClassReader(it.readBytes()) }
                        val methods = mutableSetOf<String>()
                        val fields = mutableSetOf<String>()

                        reader.accept(object : ClassVisitor(Opcodes.ASM9) {

                            override fun visitMethod(
                                access: Int,
                                name: String,
                                descriptor: String,
                                signature: String?,
                                exceptions: Array<out String>?
                            ): MethodVisitor? {
                                methods += "$name$descriptor"
                                return null
                            }

                            override fun visitField(
                                access: Int,
                                name: String,
                                descriptor: String,
                                signature: String?,
                                value: Any?
                            ): FieldVisitor? {
                                fields += name
                                return null
                            }
                        }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

                        api[reader.className] = ApiClass(
                            name = reader.className,
                            isInterface = reader.access and Opcodes.ACC_INTERFACE != 0,
                            superName = reader.superName,
                            interfaces = reader.interfaces.toList(),
                            methods = methods,
                            fields = fields
                        )
                    }
            }
        }

        return api
    }

    private fun readOwnClasses(
        classReferences: MutableSet<String>,
        memberReferences: MutableSet<MemberReference>
    ) {

        fun collectTypes(descriptor: String) {
            Regex("L(org/bukkit/[^;<]+);").findAll(descriptor).forEach { classReferences += it.groupValues[1] }
        }

        classDirectories.asFileTree.matching { include("**/*.class") }.forEach { file ->

            ClassReader(file.readBytes()).accept(object : ClassVisitor(Opcodes.ASM9) {

                override fun visitMethod(
                    access: Int,
                    name: String,
                    descriptor: String,
                    signature: String?,
                    exceptions: Array<out String>?
                ): MethodVisitor {

                    collectTypes(descriptor)

                    return object : MethodVisitor(Opcodes.ASM9) {

                        override fun visitTypeInsn(opcode: Int, type: String) {
                            if (type.startsWith(bukkitPackage)) classReferences += type
                        }

                        override fun visitLdcInsn(value: Any?) {
                            if (value is Type && value.sort == Type.OBJECT && value.internalName.startsWith(bukkitPackage)) {
                                classReferences += value.internalName
                            }
                        }

                        override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
                            collectTypes(descriptor)
                            if (owner.startsWith(bukkitPackage)) {
                                memberReferences += MemberReference(owner, name, descriptor, field = true, viaInterface = false)
                            }
                        }

                        override fun visitMethodInsn(
                            opcode: Int,
                            owner: String,
                            name: String,
                            descriptor: String,
                            isInterface: Boolean
                        ) {
                            collectTypes(descriptor)
                            if (owner.startsWith(bukkitPackage)) {
                                memberReferences += MemberReference(owner, name, descriptor, field = false, viaInterface = isInterface)
                            }
                        }
                    }
                }

                override fun visitField(
                    access: Int,
                    name: String,
                    descriptor: String,
                    signature: String?,
                    value: Any?
                ): FieldVisitor? {
                    collectTypes(descriptor)
                    return null
                }
            }, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        }
    }

    private fun hasMember(api: Map<String, ApiClass>, owner: String, reference: MemberReference): Boolean {

        val current = api[owner] ?: return hasJdkMember(owner, reference)

        if (reference.field && reference.name in current.fields) return true
        if (!reference.field && "${reference.name}${reference.descriptor}" in current.methods) return true

        val parents = buildList {
            current.superName?.let { add(it) }
            addAll(current.interfaces)
        }

        return parents.any { hasMember(api, it, reference) }
    }

    /**
     * Bukkit types inherit from the JDK — `java.lang.Object` and, for the enums the
     * older API still used, `java.lang.Enum`. Those supertypes are not in the Spigot
     * jar, so resolve them against the running JVM instead of reporting them missing.
     */
    private fun hasJdkMember(owner: String, reference: MemberReference): Boolean {

        if (!owner.startsWith("java/")) return false

        val type = runCatching { Class.forName(owner.replace('/', '.')) }.getOrNull() ?: return false

        if (reference.field) {
            return generateSequence(type) { it.superclass }
                .any { candidate -> candidate.declaredFields.any { it.name == reference.name } }
        }

        return generateSequence(type) { it.superclass }
            .any { candidate ->
                candidate.declaredMethods.any {
                    it.name == reference.name && Type.getMethodDescriptor(it) == reference.descriptor
                }
            }
    }
}

val compileJavaSpigotMin = tasks.register<JavaCompile>("compileJavaSpigotMin") {

    description = "Compiles the main sources against Spigot API ${Versions.SPIGOT_API_MIN}."
    group = LifecycleBasePlugin.VERIFICATION_GROUP

    val main = project.the<SourceSetContainer>()["main"]

    source(main.java)

    classpath = spigotMinApi + main.compileClasspath.filter { !it.name.startsWith("spigot-api-") }

    destinationDirectory.set(layout.buildDirectory.dir("classes/java/spigotMin"))
}

val checkSpigotBinaryCompatibility =
    tasks.register<CheckSpigotBinaryCompatibilityTask>("checkSpigotBinaryCompatibility") {

        description = "Verifies that the Bukkit call sites emitted into this module resolve on " +
                "Spigot API ${Versions.SPIGOT_API_MIN}."
        group = LifecycleBasePlugin.VERIFICATION_GROUP

        classDirectories.from(project.the<SourceSetContainer>()["main"].output.classesDirs)
        minimumApi.from(spigotMinApi)
        minimumApiVersion.set(Versions.SPIGOT_API_MIN)
        report.set(layout.buildDirectory.file("reports/spigot-compat/${project.name}.txt"))

        dependsOn(tasks.named("classes"))
    }

tasks.named("check") {
    dependsOn(compileJavaSpigotMin, checkSpigotBinaryCompatibility)
}
