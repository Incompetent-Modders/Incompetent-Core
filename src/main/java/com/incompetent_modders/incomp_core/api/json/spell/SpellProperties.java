package com.incompetent_modders.incomp_core.api.json.spell;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public record SpellProperties(SpellCategory category, double manaCost, int drawTime, ItemStack catalyst, ClassType classType, SpeciesType speciesType, SpellResults results, SoundEvent castSound) {
    public static final Codec<SpellProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellCategory.CODEC.optionalFieldOf("category", SpellCategory.UTILITY).forGetter(SpellProperties::category),
            Codec.DOUBLE.optionalFieldOf("mana_cost", 0.0).forGetter(SpellProperties::manaCost),
            Codec.INT.optionalFieldOf("draw_time", 0).forGetter(SpellProperties::drawTime),
            ItemStack.CODEC.optionalFieldOf("catalyst", ItemStack.EMPTY).forGetter(SpellProperties::catalyst),
            ClassType.CODEC.fieldOf("class").forGetter(SpellProperties::classType),
            SpeciesType.CODEC.fieldOf("species").forGetter(SpellProperties::speciesType),
            SpellResults.CODEC.fieldOf("results").forGetter(SpellProperties::results),
            SoundEvent.DIRECT_CODEC.optionalFieldOf("cast_sound", SoundEvents.ALLAY_THROW).forGetter(SpellProperties::castSound)
    ).apply(instance, SpellProperties::new));
    
    public SpellProperties(SpellCategory category, int manaCost, int drawTime, ItemStack catalyst, ClassType classType, SpeciesType speciesType, SpellResults results) {
        this(category, manaCost, drawTime, catalyst, classType, speciesType, results, SoundEvents.ALLAY_THROW);
    }
    
    public boolean isBlankSpell() {
        return manaCost == 0.0 && drawTime == 0 && catalyst.isEmpty() && Objects.equals(classType, CommonUtils.defaultClass) && Objects.equals(speciesType.speciesID(), CommonUtils.defaultSpecies) && results.spellResult().isEmpty() && results.function().isEmpty();
    }
    
    public void executeCast(Player player) {
        Level level = player.level();
        ResourceLocation playerClass = PlayerDataCore.ClassData.getPlayerClassType(player);
        ResourceLocation playerSpecies = PlayerDataCore.SpeciesData.getSpecies(player);
        if (!checkPlayerClass(classType, playerClass) ||  !checkPlayerSpecies(speciesType, playerSpecies)) {
            generateClassSpeciesLogger(player);
            return;
        }
        int playerMana = (int) PlayerDataCore.ManaData.getMana(player);
        if (playerMana < getManaCost(player)) {
            IncompCore.LOGGER.info("{} doesn't have enough mana to cast spell!", player.getName().getString());
            return;
        }
        if (!playerIsHoldingSpellCatalyst(player)) {
            IncompCore.LOGGER.info("{} is not holding the required catalyst to cast spell!", player.getName().getString());
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
        results.execute(player);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), castSound, player.getSoundSource(), 1.0F, 1.0F);
        CommonUtils.onCastEvent(level, player, player.getUsedItemHand());
        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(player.getUsedItemHand()).getItem()));
        IncompCore.LOGGER.info("Spell cast by {}", player.getName().getString());
    }
    public boolean hasSpellCatalyst() {
        return !this.catalyst().isEmpty();
    }
    public boolean playerIsHoldingSpellCatalyst(Player player) {
        if (!catalyst().isEmpty() && player != null) {
            return ItemStack.isSameItemSameComponents(player.getOffhandItem(), catalyst());
        }
        return true;
    }
    public void handleCatalystConsumption(Player player) {
        if (!catalyst().isEmpty()) {
            return;
        }
        boolean catalystHasDurability = catalyst().isDamageableItem();
        boolean catalystIsUnbreakable = catalyst().has(DataComponents.UNBREAKABLE) || catalyst().getItem() instanceof BundleItem;
        if (playerIsHoldingSpellCatalyst(player)) {
            if (!player.isCreative()) {
                if (catalystHasDurability) {
                    catalyst().hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                } else {
                    if (!catalystIsUnbreakable) {
                        player.getOffhandItem().shrink(1);
                    }
                    if (catalyst().getItem() instanceof BundleItem) {
                        player.getOffhandItem().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
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
    
    public boolean checkPlayerClass(ClassType requiredClass, ResourceLocation playerClass) {
        if (requiredClass.acceptAllClasses()) {
            return true;
        } else
            return requiredClass.classID().equals(playerClass);
    }
    
    public boolean checkPlayerSpecies(SpeciesType requiredSpecies, ResourceLocation playerSpecies) {
        if (requiredSpecies.acceptAllSpecies()) {
            return true;
        } else
            return requiredSpecies.speciesID().equals(playerSpecies);
    }
    
    public void generateClassSpeciesLogger(Player player) {
        ResourceLocation playerClass = PlayerDataCore.ClassData.getPlayerClassType(player);
        ResourceLocation playerSpecies = PlayerDataCore.SpeciesData.getSpecies(player);
        String classTypeText = classType.acceptAllClasses() ? "any class" : classType.classID().toString();
        String speciesTypeText = speciesType.acceptAllSpecies() ? "any species" : speciesType.speciesID().toString();
        IncompCore.LOGGER.info("{} does not meet class or species requirements to cast spell! required: {} | {}, has: {} | {}", player.getName().getString(), classTypeText, speciesTypeText, playerClass, playerSpecies);
    }
}
