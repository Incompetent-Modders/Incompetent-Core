package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.client.player_data.ClientSpeciesData;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record MessageSpeciesDataSync(ResourceLocation species, boolean invertHealAndHarm, ResourceLocation dietType, boolean keepOnDeath, int abilityCooldown) implements Packet<MessageSpeciesDataSync> {
    public static final ClientboundPacketType<MessageSpeciesDataSync> TYPE = new MessageSpeciesDataSync.Type();
    
    public static void sendToClient(Player player, ResourceLocation species, boolean invertHealAndHarm, ResourceLocation dietType, boolean keepOnDeath, int abilityCooldown) {
        SyncHandler.DEFAULT_CHANNEL.sendToPlayersInLevel(new MessageSpeciesDataSync(species, invertHealAndHarm, dietType, keepOnDeath, abilityCooldown), player.level());
    }
    
    @Override
    public PacketType<MessageSpeciesDataSync> type() {
        return TYPE;
    }
    
    private static class Type implements ClientboundPacketType<MessageSpeciesDataSync> {
        @Override
        public Class<MessageSpeciesDataSync> type() {
            return MessageSpeciesDataSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("species_data_sync");
        }
        
        public void encode(MessageSpeciesDataSync message, RegistryFriendlyByteBuf buf) {
            buf.writeResourceLocation(message.species);
            buf.writeBoolean(message.invertHealAndHarm);
            buf.writeResourceLocation(message.dietType);
            buf.writeBoolean(message.keepOnDeath);
            buf.writeInt(message.abilityCooldown);
        }
        
        public MessageSpeciesDataSync decode(RegistryFriendlyByteBuf buf) {
            var species = buf.readResourceLocation();
            var invertHealAndHarm = buf.readBoolean();
            var dietType = buf.readResourceLocation();
            var keepOnDeath = buf.readBoolean();
            var abilityCooldown = buf.readInt();
            return new MessageSpeciesDataSync(species, invertHealAndHarm, dietType, keepOnDeath, abilityCooldown);
        }
        
        @Override
        public Runnable handle(MessageSpeciesDataSync message) {
            return () -> {
                var oldSpecies = ClientSpeciesData.getInstance().getSpecies();
                var oldInvertedHealAndHarm = ClientSpeciesData.getInstance().isSpeciesInvertedHealAndHarm();
                var oldKeepOnDeath = ClientSpeciesData.getInstance().isKeepSpeciesOnDeath();
                var oldDiet = ClientSpeciesData.getInstance().getSpeciesDiet();
                var oldAbilityCooldown = ClientSpeciesData.getInstance().getSpeciesAbilityCooldown();
                if (oldSpecies != message.species()) {
                    ClientSpeciesData.getInstance().setSpecies(message.species());
                }
                if (oldInvertedHealAndHarm != message.invertHealAndHarm()) {
                    ClientSpeciesData.getInstance().setSpeciesInvertedHealAndHarm(message.invertHealAndHarm());
                }
                if (oldKeepOnDeath != message.keepOnDeath()) {
                    ClientSpeciesData.getInstance().setKeepSpeciesOnDeath(message.keepOnDeath());
                }
                if (oldDiet != message.dietType()) {
                    ClientSpeciesData.getInstance().setSpeciesDiet(message.dietType());
                }
                if (oldAbilityCooldown != message.abilityCooldown()) {
                    ClientSpeciesData.getInstance().setSpeciesAbilityCooldown(message.abilityCooldown());
                }
            };
        }
        
    }
}
