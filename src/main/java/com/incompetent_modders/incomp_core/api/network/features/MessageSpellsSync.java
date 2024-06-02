package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.client.ClientSpellManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MessageSpellsSync(List<ResourceLocation> spellIDList) implements CustomPacketPayload {
    public static final Type<MessageSpellsSync> TYPE = CustomIncompetentPayload.createType("spells_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellsSync> CODEC = StreamCodec.ofMember(
            MessageSpellsSync::write,
            MessageSpellsSync::decode);
    
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(spellIDList, ResourceLocation.STREAM_CODEC);
    }
    
    private static MessageSpellsSync decode(RegistryFriendlyByteBuf buf) {
        var spells = buf.readList(ResourceLocation.STREAM_CODEC);
        return new MessageSpellsSync(spells);
    }
    
    
    public List<ResourceLocation> getSpellIDList() {
        return spellIDList;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageSpellsSync message, final IPayloadContext ctx)
    {
        ClientSpellManager.getInstance().updateSpellList(message.getSpellIDList());
    }
}
