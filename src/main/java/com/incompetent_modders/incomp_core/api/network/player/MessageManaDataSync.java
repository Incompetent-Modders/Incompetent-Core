package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.client.player_data.ClientManaData;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record MessageManaDataSync(double mana, double maxMana) implements Packet<MessageManaDataSync> {
    public static final ClientboundPacketType<MessageManaDataSync> TYPE = new MessageManaDataSync.Type();
    
    public static void sendToClient(Player player, double mana, double maxMana) {
        SyncHandler.DEFAULT_CHANNEL.sendToPlayersInLevel(new MessageManaDataSync(mana, maxMana), player.level());
    }
    
    @Override
    public PacketType<MessageManaDataSync> type() {
        return TYPE;
    }
    
    private static class Type implements ClientboundPacketType<MessageManaDataSync> {
        @Override
        public Class<MessageManaDataSync> type() {
            return MessageManaDataSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("mana_data_sync");
        }
        
        public void encode(MessageManaDataSync message, RegistryFriendlyByteBuf buf) {
            buf.writeDouble(message.mana());
            buf.writeDouble(message.maxMana());
        }
        
        public MessageManaDataSync decode(RegistryFriendlyByteBuf buf) {
            double mana = buf.readDouble();
            double maxMana = buf.readDouble();
            return new MessageManaDataSync(mana, maxMana);
        }
        
        @Override
        public Runnable handle(MessageManaDataSync message) {
            return () -> {
                double oldMana = ClientManaData.getInstance().getMana();
                double oldMaxMana = ClientManaData.getInstance().getMaxMana();
                if (oldMana != message.mana()) {
                    ClientManaData.getInstance().setMana(message.mana());
                    IncompCore.LOGGER.info("[CLIENT ONLY] Set client mana to {}", message.mana());
                }
                if (oldMaxMana != message.maxMana()) {
                    ClientManaData.getInstance().setMaxMana(message.maxMana());
                    IncompCore.LOGGER.info("[CLIENT ONLY] Set client max mana to {}", message.maxMana());
                }
            };
        }
    }
}
