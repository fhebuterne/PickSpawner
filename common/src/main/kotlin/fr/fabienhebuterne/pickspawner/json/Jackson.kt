package fr.fabienhebuterne.pickspawner.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object Jackson {
    val mapper = ObjectMapper().registerKotlinModule()
}