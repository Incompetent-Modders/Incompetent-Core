package com.incompetent_modders.incomp_core.core.player.class_type;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.player.AbilityCooldownData;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public interface ClassTypeProvider {
    ItemCapability<ClassTypeProvider, Void> ITEM_CLASS_TYPE = ItemCapability.createVoid(IncompCore.makeId("class_type_provider"), ClassTypeProvider.class);
    EntityCapability<ClassTypeProvider, Void> ENTITY_CLASS_TYPE = EntityCapability.createVoid(IncompCore.makeId("class_type_provider"), ClassTypeProvider.class);

    ClassTypeStorage asStorage();

    void setStorage(ClassTypeStorage storage);

    default void setAbilityCooldown(int cooldown) {
        ClassTypeStorage storage = this.asStorage();

        this.setStorage(new ClassTypeStorage(storage.classType(), new AbilityCooldownData(cooldown)));
    }

    default void decreaseAbilityCooldown() {
        ClassTypeStorage storage = this.asStorage();
        AbilityCooldownData data = storage.cooldownData();
        int cooldown = data.abilityCooldown();

        if (cooldown > 0) {
            this.setStorage(new ClassTypeStorage(storage.classType(), new AbilityCooldownData(cooldown - 1)));
        } else {
            this.setStorage(new ClassTypeStorage(storage.classType(), new AbilityCooldownData(-1)));
        }
    }

    default boolean canUseAbility() {
        return this.asStorage().cooldownData().abilityCooldown() == -1;
    }
}
