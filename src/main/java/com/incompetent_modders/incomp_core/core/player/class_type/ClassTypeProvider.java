package com.incompetent_modders.incomp_core.core.player.class_type;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.player.AbilityCooldownData;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public interface ClassTypeProvider {
    ItemCapability<ClassTypeProvider, Void> ITEM_CLASS_TYPE = ItemCapability.createVoid(IncompCore.makeId("class_type_provider"), ClassTypeProvider.class);
    EntityCapability<ClassTypeProvider, Void> ENTITY_CLASS_TYPE = EntityCapability.createVoid(IncompCore.makeId("class_type_provider"), ClassTypeProvider.class);

    ClassTypeStorage asStorage();

    void setStorage(ClassTypeStorage storage);

    default void setAbilityCooldown(ResourceLocation ability, int cooldown) {
        ClassTypeStorage storage = this.asStorage();
        AbilityCooldownData data = storage.cooldownData();
        this.setStorage(new ClassTypeStorage(storage.classType(), data.addCooldown(ability, cooldown)));
    }

    default void decreaseAbilityCooldown() {
        ClassTypeStorage storage = this.asStorage();
        AbilityCooldownData data = storage.cooldownData();
        this.setStorage(new ClassTypeStorage(storage.classType(), data.decrementAllCooldowns(1)));
    }

    default boolean canUseAbility(ResourceLocation ability) {
        return !this.asStorage().cooldownData().hasAbilityCooldown(ability);
    }

    default int getAbilityCooldown(ResourceLocation ability) {
        ClassTypeStorage storage = this.asStorage();
        AbilityCooldownData data = storage.cooldownData();
        return data.getAbilityCooldown(ability);
    }
}
