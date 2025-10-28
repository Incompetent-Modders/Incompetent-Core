package com.incompetent_modders.incomp_core.core.network.serverbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.handle.ServerPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpeciesAbilityPayload(boolean applyCooldown) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SpeciesAbilityPayload> TYPE = new CustomPacketPayload.Type<>(IncompCore.makeId("species_ability"));

    public static final StreamCodec<FriendlyByteBuf, SpeciesAbilityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SpeciesAbilityPayload::applyCooldown,
            SpeciesAbilityPayload::new
    );

    public void handle(IPayloadContext context) {
        ServerPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public CustomPacketPayload.Type<SpeciesAbilityPayload> type() {
        return TYPE;
    }
}
