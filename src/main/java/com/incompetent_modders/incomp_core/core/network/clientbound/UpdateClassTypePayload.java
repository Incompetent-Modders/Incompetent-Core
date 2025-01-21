package com.incompetent_modders.incomp_core.core.network.clientbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.core.network.handle.ClientPayloadHandler;
import com.incompetent_modders.incomp_core.core.player.class_type.ClassTypeStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateClassTypePayload(ClassTypeStorage storage) implements CustomPacketPayload {

    public static final Type<UpdateClassTypePayload> TYPE = new Type<>(IncompCore.makeId("update_class_type"));

    public static final StreamCodec<FriendlyByteBuf, UpdateClassTypePayload> STREAM_CODEC = ClassTypeStorage.STREAM_CODEC.map(UpdateClassTypePayload::new, UpdateClassTypePayload::storage);

    public void handle(IPayloadContext context) {
        ClientPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
