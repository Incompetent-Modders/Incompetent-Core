package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributes;
import com.incompetent_modders.incomp_core.api.network.SyncHandler;
import com.incompetent_modders.incomp_core.client.player_data.ClientSpeciesData;
import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record MessageSpeciesAttributesSync(SpeciesAttributes speciesAttributes) implements Packet<MessageSpeciesAttributesSync> {
    public static final ClientboundPacketType<MessageSpeciesAttributesSync> TYPE = new MessageSpeciesAttributesSync.Type();
    
    public static void sendToClient(Player player, SpeciesAttributes speciesAttributes) {
        SyncHandler.DEFAULT_CHANNEL.sendToPlayersInLevel(new MessageSpeciesAttributesSync(speciesAttributes), player.level());
    }
    
    @Override
    public PacketType<MessageSpeciesAttributesSync> type() {
        return TYPE;
    }
    
    private static class Type implements ClientboundPacketType<MessageSpeciesAttributesSync> {
        @Override
        public Class<MessageSpeciesAttributesSync> type() {
            return MessageSpeciesAttributesSync.class;
        }
        
        @Override
        public ResourceLocation id() {
            return IncompCore.makeId("species_attributes_sync");
        }
        
        public void encode(MessageSpeciesAttributesSync message, RegistryFriendlyByteBuf buf) {
            buf.writeFloat(message.speciesAttributes.maxHealth());
            buf.writeFloat(message.speciesAttributes.attackDamage());
            buf.writeFloat(message.speciesAttributes.attackKnockback());
            buf.writeFloat(message.speciesAttributes.moveSpeed());
            buf.writeFloat(message.speciesAttributes.armour());
            buf.writeFloat(message.speciesAttributes.luck());
            buf.writeFloat(message.speciesAttributes.blockInteractionRange());
            buf.writeFloat(message.speciesAttributes.entityInteractionRange());
            buf.writeFloat(message.speciesAttributes.gravity());
            buf.writeFloat(message.speciesAttributes.jumpStrength());
            buf.writeFloat(message.speciesAttributes.knockbackResistance());
            buf.writeFloat(message.speciesAttributes.safeFallDistance());
            buf.writeFloat(message.speciesAttributes.scale());
            buf.writeFloat(message.speciesAttributes.stepHeight());
            buf.writeFloat(message.speciesAttributes.armourToughness());
        }
        
        public MessageSpeciesAttributesSync decode(RegistryFriendlyByteBuf buf) {
            float maxHealth = buf.readFloat();
            float attackDamage = buf.readFloat();
            float attackKnockback = buf.readFloat();
            float moveSpeed = buf.readFloat();
            float armour = buf.readFloat();
            float luck = buf.readFloat();
            float blockInteractionRange = buf.readFloat();
            float entityInteractionRange = buf.readFloat();
            float gravity = buf.readFloat();
            float jumpStrength = buf.readFloat();
            float knockbackResistance = buf.readFloat();
            float safeFallDistance = buf.readFloat();
            float scale = buf.readFloat();
            float stepHeight = buf.readFloat();
            float armourToughness = buf.readFloat();
            return new MessageSpeciesAttributesSync(new SpeciesAttributes(maxHealth, attackDamage, attackKnockback, moveSpeed, armour, luck, blockInteractionRange, entityInteractionRange, gravity, jumpStrength, knockbackResistance, safeFallDistance, scale, stepHeight, armourToughness));
        }
        
        @Override
        public Runnable handle(MessageSpeciesAttributesSync message) {
            return () -> {
                float oldMaxHealth = (float) ClientSpeciesData.getInstance().getSpeciesMaxHealth();
                float oldAttackDamage = (float) ClientSpeciesData.getInstance().getSpeciesAttackDamage();
                float oldAttackKnockback = (float) ClientSpeciesData.getInstance().getSpeciesAttackKnockback();
                float oldMoveSpeed = (float) ClientSpeciesData.getInstance().getSpeciesMovementSpeed();
                float oldArmour = (float) ClientSpeciesData.getInstance().getSpeciesArmor();
                float oldLuck = (float) ClientSpeciesData.getInstance().getSpeciesLuck();
                float oldBlockInteractionRange = (float) ClientSpeciesData.getInstance().getSpeciesBlockInteractionRange();
                float oldEntityInteractionRange = (float) ClientSpeciesData.getInstance().getSpeciesEntityInteractionRange();
                float oldGravity = (float) ClientSpeciesData.getInstance().getSpeciesGravity();
                float oldJumpStrength = (float) ClientSpeciesData.getInstance().getSpeciesJumpStrength();
                float oldKnockbackResistance = (float) ClientSpeciesData.getInstance().getSpeciesKnockbackResistance();
                float oldSafeFallDistance = (float) ClientSpeciesData.getInstance().getSpeciesSafeFallDistance();
                float oldScale = (float) ClientSpeciesData.getInstance().getSpeciesScale();
                float oldStepHeight = (float) ClientSpeciesData.getInstance().getSpeciesStepHeight();
                float oldArmourToughness = (float) ClientSpeciesData.getInstance().getSpeciesArmourToughness();
                List<String> toMention = new ArrayList<>();
                if (oldMaxHealth != message.speciesAttributes.maxHealth()) {
                    ClientSpeciesData.getInstance().setSpeciesMaxHealth(message.speciesAttributes.maxHealth());
                    toMention.add("maxHealth");
                }
                if (oldAttackDamage != message.speciesAttributes.attackDamage()) {
                    ClientSpeciesData.getInstance().setSpeciesAttackDamage(message.speciesAttributes.attackDamage());
                    toMention.add("attackDamage");
                }
                if (oldAttackKnockback != message.speciesAttributes.attackKnockback()) {
                    ClientSpeciesData.getInstance().setSpeciesAttackKnockback(message.speciesAttributes.attackKnockback());
                    toMention.add("attackKnockback");
                }
                if (oldMoveSpeed != message.speciesAttributes.moveSpeed()) {
                    ClientSpeciesData.getInstance().setSpeciesMovementSpeed(message.speciesAttributes.moveSpeed());
                    toMention.add("moveSpeed");
                }
                if (oldArmour != message.speciesAttributes.armour()) {
                    ClientSpeciesData.getInstance().setSpeciesArmor(message.speciesAttributes.armour());
                    toMention.add("armour");
                }
                if (oldLuck != message.speciesAttributes.luck()) {
                    ClientSpeciesData.getInstance().setSpeciesLuck(message.speciesAttributes.luck());
                    toMention.add("luck");
                }
                if (oldBlockInteractionRange != message.speciesAttributes.blockInteractionRange()) {
                    ClientSpeciesData.getInstance().setSpeciesBlockInteractionRange(message.speciesAttributes.blockInteractionRange());
                    toMention.add("blockInteractionRange");
                }
                if (oldEntityInteractionRange != message.speciesAttributes.entityInteractionRange()) {
                    ClientSpeciesData.getInstance().setSpeciesEntityInteractionRange(message.speciesAttributes.entityInteractionRange());
                    toMention.add("entityInteractionRange");
                }
                if (oldGravity != message.speciesAttributes.gravity()) {
                    ClientSpeciesData.getInstance().setSpeciesGravity(message.speciesAttributes.gravity());
                    toMention.add("gravity");
                }
                if (oldJumpStrength != message.speciesAttributes.jumpStrength()) {
                    ClientSpeciesData.getInstance().setSpeciesJumpStrength(message.speciesAttributes.jumpStrength());
                    toMention.add("jumpStrength");
                }
                if (oldKnockbackResistance != message.speciesAttributes.knockbackResistance()) {
                    ClientSpeciesData.getInstance().setSpeciesKnockbackResistance(message.speciesAttributes.knockbackResistance());
                    toMention.add("knockbackResistance");
                }
                if (oldSafeFallDistance != message.speciesAttributes.safeFallDistance()) {
                    ClientSpeciesData.getInstance().setSpeciesSafeFallDistance(message.speciesAttributes.safeFallDistance());
                    toMention.add("safeFallDistance");
                }
                if (oldScale != message.speciesAttributes.scale()) {
                    ClientSpeciesData.getInstance().setSpeciesScale(message.speciesAttributes.scale());
                    toMention.add("scale");
                }
                if (oldStepHeight != message.speciesAttributes.stepHeight()) {
                    ClientSpeciesData.getInstance().setSpeciesStepHeight(message.speciesAttributes.stepHeight());
                    toMention.add("stepHeight");
                }
                if (oldArmourToughness != message.speciesAttributes.armourToughness()) {
                    ClientSpeciesData.getInstance().setSpeciesArmourToughness(message.speciesAttributes.armourToughness());
                    toMention.add("armourToughness");
                }
                if (!toMention.isEmpty()) {
                    IncompCore.LOGGER.info("Updated species attributes: {}", toMention);
                }
            };
        }
    }
    
}
