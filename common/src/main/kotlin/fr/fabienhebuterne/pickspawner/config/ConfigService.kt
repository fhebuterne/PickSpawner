package fr.fabienhebuterne.pickspawner.config

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.json.Jackson
import java.io.File
import java.io.IOException

abstract class ConfigService<T : ConfigType>(
    private val instance: PickSpawner,
    private val fileName: String
) {
    companion object {
        private const val EXTENSION_FILE = "yml"
    }

    protected var file: File = File(instance.dataFolder, "$fileName.$EXTENSION_FILE")
    protected lateinit var config: T
    protected val json = Jackson.mapper

    fun createAndLoadConfig(copyFromResource: Boolean) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            if (copyFromResource) {
                instance.saveResource("$fileName.$EXTENSION_FILE", false)
            } else {
                file.createNewFile()
            }
        }

        loadConfig()
    }

    fun loadConfig() {
        config = decodeFromString()

        // We save after load to add missing key if config is updated
        save()
    }

    fun getSerialization(): T {
        return config
    }

    private fun save() {
        try {
            file.writeText(encodeToString(), Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected abstract fun decodeFromString(): T

    protected fun encodeToString(): String = json.writeValueAsString(config)
}
