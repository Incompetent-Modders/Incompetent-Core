package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.client.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record MessageClassTypesSync(List<ResourceLocation> classTypesIDList) implements Packet<MessageClassTypesSync> {
    public static final ClientboundPacketType<MessageClassTypesSync> TYPE = new MessageClassTypesSync.Type();
    
    @Override
    public PacketType<MessageClassTypesSync> type() {
        return TYPE;
    }
    
    public List<ResourceLocation> getClassTypeIDList() {
        return classTypesIDList;
    }
    
    
    private static class Type implements ClientboundPacketType<MessageClassTypesSync> {
        
        @Override
        public Class<MessageClassTypesSync> type() {
            return MessageClassTypesSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_class_types");
        }
        
        public void encode(MessageClassTypesSync message, RegistryFriendlyByteBuf buf) {
            buf.writeCollection(message.classTypesIDList, ResourceLocation.STREAM_CODEC);
        }
        
        public MessageClassTypesSync decode(RegistryFriendlyByteBuf buf) {
            var spells = buf.readList(ResourceLocation.STREAM_CODEC);
            return new MessageClassTypesSync(spells);
        }
        
        @Override
        public Runnable handle(MessageClassTypesSync message) {
            return () -> {
                IncompCore.LOGGER.info("Received class type list sync packet");
                ClientClassTypeManager.getInstance().updateClassTypeList(message.getClassTypeIDList());
            };
        }
    }
}
