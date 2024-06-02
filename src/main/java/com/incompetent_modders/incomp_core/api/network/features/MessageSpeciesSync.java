package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MessageSpeciesSync(List<ResourceLocation> speciesIDList) implements CustomPacketPayload {
    public static final Type<MessageSpeciesSync> TYPE = CustomIncompetentPayload.createType("species_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpeciesSync> CODEC = StreamCodec.ofMember(
            MessageSpeciesSync::write,
            MessageSpeciesSync::decode);
    
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(speciesIDList, ResourceLocation.STREAM_CODEC);
    }
    
    private static MessageSpeciesSync decode(RegistryFriendlyByteBuf buf) {
        var spells = buf.readList(ResourceLocation.STREAM_CODEC);
        return new MessageSpeciesSync(spells);
    }
    
    
    public List<ResourceLocation> getSpeciesIDList() {
        return speciesIDList;
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageSpeciesSync message, final IPayloadContext ctx)
    {
        IncompCore.LOGGER.info("Received species list sync packet");
        ClientSpeciesManager.getInstance().updateSpeciesList(message.getSpeciesIDList());
    }
}
