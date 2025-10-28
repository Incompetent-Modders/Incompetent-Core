package com.incompetent_modders.incomp_core.core.player.species_type;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.player.AbilityCooldownData;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public interface SpeciesTypeProvider {
    ItemCapability<SpeciesTypeProvider, Void> ITEM_SPECIES_TYPE = ItemCapability.createVoid(IncompCore.makeId("species_type_provider"), SpeciesTypeProvider.class);
    EntityCapability<SpeciesTypeProvider, Void> ENTITY_SPECIES_TYPE = EntityCapability.createVoid(IncompCore.makeId("species_type_provider"), SpeciesTypeProvider.class);

    SpeciesTypeStorage asStorage();

    void setStorage(SpeciesTypeStorage storage);

    default void setAbilityCooldown(int cooldown) {
        SpeciesTypeStorage storage = this.asStorage();

        //this.setStorage(new SpeciesTypeStorage(storage.speciesType(), new AbilityCooldownData(cooldown)));
    }

    default void decreaseAbilityCooldown() {
        SpeciesTypeStorage storage = this.asStorage();
        AbilityCooldownData data = storage.cooldownData();
        //int cooldown = data.abilityCooldown();

        //if (cooldown > 0) {
        //    this.setStorage(new SpeciesTypeStorage(storage.speciesType(), new AbilityCooldownData(cooldown - 1)));
        //} else {
        //    this.setStorage(new SpeciesTypeStorage(storage.speciesType(), new AbilityCooldownData(-1)));
        //}
    }

    default boolean canUseAbility() {
        return false;//this.asStorage().cooldownData().abilityCooldown() == -1;
    }
}
