package com.incompetent_modders.incomp_core.core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateClassTypePayload;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateManaPayload;
import com.incompetent_modders.incomp_core.core.network.clientbound.UpdateSpeciesTypePayload;
import com.incompetent_modders.incomp_core.core.network.serverbound.ClassAbilityPayload;
import com.incompetent_modders.incomp_core.core.network.serverbound.ScrollSpellSlotPacket;
import com.incompetent_modders.incomp_core.core.network.serverbound.SpeciesAbilityPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkEvents {

    @SubscribeEvent
    private void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(IncompCore.MODID).versioned("1.0");

        registrar.playToClient(UpdateClassTypePayload.TYPE, UpdateClassTypePayload.STREAM_CODEC, UpdateClassTypePayload::handle);
        registrar.playToClient(UpdateSpeciesTypePayload.TYPE, UpdateSpeciesTypePayload.STREAM_CODEC, UpdateSpeciesTypePayload::handle);
        registrar.playToClient(UpdateManaPayload.TYPE, UpdateManaPayload.STREAM_CODEC, UpdateManaPayload::handle);

        registrar.playToServer(ClassAbilityPayload.TYPE, ClassAbilityPayload.STREAM_CODEC, ClassAbilityPayload::handle);
        registrar.playToServer(SpeciesAbilityPayload.TYPE, SpeciesAbilityPayload.STREAM_CODEC, SpeciesAbilityPayload::handle);
        registrar.playToServer(ScrollSpellSlotPacket.TYPE, ScrollSpellSlotPacket.STREAM_CODEC, ScrollSpellSlotPacket::handle);
    }
}
