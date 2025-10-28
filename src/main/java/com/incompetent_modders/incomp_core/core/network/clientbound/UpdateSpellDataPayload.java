package com.incompetent_modders.incomp_core.core.network.clientbound;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.SpellData;
import com.incompetent_modders.incomp_core.core.network.handle.ClientPayloadHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateSpellDataPayload(SpellData spellData) implements CustomPacketPayload {
    public static final Type<UpdateSpellDataPayload> TYPE = new Type<>(IncompCore.makeId("update_spell_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSpellDataPayload> STREAM_CODEC = SpellData.STREAM_CODEC.map(UpdateSpellDataPayload::new, UpdateSpellDataPayload::spellData);

    public void handle(IPayloadContext context) {
        ClientPayloadHandler.getInstance().handle(this, context);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
