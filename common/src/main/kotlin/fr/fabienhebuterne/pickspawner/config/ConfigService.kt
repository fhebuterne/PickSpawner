package fr.fabienhebuterne.pickspawner.config

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.json.Jackson
import java.io.File
import java.io.IOException

abstract class ConfigService<T : ConfigType>(
    private val instance: PickSpawner,
    private val fileName: String
) {

    protected var file: File = File(instance.dataFolder, "$fileName.yml")
    protected lateinit var obj: T
    protected val json = Jackson.mapper

    fun createAndLoadConfig(copyFromResource: Boolean) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            if (copyFromResource) {
                instance.saveResource("$fileName.json", false)
            } else {
                file.createNewFile()
            }
        }

        loadConfig()
    }

    fun loadConfig() {
        obj = decodeFromString()

        // TODO : Add method to check missing key/value in current file (compare with resource jar file)
        // We save after load to add missing key if config is updated
        save()
    }

    fun getSerialization(): T {
        return obj
    }

    fun setSerialization(obj: T) {
        this.obj = obj
    }

    private fun save() {
        try {
            file.writeText(encodeToString(), Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected abstract fun decodeFromString(): T

    protected fun encodeToString(): String = json.writeValueAsString(obj)
}
