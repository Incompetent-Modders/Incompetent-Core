package com.incompetent_modders.incomp_core.core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.UpdateClassTypePayload;
import com.incompetent_modders.incomp_core.core.network.UpdateManaPayload;
import com.incompetent_modders.incomp_core.core.network.UpdateSpeciesTypePayload;
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
    }
}
