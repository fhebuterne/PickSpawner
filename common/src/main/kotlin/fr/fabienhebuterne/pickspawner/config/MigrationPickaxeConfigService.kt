package fr.fabienhebuterne.pickspawner.config

import com.fasterxml.jackson.module.kotlin.readValue
import fr.fabienhebuterne.pickspawner.PickSpawner

class MigrationPickaxeConfigService(instance: PickSpawner) :
    ConfigService<MigrationPickaxeConfig>(instance, "migrationPickaxe") {

    override fun decodeFromString(): MigrationPickaxeConfig {
        return json.readValue(file.readText(Charsets.UTF_8))
    }

}
