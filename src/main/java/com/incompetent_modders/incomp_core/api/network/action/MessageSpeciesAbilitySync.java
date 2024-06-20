package com.incompetent_modders.incomp_core.api.network.action;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesProperties;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record MessageSpeciesAbilitySync(boolean applyCooldown) implements Packet<MessageSpeciesAbilitySync> {
    public static final ServerboundPacketType<MessageSpeciesAbilitySync> TYPE = new MessageSpeciesAbilitySync.Type();
    
    public static void sendToServer(boolean applyCooldown) {
        SyncHandler.DEFAULT_CHANNEL.sendToServer(new MessageSpeciesAbilitySync(applyCooldown));
    }
    
    @Override
    public PacketType<MessageSpeciesAbilitySync> type() {
        return TYPE;
    }
    
    private static class Type implements ServerboundPacketType<MessageSpeciesAbilitySync> {
        @Override
        public Class<MessageSpeciesAbilitySync> type() {
            return MessageSpeciesAbilitySync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_species_ability");
        }
        
        @Override
        public void encode(MessageSpeciesAbilitySync messageClassAbilitySync, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            registryFriendlyByteBuf.writeBoolean(messageClassAbilitySync.applyCooldown());
        }
        
        @Override
        public MessageSpeciesAbilitySync decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var applyCooldown = registryFriendlyByteBuf.readBoolean();
            return new MessageSpeciesAbilitySync(applyCooldown);
        }
        
        @Override
        public Consumer<Player> handle(MessageSpeciesAbilitySync messageSpeciesAbilitySync) {
            return (player) -> {
                ResourceLocation species = SpeciesData.Get.playerSpecies(player);
                SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(species);
                boolean canUseAbility = SpeciesData.Util.canUseAbility(player);
                if (canUseAbility) {
                    speciesProperties.ability().apply(player.level(), player);
                    SpeciesData.Set.abilityCooldown(player, messageSpeciesAbilitySync.applyCooldown() ? speciesProperties.abilityCooldown() : 0);
                }
            };
        }
    }
}
