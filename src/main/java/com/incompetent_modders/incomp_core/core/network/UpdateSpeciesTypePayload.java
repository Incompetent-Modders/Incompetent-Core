package com.incompetent_modders.incomp_core.core.network;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.handle.ClientPayloadHandler;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeStorage;
import com.incompetent_modders.incomp_core.core.player.species_type.SpeciesTypeStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateSpeciesTypePayload(SpeciesTypeStorage storage) implements CustomPacketPayload {

    public static final Type<UpdateSpeciesTypePayload> TYPE = new Type<>(IncompCore.makeId("update_species_type"));

    public static final StreamCodec<FriendlyByteBuf, UpdateSpeciesTypePayload> STREAM_CODEC = SpeciesTypeStorage.STREAM_CODEC.map(UpdateSpeciesTypePayload::new, UpdateSpeciesTypePayload::storage);

    public void handle(IPayloadContext context) {
        ClientPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
