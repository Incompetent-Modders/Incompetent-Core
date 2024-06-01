package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface CustomIncompetentPayload extends CustomPacketPayload {
    static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String name) {
        return new CustomPacketPayload.Type<>(IncompCore.makeId(name));
    }
}
