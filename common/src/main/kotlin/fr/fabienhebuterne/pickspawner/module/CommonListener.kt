package fr.fabienhebuterne.pickspawner.module

import fr.fabienhebuterne.pickspawner.exception.CustomException

object CommonListener {
    fun execute(runnable: () -> Unit) {
        try {
            runnable()
        } catch (ignored: CustomException) {
            // We ignore CustomException because error msg send to player
            // and don't want to have stacktrace on console
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}