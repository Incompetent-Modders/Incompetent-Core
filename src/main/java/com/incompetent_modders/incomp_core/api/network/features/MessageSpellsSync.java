package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MessageSpellsSync(Map<ResourceLocation, SpellProperties> spellsList) implements Packet<MessageSpellsSync> {
    public static final ClientboundPacketType<MessageSpellsSync> TYPE = new Type();
    
    @Override
    public PacketType<MessageSpellsSync> type() {
        return TYPE;
    }
    
    public Map<ResourceLocation, SpellProperties> getSpellProperties() {
        return spellsList;
    }
    
    
    private static class Type implements ClientboundPacketType<MessageSpellsSync> {
        private static final Codec<Map<ResourceLocation, SpellProperties>> SPELLS_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, SpellProperties.CODEC);
        
        @Override
        public Class<MessageSpellsSync> type() {
            return MessageSpellsSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_spells");
        }
        
        public void encode(MessageSpellsSync message, RegistryFriendlyByteBuf buffer) {
            buffer.writeMap(message.spellsList(), FriendlyByteBuf::writeResourceLocation, (fbb, value) -> value.toNetwork(buffer));
        }
        
        public MessageSpellsSync decode(RegistryFriendlyByteBuf buf) {
            return new MessageSpellsSync(buf.readMap(FriendlyByteBuf::readResourceLocation, SpellProperties::fromNetwork));
        }
        
        @Override
        public Runnable handle(MessageSpellsSync message) {
            return () -> {
                IncompCore.LOGGER.info("Received spell list sync packet");
                ClientSpellManager.getInstance().updateSpellList(message.getSpellProperties());
            };
        }
        
    }
}
