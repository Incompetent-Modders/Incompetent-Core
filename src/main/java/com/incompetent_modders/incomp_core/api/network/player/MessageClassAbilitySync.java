package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.network.MessageSpellSlotScrollSync;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpellsSync;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Consumer;

public record MessageClassAbilitySync(boolean applyCooldown) implements Packet<MessageClassAbilitySync> {
    public static final ServerboundPacketType<MessageClassAbilitySync> TYPE = new MessageClassAbilitySync.Type();
    
    public static void sendToServer(boolean applyCooldown) {
        SyncHandler.DEFAULT_CHANNEL.sendToServer(new MessageClassAbilitySync(applyCooldown));
    }
    
    @Override
    public PacketType<MessageClassAbilitySync> type() {
        return TYPE;
    }
    
    private static class Type implements ServerboundPacketType<MessageClassAbilitySync> {
        @Override
        public Class<MessageClassAbilitySync> type() {
            return MessageClassAbilitySync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("sync_class_ability");
        }
        
        @Override
        public void encode(MessageClassAbilitySync messageClassAbilitySync, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            registryFriendlyByteBuf.writeBoolean(messageClassAbilitySync.applyCooldown());
        }
        
        @Override
        public MessageClassAbilitySync decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            var applyCooldown = registryFriendlyByteBuf.readBoolean();
            return new MessageClassAbilitySync(applyCooldown);
        }
        
        @Override
        public Consumer<Player> handle(MessageClassAbilitySync messageClassAbilitySync) {
            return (player) -> {
                ResourceLocation classType = ClassData.Get.playerClassType(player);
                ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
                boolean canUseAbility = ClassData.Util.canUseAbility(player);
                if (canUseAbility) {
                    classTypeProperties.ability().apply(player.level(), player);
                    ClassData.Set.abilityCooldown(player, messageClassAbilitySync.applyCooldown() ? classTypeProperties.abilityCooldown() : 0);
                }
            };
        }
    }
}
