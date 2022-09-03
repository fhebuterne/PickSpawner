package fr.fabienhebuterne.module

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.DefaultConfig
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import io.mockk.*
import org.bukkit.inventory.meta.Damageable
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ItemInitServiceTest {

    private val pickSpawner: PickSpawner = mockk()
    private val itemInitService = ItemInitService(pickSpawner)

    @Test
    fun `should refresh durability on item meta lore`() {
        // GIVEN
        val itemMeta: Damageable = mockk()
        every { itemMeta.damage } returns 10
        every { pickSpawner.defaultConfig } returns DefaultConfig(
            loreCustomPickaxe = listOf("---", "Usage : {{usage}}", "", "---")
        )

        val slot: CapturingSlot<List<String>> = slot()
        every { itemMeta.lore = capture(slot) } just Runs
        every { itemMeta.lore } returns listOf()

        // WHEN
        itemInitService.refreshDurabilityOnItemMetaLore(itemMeta, 250)

        // THEN
        expectThat(slot.captured).isEqualTo(listOf("---", "Usage : 240", "", "---"))

    }

}