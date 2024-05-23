package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributes;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesAttributesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesProperties;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectProperties;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.network.MessagePlayerDataSync;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.registry.ModAbilities;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModClassPassiveEffects;
import com.incompetent_modders.incomp_core.util.CommonUtils;
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
    
    public static final String CLASS_DATA_ID = IncompCore.MODID + ":ClassData";
    public static final String SPECIES_DATA_ID = IncompCore.MODID + ":SpeciesData";
    public static final String MANA_DATA_ID = IncompCore.MODID + ":ManaData";
    
    public static CompoundTag getClassData(Player spe) {
        return spe.getPersistentData().getCompound(CLASS_DATA_ID);
    }
    
    public static CompoundTag getSpeciesData(Player spe) {
        return spe.getPersistentData().getCompound(SPECIES_DATA_ID);
    }
    
    public static CompoundTag getManaData(Player spe) {
        return spe.getPersistentData().getCompound(MANA_DATA_ID);
    }
    public static void syncClassData(ServerPlayer spe) {
        var msgCD = new MessagePlayerDataSync(new MessagePlayerDataSync.DataAndID(PlayerDataCore.getClassData(spe), CLASS_DATA_ID));
        PacketDistributor.sendToPlayer(spe, msgCD);
    }
    public static void syncSpeciesData(ServerPlayer spe) {
        var msgSD = new MessagePlayerDataSync(new MessagePlayerDataSync.DataAndID(PlayerDataCore.getSpeciesData(spe), SPECIES_DATA_ID));
        PacketDistributor.sendToPlayer(spe, msgSD);
    }
    public static void syncManaData(ServerPlayer spe) {
        var msgMD = new MessagePlayerDataSync(new MessagePlayerDataSync.DataAndID(PlayerDataCore.getManaData(spe), MANA_DATA_ID));
        PacketDistributor.sendToPlayer(spe, msgMD);
    }
    public static void syncAllData(ServerPlayer spe) {
        syncClassData(spe);
        syncSpeciesData(spe);
        syncManaData(spe);
    }
    
    public static void handleClassDataTick(ServerPlayer player, PlayerTickEvent event) {
        ResourceLocation classType = PlayerDataCore.ClassData.getPlayerClassType(player);
        ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
        AttributeInstance manaRegen = player.getAttribute(ModAttributes.MANA_REGEN);
        if (PlayerDataCore.ManaData.getMana(player) < PlayerDataCore.ManaData.getMaxMana(player) && manaRegen != null && PlayerDataCore.ClassData.canRegenMana(player)) {
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
            regenInterval++;
            if (regenInterval >= (20 / mod.get())) {
                PlayerDataCore.ManaData.healMana(player, manaRegen.getValue());
                CommonUtils.onManaHeal(player, manaRegen.getValue());
                regenInterval = 0;
            }
        }
        AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
        if (maxMana != null && classTypeProperties != null) {
            PlayerDataCore.ManaData.setMaxMana(player, classTypeProperties.maxMana());
        }
        classAbilityCooldownInterval++;
        if (classAbilityCooldownInterval >= 20) {
            if (PlayerDataCore.ClassData.getAbilityCooldown(player) > 0) {
                PlayerDataCore.ClassData.setAbilityCooldown(player, PlayerDataCore.ClassData.getAbilityCooldown(player) - 1);
            }
            classAbilityCooldownInterval = 0;
        }
        if (classType.equals(new ResourceLocation(IncompCore.MODID, "simple_human"))) {
            IncompCore.LOGGER.info("Player has old ID for default ClassType, setting to new default...");
            PlayerDataCore.ClassData.setPlayerClassType(player, CommonUtils.defaultClass);
        }
        if (classTypeProperties != null) {
            PlayerDataCore.ClassData.setPlayerClassType(player, classType);
            PlayerDataCore.ClassData.setCanRegenMana(player, classTypeProperties.canRegenerateMana(player, player.level()));
            PlayerDataCore.ClassData.setPacifist(player, classTypeProperties.pacifist());
            PlayerDataCore.ClassData.setAbility(player, classTypeProperties.ability().getType());
            PlayerDataCore.ClassData.setPassiveEffect(player, classTypeProperties.passiveEffect().getType());
            classTypeProperties.tickClassFeatures(event);
        }
    }
    public static void handleSpeciesDataTick(ServerPlayer player, PlayerTickEvent event) {
        ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
        speciesAbilityCooldownInterval++;
        if (speciesAbilityCooldownInterval >= 20) {
            if (PlayerDataCore.SpeciesData.getAbilityCooldown(player) > 0) {
                PlayerDataCore.SpeciesData.setAbilityCooldown(player, PlayerDataCore.SpeciesData.getAbilityCooldown(player) - 1);
            }
            speciesAbilityCooldownInterval = 0;
        }
        SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
        SpeciesAttributes speciesAttributes = SpeciesAttributesListener.getSpeciesTypeAttributes(speciesType);
        
        if (speciesProperties != null ) {
            PlayerDataCore.SpeciesData.setSpecies(player, speciesType);
            PlayerDataCore.SpeciesData.setInvertedHealAndHarm(player, speciesProperties.invertHealAndHarm());
            PlayerDataCore.SpeciesData.setKeepOnDeath(player, speciesProperties.keepOnDeath());
            PlayerDataCore.SpeciesData.setDiet(player, speciesProperties.dietType());
            PlayerDataCore.SpeciesData.setBehaviour(player, speciesProperties.behaviour().getType());
            PlayerDataCore.SpeciesData.setAbility(player, speciesProperties.ability().getType());
            speciesProperties.tickSpeciesAttributes(player);
            if (speciesAttributes != null) {
                PlayerDataCore.SpeciesData.setMaxHealth(player, speciesAttributes.maxHealth());
                PlayerDataCore.SpeciesData.setAttackDamage(player, speciesAttributes.attackDamage());
                PlayerDataCore.SpeciesData.setAttackKnockback(player, speciesAttributes.attackKnockback());
                PlayerDataCore.SpeciesData.setMovementSpeed(player, speciesAttributes.moveSpeed());
                PlayerDataCore.SpeciesData.setArmor(player, speciesAttributes.armour());
                PlayerDataCore.SpeciesData.setLuck(player, speciesAttributes.luck());
                PlayerDataCore.SpeciesData.setBlockInteractionRange(player, speciesAttributes.blockInteractionRange());
                PlayerDataCore.SpeciesData.setEntityInteractionRange(player, speciesAttributes.entityInteractionRange());
                PlayerDataCore.SpeciesData.setGravity(player, speciesAttributes.gravity());
                PlayerDataCore.SpeciesData.setJumpStrength(player, speciesAttributes.jumpStrength());
                PlayerDataCore.SpeciesData.setKnockbackResistance(player, speciesAttributes.knockbackResistance());
                PlayerDataCore.SpeciesData.setSafeFallDistance(player, speciesAttributes.safeFallDistance());
                PlayerDataCore.SpeciesData.setScale(player, speciesAttributes.scale());
                PlayerDataCore.SpeciesData.setStepHeight(player, speciesAttributes.stepHeight());
                PlayerDataCore.SpeciesData.setArmourToughness(player, speciesAttributes.armourToughness());
            }
        }
    }
    
    
    public static class ClassData {
        public static ResourceLocation getPlayerClassType(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return new ResourceLocation(nc.getString("classType"));
        }
        public static boolean canRegenMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return nc.getBoolean("canRegenMana");
        }
        public static boolean isPacifist(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return nc.getBoolean("isPacifist");
        }
        public static ClassPassiveEffectType<?> getPassiveEffect(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return ModRegistries.CLASS_PASSIVE_EFFECT_TYPE.get(new ResourceLocation(nc.getString("passiveEffect")));
        }
        public static AbilityType<?> getAbility(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return ModRegistries.ABILITY_TYPE.get(new ResourceLocation(nc.getString("ability")));
        }
        public static void setPlayerClassType(Player spe, ResourceLocation ct) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (ct == null)
                ct = CommonUtils.defaultClass;
            if (nc.contains("class_type"))
                nc.remove("class_type");
            nc.putString("classType", ct.toString());
        }
        public static void setCanRegenMana(Player spe, boolean canRegen) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            nc.putBoolean("canRegenMana", canRegen);
        }
        public static void setPacifist(Player spe, boolean pacifist) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            nc.putBoolean("isPacifist", pacifist);
        }
        public static int getAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            return nc.getInt("abilityCooldown");
        }
        
        public static void setAbilityCooldown(Player spe, int cooldown) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            nc.putInt("abilityCooldown", cooldown);
        }
        public static void decrementAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            int cooldown = nc.getInt("abilityCooldown");
            if (cooldown > 0)
                nc.putInt("abilityCooldown", cooldown - 1);
        }
        public static boolean canUseAbility(Player spe) {
            return getAbilityCooldown(spe) <= 0;
        }
        public static void setPassiveEffect(Player spe, ClassPassiveEffectType<?> passiveEffect) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (passiveEffect == null)
                passiveEffect = ModClassPassiveEffects.DEFAULT.get();
            if (nc.contains("passiveEffect"))
                nc.remove("passiveEffect");
            nc.putString("passiveEffect", passiveEffect.getClassPassiveEffectTypeIdentifier().toString());
        }
        public static void setAbility(Player spe, AbilityType<?> ability) {
            CompoundTag nc = spe.getPersistentData().getCompound(CLASS_DATA_ID);
            if (ability == null)
                ability = ModAbilities.DEFAULT.get();
            if (nc.contains("ability"))
                nc.remove("ability");
            nc.putString("ability", ability.getAbilityTypeIdentifier().toString());
        }
    }
    
    public static class SpeciesData {
        public static ResourceLocation getSpecies(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return new ResourceLocation(nc.getString("species"));
        }
        public static boolean isInvertedHealAndHarm(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getBoolean("invertedHealAndHarm");
        }
        public static boolean shouldKeepOnDeath(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getBoolean("keepOnDeath");
        }
        public static ResourceLocation getDiet(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return new ResourceLocation(nc.getString("diet"));
        }
        public static double getMaxHealth(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("maxHealth");
        }
        public static double getAttackDamage(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("attackDamage");
        }
        public static double getAttackKnockback(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("attackKnockback");
        }
        public static double getMovementSpeed(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("movementSpeed");
        }
        public static double getArmor(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("armor");
        }
        public static double getLuck(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("luck");
        }
        public static double getBlockInteractionRange(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("blockInteractionRange");
        }
        public static double getEntityInteractionRange(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("entityInteractionRange");
        }
        public static double getGravity(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("gravity");
        }
        public static double getJumpStrength(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("jumpStrength");
        }
        public static double getKnockbackResistance(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("knockbackResistance");
        }
        public static double getSafeFallDistance(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("safeFallDistance");
        }
        public static double getScale(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("scale");
        }
        public static double getStepHeight(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("stepHeight");
        }
        public static double getArmourToughness(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getDouble("armourToughness");
        }
        public static SpeciesBehaviourType<?> getBehaviour(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return ModRegistries.SPECIES_BEHAVIOUR_TYPE.get(new ResourceLocation(nc.getString("behaviour")));
        }
        
        public static void setSpecies(Player spe, ResourceLocation st) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (st == null)
                st = CommonUtils.defaultSpecies;
            if (nc.contains("species"))
                nc.remove("species");
            nc.putString("species", st.toString());
        }
        public static void setInvertedHealAndHarm(Player spe, boolean invertedHealAndHarm) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            
                
            nc.putBoolean("invertedHealAndHarm", invertedHealAndHarm);
        }
        public static void setKeepOnDeath(Player spe, boolean keepOnDeath) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putBoolean("keepOnDeath", keepOnDeath);
        }
        public static void setDiet(Player spe, ResourceLocation diet) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (diet == null)
                diet = CommonUtils.defaultDiet;
            if (nc.contains("diet"))
                nc.remove("diet");
            nc.putString("diet", diet.toString());
        }
        public static void setMaxHealth(Player spe, double maxHealth) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("maxHealth", maxHealth);
        }
        public static void setAttackDamage(Player spe, double attackDamage) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("attackDamage", attackDamage);
        }
        public static void setAttackKnockback(Player spe, double attackKnockback) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("attackKnockback", attackKnockback);
        }
        public static void setMovementSpeed(Player spe, double movementSpeed) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("movementSpeed", movementSpeed);
        }
        public static void setArmor(Player spe, double armor) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("armor", armor);
        }
        public static void setLuck(Player spe, double luck) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("luck", luck);
        }
        public static void setBlockInteractionRange(Player spe, double blockInteractionRange) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("blockInteractionRange", blockInteractionRange);
        }
        public static void setEntityInteractionRange(Player spe, double entityInteractionRange) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("entityInteractionRange", entityInteractionRange);
        }
        public static void setGravity(Player spe, double gravity) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("gravity", gravity);
        }
        public static void setJumpStrength(Player spe, double jumpStrength) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("jumpStrength", jumpStrength);
        }
        public static void setKnockbackResistance(Player spe, double knockbackResistance) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("knockbackResistance", knockbackResistance);
        }
        public static void setSafeFallDistance(Player spe, double safeFallDistance) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("safeFallDistance", safeFallDistance);
        }
        public static void setScale(Player spe, double scale) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("scale", scale);
        }
        public static void setStepHeight(Player spe, double stepHeight) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("stepHeight", stepHeight);
        }
        public static void setArmourToughness(Player spe, double armourToughness) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putDouble("armourToughness", armourToughness);
        }
        public static void setBehaviour(Player spe, SpeciesBehaviourType<?> behaviour) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (behaviour == null)
                behaviour = SpeciesListener.getSpeciesTypeProperties(CommonUtils.defaultSpecies).behaviour().getType();
            if (nc.contains("behaviour"))
                nc.remove("behaviour");
            nc.putString("behaviour", behaviour.getBehaviourTypeIdentifier().toString());
        }
        public static AbilityType<?> getAbility(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return ModRegistries.ABILITY_TYPE.get(new ResourceLocation(nc.getString("ability")));
        }
        public static int getAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            return nc.getInt("abilityCooldown");
        }
        
        public static void setAbilityCooldown(Player spe, int cooldown) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            nc.putInt("abilityCooldown", cooldown);
        }
        public static void decrementAbilityCooldown(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            int cooldown = nc.getInt("abilityCooldown");
            if (cooldown > 0)
                nc.putInt("abilityCooldown", cooldown - 1);
        }
        public static boolean canUseAbility(Player spe) {
            return getAbilityCooldown(spe) <= 0;
        }
        public static void setAbility(Player spe, AbilityType<?> ability) {
            CompoundTag nc = spe.getPersistentData().getCompound(SPECIES_DATA_ID);
            if (ability == null)
                ability = ModAbilities.DEFAULT.get();
            if (nc.contains("ability"))
                nc.remove("ability");
            nc.putString("ability", ability.getAbilityTypeIdentifier().toString());
        }
    }
    public static class ManaData {
        public static double getMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            return nc.getDouble("mana");
        }
        public static void setMana(Player spe, double mana) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            nc.putDouble("mana", mana);
        }
        public static double getMaxMana(Player spe) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            return nc.getInt("maxMana");
        }
        public static void setMaxMana(Player spe, double maxMana) {
            CompoundTag nc = spe.getPersistentData().getCompound(MANA_DATA_ID);
            nc.putDouble("maxMana", maxMana);
        }
        public static double removeMana(Player spe, double manaToRemove) {
            if (manaToRemove < 0)
                manaToRemove = 0;
            setMana(spe, getMana(spe) - manaToRemove);
            return getMana(spe);
        }
        public static void healMana(Player spe, double amount) {
            amount = CommonUtils.onManaHeal(spe, amount);
            if (amount <= 0) return;
            double f = getMana(spe);
            if (f < getMaxMana(spe)) {
                setMana(spe, Math.min(f + amount, getMaxMana(spe)));
            }
        }
    }
    public static void setClassData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(CLASS_DATA_ID, nc);
    }
    public static void setSpeciesData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(SPECIES_DATA_ID, nc);
    }
    public static void setManaData(Player spe, CompoundTag nc) {
        spe.getPersistentData().put(MANA_DATA_ID, nc);
    }
    public static void setData(Player spe, String id, CompoundTag nc) {
        spe.getPersistentData().put(id, nc);
    }
    
}
