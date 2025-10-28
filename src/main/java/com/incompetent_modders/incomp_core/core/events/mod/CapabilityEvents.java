package com.incompetent_modders.incomp_core.core.events.mod;

import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeProvider;
import com.incompetent_modders.incomp_core.core.player.class_type.EntityClassTypeProvider;
import com.incompetent_modders.incomp_core.core.player.mana.EntityManaProvider;
import com.incompetent_modders.incomp_core.core.player.mana.ManaProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.EntitySpeciesTypeProvider;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityEvents {
    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(ClassTypeProvider.ENTITY_CLASS_TYPE, EntityType.PLAYER, (player, context) -> new EntityClassTypeProvider<>(player));
        event.registerEntity(SpeciesTypeProvider.ENTITY_SPECIES_TYPE, EntityType.PLAYER, (player, context) -> new EntitySpeciesTypeProvider<>(player));
        event.registerEntity(ManaProvider.ENTITY_MANA, EntityType.PLAYER, (player, context) -> new EntityManaProvider<>(player));
    }
}
