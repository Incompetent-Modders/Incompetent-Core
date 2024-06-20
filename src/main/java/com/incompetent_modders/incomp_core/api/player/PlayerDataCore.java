package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributes;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesProperties;
import com.incompetent_modders.incomp_core.api.json.potion.PotionEffectProperties;
import com.incompetent_modders.incomp_core.api.json.potion.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.network.player.MessageManaDataSync;
import com.incompetent_modders.incomp_core.api.network.player.MessagePlayerDataSync;
import com.incompetent_modders.incomp_core.api.network.player.MessageSpeciesAttributesSync;
import com.incompetent_modders.incomp_core.common.registry.ModAttributes;
import com.incompetent_modders.incomp_core.common.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class PlayerDataCore {
    static float regenInterval = 0;
    static float classAbilityCooldownInterval = 0;
    static float speciesAbilityCooldownInterval = 0;
    
    public static final String PLAYER_DATA_ID = IncompCore.MODID + ":PlayerData";
    public static final String CLASS_DATA_ID = "ClassData";
    public static final String SPECIES_DATA_ID = "SpeciesData";
    public static final String MANA_DATA_ID = "ManaData";
    
    public static CompoundTag getPlayerData(Player spe) {
        return spe.getPersistentData().getCompound(PLAYER_DATA_ID);
    }
    
    public static void handleClassDataTick(ServerPlayer player, PlayerTickEvent event) {
        ResourceLocation classType = ClassData.Get.playerClassType(player);
        CompoundTag playerData = getPlayerData(player);
        if (ClassData.Util.isClassPresent(player)) {
            ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
            AttributeInstance manaRegen = player.getAttribute(ModAttributes.MANA_REGEN);
            ClassData.Util.decrementAbilityCooldown(player);
            if (ManaData.Get.mana(player) < ManaData.Get.maxMana(player) && manaRegen != null && ClassData.Get.canRegenMana(player)) {
                AtomicReference<Float> mod = new AtomicReference<>(1.0F);
                if (!player.getActiveEffects().isEmpty()) {
                    player.getActiveEffects().forEach(effect -> {
                        PotionEffectProperties properties = PotionEffectPropertyListener.getEffectProperties(effect.getEffect().value());
                        if (properties != null) {
                            if (properties.manaRegenModifier() != 1.0F) {
                                mod.set(properties.manaRegenModifier());
                            }
                        }
                    });
                }
                {
                    regenInterval++;
                    if (regenInterval < (20 / mod.get())) {
                        return;
                    }
                    ManaData.Util.healMana(player, manaRegen.getValue());
                    Utils.onManaHeal(player, manaRegen.getValue());
                    MessageManaDataSync.sendToClient(player, ManaData.Get.mana(player), ManaData.Get.maxMana(player));
                    regenInterval = 0;
                }
            }
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
            {
                if (maxMana == null) {
                    return;
                }
                
                if (classTypeProperties == null) {
                    return;
                }
                
                ManaData.Set.maxMana(player, classTypeProperties.maxMana());
                MessageManaDataSync.sendToClient(player, ManaData.Get.mana(player), ManaData.Get.maxMana(player));
            }
            {
                classAbilityCooldownInterval++;
                if (classAbilityCooldownInterval < 20) {
                    return;
                }
                if (ClassData.Get.abilityCooldown(player) <= 0) {
                    return;
                }
                
                IncompCore.LOGGER.info("Decrementing class ability cooldown for player {}", player.getName().getString());
                ClassData.Set.abilityCooldown(player, ClassData.Get.abilityCooldown(player) - 1);
                MessagePlayerDataSync.sendToClient(player, playerData);
                classAbilityCooldownInterval = 0;
            }
            if (classType.equals(ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "simple_human"))) {
                IncompCore.LOGGER.info("Player has old ID for default ClassType, setting to new default...");
                ClassData.Set.playerClassType(player, Utils.defaultClass);
                MessagePlayerDataSync.sendToClient(player, playerData);
            }
            
            ClassData.Set.playerClassType(player, classType);
            ClassData.Set.canRegenMana(player, classTypeProperties.canRegenerateMana(player, player.level()));
            ClassData.Set.isPacifist(player, classTypeProperties.pacifist());
            ClassData.Set.ability(player, classTypeProperties.ability().getType());
            ClassData.Set.passiveEffect(player, classTypeProperties.passiveEffect().getType());
            classTypeProperties.tickClassFeatures(event);
        }
    }
    public static void handleSpeciesDataTick(ServerPlayer player, PlayerTickEvent event) {
        ResourceLocation speciesType = SpeciesData.Get.playerSpecies(player);
        CompoundTag playerData = getPlayerData(player);
        if (SpeciesData.Util.isSpeciesPresent(player)) {
            {
                speciesAbilityCooldownInterval++;
                
                if (speciesAbilityCooldownInterval < 20) {
                    return;
                }
                
                speciesAbilityCooldownInterval = 0;
                
                if (SpeciesData.Get.abilityCooldown(player) <= 0) {
                    return;
                }
                
                SpeciesData.Set.abilityCooldown(player, SpeciesData.Get.abilityCooldown(player) - 1);
                MessagePlayerDataSync.sendToClient(player, playerData);
            }
            
            SpeciesData.Util.decrementAbilityCooldown(player);
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
            SpeciesAttributes speciesAttributes = SpeciesAttributesListener.getSpeciesTypeAttributes(speciesType);
            {
                if (speciesProperties == null) {
                    return;
                }
                SpeciesData.Set.playerSpecies(player, speciesType);
                SpeciesData.Set.isInvertedHealAndHarm(player, speciesProperties.invertHealAndHarm());
                SpeciesData.Set.keepOnDeath(player, speciesProperties.keepOnDeath());
                SpeciesData.Set.diet(player, speciesProperties.dietType());
                SpeciesData.Set.behaviour(player, speciesProperties.behaviour().getType());
                SpeciesData.Set.ability(player, speciesProperties.ability().getType());
                speciesProperties.tickSpeciesAttributes(player);
                {
                    if (speciesAttributes == null) {
                        return;
                    }
                    SpeciesData.Set.Attributes.maxHealth(player, speciesAttributes.maxHealth());
                    SpeciesData.Set.Attributes.attackDamage(player, speciesAttributes.attackDamage());
                    SpeciesData.Set.Attributes.attackKnockback(player, speciesAttributes.attackKnockback());
                    SpeciesData.Set.Attributes.movementSpeed(player, speciesAttributes.moveSpeed());
                    SpeciesData.Set.Attributes.armor(player, speciesAttributes.armour());
                    SpeciesData.Set.Attributes.luck(player, speciesAttributes.luck());
                    SpeciesData.Set.Attributes.blockInteractionRange(player, speciesAttributes.blockInteractionRange());
                    SpeciesData.Set.Attributes.entityInteractionRange(player, speciesAttributes.entityInteractionRange());
                    SpeciesData.Set.Attributes.gravity(player, speciesAttributes.gravity());
                    SpeciesData.Set.Attributes.jumpStrength(player, speciesAttributes.jumpStrength());
                    SpeciesData.Set.Attributes.knockbackResistance(player, speciesAttributes.knockbackResistance());
                    SpeciesData.Set.Attributes.safeFallDistance(player, speciesAttributes.safeFallDistance());
                    SpeciesData.Set.Attributes.scale(player, speciesAttributes.scale());
                    SpeciesData.Set.Attributes.stepHeight(player, speciesAttributes.stepHeight());
                    SpeciesData.Set.Attributes.armourToughness(player, speciesAttributes.armourToughness());
                    MessageSpeciesAttributesSync.sendToClient(player, speciesAttributes);
                }
            }
            
        }
    }
    
    public static void setPlayerData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(PLAYER_DATA_ID, nc);
    }
    public static void updatePlayerData(ServerPlayer player) {
        CompoundTag playerData = getPlayerData(player);
        setPlayerData(player, playerData);
        MessagePlayerDataSync.sendToClient(player, playerData);
    }
    public static void setData(Player spe, String id, CompoundTag nc) {
        spe.getPersistentData().put(id, nc);
    }
    
}
