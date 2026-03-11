import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("net.minecrell.plugin-yml.bukkit")
    id("com.gradleup.shadow")
}

open class PlayTimeShadowExtension {

    internal var bukkitAction: Action<BukkitPluginDescription>? = null
    internal var shadowAction: Action<ShadowJar>? = null

    fun pluginYml(action: Action<BukkitPluginDescription>) {
        bukkitAction = action
    }

    fun shadowJar(action: Action<ShadowJar>) {
        shadowAction = action
    }
}

extensions.create("playTimeShadow", PlayTimeShadowExtension::class.java)

afterEvaluate {

    val ext = extensions.getByType(PlayTimeShadowExtension::class.java)

    ext.bukkitAction?.let { action ->
        extensions.configure<BukkitPluginDescription>("bukkit") {
            action.execute(this)
        }
    }

    tasks.withType<ShadowJar>().configureEach {
        tasks.findByName("generatePluginLibrariesJson")?.let {
            dependsOn(it)
        }

        ext.shadowAction?.execute(this)
    }
}