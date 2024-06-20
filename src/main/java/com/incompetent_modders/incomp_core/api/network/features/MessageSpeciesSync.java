package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.managers.ClientSpeciesManager;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MessageSpeciesSync(List<ResourceLocation> speciesIDList) implements Packet<MessageSpeciesSync> {
    public static final ClientboundPacketType<MessageSpeciesSync> TYPE = new MessageSpeciesSync.Type();
    
    @Override
    public PacketType<MessageSpeciesSync> type() {
        return TYPE;
    }
    
    public List<ResourceLocation> getSpeciesIDList() {
        return speciesIDList;
    }
    
    private static class Type implements ClientboundPacketType<MessageSpeciesSync> {
        
        @Override
        public Class<MessageSpeciesSync> type() {
            return MessageSpeciesSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_species");
        }
        
        public void encode(MessageSpeciesSync message, RegistryFriendlyByteBuf buf) {
            buf.writeCollection(message.speciesIDList, ResourceLocation.STREAM_CODEC);
        }
        
        public MessageSpeciesSync decode(RegistryFriendlyByteBuf buf) {
            var spells = buf.readList(ResourceLocation.STREAM_CODEC);
            return new MessageSpeciesSync(spells);
        }
        
        @Override
        public Runnable handle(MessageSpeciesSync message) {
            return () -> {
                IncompCore.LOGGER.info("Received species list sync packet");
                ClientSpeciesManager.getInstance().updateSpeciesList(message.getSpeciesIDList());
            };
        }
    }
}
