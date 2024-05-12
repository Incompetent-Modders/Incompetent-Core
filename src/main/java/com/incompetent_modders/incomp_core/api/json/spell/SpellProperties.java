package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModSpeciesTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public record SpellProperties(SpellCategory category, double manaCost, int drawTime, ItemStack catalyst, ClassType.Value classType, SpeciesType.Value speciesType, SpellResults results, SoundEvent castSound) {
    public static final Codec<SpellProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellCategory.CODEC.optionalFieldOf("category", SpellCategory.UTILITY).forGetter(SpellProperties::category),
            Codec.DOUBLE.optionalFieldOf("mana_cost", 0.0).forGetter(SpellProperties::manaCost),
            Codec.INT.optionalFieldOf("draw_time", 0).forGetter(SpellProperties::drawTime),
            ItemStack.CODEC.optionalFieldOf("catalyst", ItemStack.EMPTY).forGetter(SpellProperties::catalyst),
            ClassType.Value.CODEC.optionalFieldOf("class_type", ClassType.valueOf(ModClassTypes.NONE)).forGetter(SpellProperties::classType),
            SpeciesType.Value.CODEC.optionalFieldOf("species_type", SpeciesType.valueOf(ModSpeciesTypes.HUMAN)).forGetter(SpellProperties::speciesType),
            SpellResults.CODEC.fieldOf("results").forGetter(SpellProperties::results),
            SoundEvent.DIRECT_CODEC.optionalFieldOf("cast_sound", SoundEvents.ALLAY_THROW).forGetter(SpellProperties::castSound)
    ).apply(instance, SpellProperties::new));
    
    public SpellProperties(SpellCategory category, int manaCost, int drawTime, ItemStack catalyst, ClassType.Value classType, SpeciesType.Value speciesType, SpellResults results) {
        this(category, manaCost, drawTime, catalyst, classType, speciesType, results, SoundEvents.ALLAY_THROW);
    }
    
    public boolean isBlankSpell() {
        return manaCost == 0.0 && drawTime == 0 && catalyst.isEmpty() && Objects.equals(classType.getClassType(), ModClassTypes.NONE.get()) && Objects.equals(speciesType.getSpecies(), ModSpeciesTypes.HUMAN.get()) && results.spellResult().isEmpty() && results.function().isEmpty();
    }
    
    public void executeCast(Player player) {
        Level level = player.level();
        ClassType playerClass = PlayerDataCore.ClassData.getPlayerClassType(player);
        SpeciesType playerSpecies = PlayerDataCore.SpeciesData.getSpecies(player);
        if (!checkPlayerClass(classType, playerClass) ||  !checkPlayerSpecies(speciesType, playerSpecies)) {
            IncompCore.LOGGER.info("{} does not meet class or species requirements to cast spell! required: {} | {}, has: {} | {}", player.getName().getString(), classTypeList(classType), speciesTypeList(speciesType), playerClass.getClassTypeIdentifier(), playerSpecies.getSpeciesTypeIdentifier());
            return;
        }
        int playerMana = (int) PlayerDataCore.ManaData.getMana(player);
        if (playerMana < manaCost) {
            IncompCore.LOGGER.info("{} doesn't have enough mana to cast spell!", player.getName().getString());
            return;
        }
        PlayerDataCore.ManaData.removeMana(player, getManaCost(player));
        handleCatalystConsumption(player);
        results.execute(player);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), castSound, player.getSoundSource(), 1.0F, 1.0F);
        CommonUtils.onCastEvent(level, player, player.getUsedItemHand());
        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(player.getUsedItemHand()).getItem()));
        IncompCore.LOGGER.info("Spell cast by {}", player.getName().getString());
    }
    public void executeCastNoRequirements(Player player) {
        Level level = player.level();
        PlayerDataCore.ManaData.removeMana(player, getManaCost(player));
        handleCatalystConsumption(player);
        results.execute(player);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), castSound, player.getSoundSource(), 1.0F, 1.0F);
        CommonUtils.onCastEvent(level, player, player.getUsedItemHand());
        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(player.getUsedItemHand()).getItem()));
        IncompCore.LOGGER.info("Spell cast by {}", player.getName().getString());
    }
    
    public boolean playerIsHoldingSpellCatalyst(Player player) {
        if (!catalyst().isEmpty()) {
            return player.getOffhandItem().is(catalyst().getItem());
        }
        return true;
    }
    public void handleCatalystConsumption(Player player) {
        if (!catalyst().isEmpty()) {
            return;
        }
        boolean catalystHasDurability = catalyst().isDamageableItem();
        boolean catalystIsUnbreakable = catalyst().has(DataComponents.UNBREAKABLE);
        if (playerIsHoldingSpellCatalyst(player)) {
            if (!player.isCreative()) {
                if (catalystHasDurability) {
                    catalyst().hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                } else {
                    if (!catalystIsUnbreakable) {
                        player.getOffhandItem().shrink(1);
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(catalyst().getItem()));
            } else
                player.awardStat(Stats.ITEM_USED.get(catalyst().getItem()));
        } else {
            Component message = Component.translatable("item.incompetent_core.spellcasting.catalyst_required", catalyst().getDisplayName());
            player.displayClientMessage(message, true);
        }
    }
    
    public double getManaCost(Player player) {
        AtomicReference<Float> mod = new AtomicReference<>(1.0F);
        if (player == null) return manaCost();
        if (!player.getActiveEffects().isEmpty()) {
            player.getActiveEffects().forEach(effect -> {
                float manaCostModifier = PotionEffectPropertyListener.getEffectProperties(effect.getEffect().value()) == null ? 1.0F : PotionEffectPropertyListener.getEffectProperties(effect.getEffect().value()).manaCostModifier();
                if (manaCostModifier != 1.0F) {
                    mod.set(manaCostModifier);
                }
            });
        }
        return manaCost() * mod.get();
    }
    
    public String classTypeList(ClassType.Value classTypes) {
        StringBuilder classTypeList = new StringBuilder();
        for (ClassType classType : classTypes.getClassType()) {
            classTypeList.append(classType.getClassTypeIdentifier()).append(" / ");
        }
        return classTypeList.toString();
    }
    public String speciesTypeList(SpeciesType.Value speciesTypes) {
        StringBuilder speciesTypeList = new StringBuilder();
        for (SpeciesType speciesType : speciesTypes.getSpecies()) {
            speciesTypeList.append(speciesType.getSpeciesTypeIdentifier()).append(" / ");
        }
        return speciesTypeList.toString();
    }
    
    public boolean checkPlayerClass(ClassType.Value requiredClass, ClassType playerClass) {
        if (requiredClass instanceof ClassType.TagValue tagValue) {
            return tagValue.getClassType().contains(playerClass);
        } else {
            return requiredClass.getClassType().contains(playerClass);
        }
    }
    
    public boolean checkPlayerSpecies(SpeciesType.Value requiredSpecies, SpeciesType playerSpecies) {
        if (requiredSpecies instanceof SpeciesType.TagValue tagValue) {
            return tagValue.getSpecies().contains(playerSpecies);
        } else {
            return requiredSpecies.getSpecies().contains(playerSpecies);
        }
    }
}
