package fr.fabienhebuterne.pickspawner.config

import com.fasterxml.jackson.module.kotlin.readValue
import fr.fabienhebuterne.pickspawner.PickSpawner

class TranslationConfigService(instance: PickSpawner) :
    ConfigService<TranslationConfig>(instance, "translation") {

    override fun decodeFromString(): TranslationConfig {
        return json.readValue(file.readText(Charsets.UTF_8))
    }

}
