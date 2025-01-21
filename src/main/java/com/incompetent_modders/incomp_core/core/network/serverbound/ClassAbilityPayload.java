package com.incompetent_modders.incomp_core.core.network.serverbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.handle.ServerPayloadHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClassAbilityPayload(boolean applyCooldown) implements CustomPacketPayload {
    public static final Type<ClassAbilityPayload> TYPE = new Type<>(IncompCore.makeId("class_ability"));

    public static final StreamCodec<FriendlyByteBuf, ClassAbilityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ClassAbilityPayload::applyCooldown,
            ClassAbilityPayload::new
    );

    public void handle(IPayloadContext context) {
        ServerPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<ClassAbilityPayload> type() {
        return TYPE;
    }
}
