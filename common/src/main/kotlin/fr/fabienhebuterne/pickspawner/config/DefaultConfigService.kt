package fr.fabienhebuterne.pickspawner.config

import com.fasterxml.jackson.module.kotlin.readValue
import fr.fabienhebuterne.pickspawner.PickSpawner

class DefaultConfigService(instance: PickSpawner) :
    ConfigService<DefaultConfig>(instance, "config") {

    override fun decodeFromString(): DefaultConfig {
        return json.readValue(file.readText(Charsets.UTF_8))
    }

}
