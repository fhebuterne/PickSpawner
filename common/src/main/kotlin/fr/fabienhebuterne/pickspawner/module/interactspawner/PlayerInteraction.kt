package fr.fabienhebuterne.pickspawner.module.interactspawner

import org.bukkit.entity.EntityType
import java.util.*

object PlayerInteraction {
    val entityTypeByPlayer: MutableMap<UUID, EntityType> = mutableMapOf()
}