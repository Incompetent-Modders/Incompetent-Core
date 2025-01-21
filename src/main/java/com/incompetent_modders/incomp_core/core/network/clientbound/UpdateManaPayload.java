package com.incompetent_modders.incomp_core.core.network.clientbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.handle.ClientPayloadHandler;
import com.incompetent_modders.incomp_core.core.player.mana.ManaStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateManaPayload(ManaStorage storage) implements CustomPacketPayload {

    public static final Type<UpdateManaPayload> TYPE = new Type<>(IncompCore.makeId("update_mana"));

    public static final StreamCodec<FriendlyByteBuf, UpdateManaPayload> STREAM_CODEC = ManaStorage.STREAM_CODEC.map(UpdateManaPayload::new, UpdateManaPayload::storage);

    public void handle(IPayloadContext context) {
        ClientPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
