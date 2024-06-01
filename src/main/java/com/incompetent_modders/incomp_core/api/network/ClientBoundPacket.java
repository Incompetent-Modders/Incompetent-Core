package com.incompetent_modders.incomp_core.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface ClientBoundPacket extends CustomIncompetentPayload {
    default void handleOnClient(IPayloadContext context) {
        context.enqueueWork(() -> {
            handleOnClient(context.player());
        });
    }
    
    default void handleOnClient(Player player) {
        throw new AbstractMethodError("Unimplemented method on " + getClass());
    }
}
