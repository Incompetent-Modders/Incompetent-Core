package com.incompetent_modders.incomp_core.core.def;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.core.def.params.*;
import com.incompetent_modders.incomp_core.core.def.params.SpeciesTypeCondition;
import com.incompetent_modders.incomp_core.core.def.params.ClassTypeCondition;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public record Spell(Component description, SpellDefinition definition) {

    public static final Codec<Spell> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Spell::description),
            SpellDefinition.CODEC.fieldOf("definition").forGetter(Spell::definition)
    ).apply(instance, Spell::new));

    public static final Codec<Holder<Spell>> CODEC = RegistryFixedCodec.create(ModRegistries.Keys.SPELL);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Spell>> STREAM_CODEC;

    public static final Codec<Spell> NETWORK_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Spell::description),
            SpellDefinition.CODEC.fieldOf("definition").forGetter(Spell::definition)
    ).apply(instance, Spell::new));

    public Spell(Component description, SpellDefinition definition) {
        this.description = description;
        this.definition = definition;
    }


    static {
        STREAM_CODEC = ByteBufCodecs.holderRegistry(ModRegistries.Keys.SPELL);
    }

    public boolean isBlankSpell() {
        return definition().manaCost == 0.0 && definition().drawTime == 0 && definition().conditions().catalyst.item().isEmpty() && Objects.equals(definition().conditions().classType, ClassTypeCondition.ANY) && Objects.equals(definition().conditions().speciesType, SpeciesTypeCondition.ANY) && definition().results.spellResult().isEmpty() && definition().results.function().isEmpty();
    }

    public record SpellDefinition(SpellCategory category, double manaCost, int drawTime, SpellResults results, SoundEvent castSound, SpellConditions conditions) {
        public static final MapCodec<SpellDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SpellCategory.CODEC.optionalFieldOf("category", SpellCategory.UTILITY).forGetter(SpellDefinition::category),
                Codec.DOUBLE.optionalFieldOf("mana_cost", 0.0).forGetter(SpellDefinition::manaCost),
                Codec.INT.optionalFieldOf("draw_time", 0).forGetter(SpellDefinition::drawTime),
                SpellResults.CODEC.optionalFieldOf("results", SpellResults.EMPTY).forGetter(SpellDefinition::results),
                SoundEvent.DIRECT_CODEC.optionalFieldOf("cast_sound", SoundEvents.ALLAY_THROW).forGetter(SpellDefinition::castSound),
                SpellConditions.CODEC.optionalFieldOf("conditions", SpellConditions.EMPTY).forGetter(SpellDefinition::conditions)
                ).apply(instance, SpellDefinition::new));

        public SpellDefinition(SpellCategory category, double manaCost, int drawTime, SpellResults results, SoundEvent castSound, SpellConditions conditions) {
            this.category = category;
            this.manaCost = manaCost;
            this.drawTime = drawTime;
            this.conditions = conditions;
            this.results = results;
            this.castSound = castSound;
        }

        public static Builder builder(double manaCost, int drawTime) {
            return new Builder(manaCost, drawTime);
        }

        public static class Builder {
            private SpellCategory category = SpellCategory.UTILITY;
            private double manaCost = 0.0;
            private int drawTime = 0;
            private SpellResults results = SpellResults.EMPTY;
            private SoundEvent castSound = SoundEvents.ALLAY_THROW;
            private SpellConditions conditions = SpellConditions.EMPTY;

            public Builder (double manaCost, int drawTime) {
                this.manaCost = manaCost;
                this.drawTime = drawTime;
            }

            public Builder category(SpellCategory category) {
                this.category = category;
                return this;
            }

            public Builder conditions(SpellConditions conditions) {
                this.conditions = conditions;
                return this;
            }

            public Builder conditions(SpellConditions.Builder conditionsBuilder) {
                this.conditions = conditionsBuilder.build();
                return this;
            }

            public Builder results(SpellResults results) {
                this.results = results;
                return this;
            }

            public Builder castSound(SoundEvent castSound) {
                this.castSound = castSound;
                return this;
            }

            public SpellDefinition build() {
                return new SpellDefinition(category, manaCost, drawTime, results, castSound, conditions);
            }
        }
    }

    public record SpellConditions(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
        public static final Codec<SpellConditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CatalystCondition.CODEC.optionalFieldOf("catalyst", CatalystCondition.EMPTY).forGetter(SpellConditions::catalyst),
                ClassTypeCondition.CODEC.optionalFieldOf("class", ClassTypeCondition.ANY).forGetter(SpellConditions::classType),
                SpeciesTypeCondition.CODEC.optionalFieldOf("species", SpeciesTypeCondition.ANY).forGetter(SpellConditions::speciesType)
        ).apply(instance, SpellConditions::new));

        public static final SpellConditions EMPTY = new SpellConditions(CatalystCondition.EMPTY, ClassTypeCondition.ANY, SpeciesTypeCondition.ANY);

        public boolean matches(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
            return this.catalyst == catalyst && this.classType == classType && this.speciesType == speciesType;
        }

        public boolean matches(SpellConditions conditions) {
            return this.catalyst == conditions.catalyst && this.classType == conditions.classType && this.speciesType == conditions.speciesType;
        }

        public SpellConditions(CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType) {
            this.catalyst = catalyst;
            this.classType = classType;
            this.speciesType = speciesType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private CatalystCondition catalyst = CatalystCondition.EMPTY;
            private ClassTypeCondition classType = ClassTypeCondition.ANY;
            private SpeciesTypeCondition speciesType = SpeciesTypeCondition.ANY;

            public Builder catalyst(CatalystCondition catalyst) {
                this.catalyst = catalyst;
                return this;
            }

            public Builder classType(ClassTypeCondition classType) {
                this.classType = classType;
                return this;
            }

            public Builder speciesType(SpeciesTypeCondition speciesType) {
                this.speciesType = speciesType;
                return this;
            }

            public SpellConditions build() {
                return new SpellConditions(catalyst, classType, speciesType);
            }
        }
    }

    public void executeCast(Player player) {
        Level level = player.level();
        ResourceKey<ClassType> playerClass = PlayerDataHelper.getClassTypeWithKey(player).getFirst();
        ResourceKey<SpeciesType> playerSpecies = PlayerDataHelper.getSpeciesTypeWithKey(player).getFirst();
        if (!checkPlayerClass(definition.conditions.classType(), playerClass) ||  !checkPlayerSpecies(definition.conditions.speciesType(), playerSpecies)) {
            generateClassSpeciesLogger(player);
            return;
        }
        double playerMana = PlayerDataHelper.getMana(player);
        if (playerMana < getManaCost(player)) {
            IncompCore.LOGGER.info("{} doesn't have enough mana to cast spell!", player.getName().getString());
            return;
        }
        if (!playerIsHoldingSpellCatalyst(player)) {
            IncompCore.LOGGER.info("{} is not holding the required catalyst to cast spell!", player.getName().getString());
            return;
        }
        PlayerDataHelper.removeMana(player, getManaCost(player));
        handleCatalystConsumption(player);
        executeCast(level, player);
    }

    public void executeCast(Level level, Player player) {
        definition().results().execute(player);
        if (level.isClientSide()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(), definition().castSound(), player.getSoundSource(), 1.0F, 1.0F);
        }
        Utils.onCastEvent(level, player, player.getUsedItemHand());
        player.awardStat(Stats.ITEM_USED.get(player.getItemInHand(player.getUsedItemHand()).getItem()));
        IncompCore.LOGGER.info("Spell cast by {}", player.getName().getString());
    }

    public boolean hasSpellCatalyst() {
        return !this.definition().conditions().catalyst().item().isEmpty();
    }
    public boolean playerIsHoldingSpellCatalyst(Player player) {
        if (hasSpellCatalyst() && player != null) {
            return ItemStack.isSameItemSameComponents(player.getOffhandItem(), definition().conditions().catalyst().item());
        }
        return true;
    }

    public void handleCatalystConsumption(Player player) {
        if (this.definition().conditions().catalyst().item().isEmpty()) {
            return;
        }
        boolean catalystHasDurability = this.definition().conditions().catalyst().item().isDamageableItem();
        boolean catalystIsUnbreakable = this.definition().conditions().catalyst().item().has(DataComponents.UNBREAKABLE) || this.definition().conditions().catalyst().item().getItem() instanceof BundleItem || this.definition().conditions().catalyst().keepCatalyst();
        boolean catalystDropsLeftoverItems = this.definition().conditions().catalyst().dropLeftoverItems();
        if (playerIsHoldingSpellCatalyst(player)) {
            if (!player.isCreative()) {
                if (catalystHasDurability) {
                    this.definition().conditions().catalyst().item().hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
                } else {
                    if (catalystIsUnbreakable) {
                        if (this.definition().conditions().catalyst().item().getItem() instanceof BundleItem) {
                            this.definition().conditions().catalyst().item().set(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
                        }
                    } else {
                        player.getOffhandItem().shrink(1);
                        if (catalystDropsLeftoverItems) {
                            player.drop(player.getOffhandItem(), true, true);
                        }
                    }
                }
                player.awardStat(Stats.ITEM_USED.get(this.definition().conditions().catalyst().item().getItem()));
            } else
                player.awardStat(Stats.ITEM_USED.get(this.definition().conditions().catalyst().item().getItem()));
        } else {
            Component message = Component.translatable("item.incompetent_core.spellcasting.catalyst_required", this.definition().conditions().catalyst().item().getDisplayName());
            player.displayClientMessage(message, false);
        }
    }

    public double getManaCost(Player player) {
        float mod = 1.0F;
        if (player == null) return definition().manaCost();
        if (!player.getActiveEffects().isEmpty()) {
            for (MobEffectInstance effectInstance : player.getActiveEffects()) {
                RegistryAccess access = player.level().registryAccess();
                for (Holder.Reference<PotionProperty> holder : access.registryOrThrow(ModRegistries.Keys.POTION_PROPERTY).holders().toList()) {
                    if (holder.value().effect() == effectInstance.getEffect()) {
                        float manaCostModifier = holder.value().manaCostModifier();
                        if (manaCostModifier != 1.0F) {
                            mod = manaCostModifier;
                        }
                    }
                }
            }
        }
        return definition().manaCost() * mod;
    }

    public boolean checkPlayerClass(ClassTypeCondition requiredClass, ResourceKey<ClassType> playerClass) {
        if (requiredClass.acceptAllClasses()) {
            return true;
        } else
            return requiredClass.classKey().equals(playerClass);
    }

    public boolean checkPlayerSpecies(SpeciesTypeCondition requiredSpecies, ResourceKey<SpeciesType> playerSpecies) {
        if (requiredSpecies.acceptAllSpecies()) {
            return true;
        } else
            return requiredSpecies.speciesKey().equals(playerSpecies);
    }

    public void generateClassSpeciesLogger(Player player) {
        ResourceKey<ClassType> playerClass = PlayerDataHelper.getClassTypeWithKey(player).getFirst();
        ResourceKey<SpeciesType> playerSpecies = PlayerDataHelper.getSpeciesTypeWithKey(player).getFirst();
        String classTypeText = definition.conditions.classType().acceptAllClasses() ? "any class" : definition.conditions.classType().classKey().toString();
        String speciesTypeText = definition.conditions.speciesType().acceptAllSpecies() ? "any species" : definition.conditions.speciesType().speciesKey().toString();
        IncompCore.LOGGER.info("{} does not meet class or species requirements to cast spell! required: {} | {}, has: {} | {}", player.getName().getString(), classTypeText, speciesTypeText, playerClass, playerSpecies);
    }

    public static Component getDisplayName(ResourceKey<Spell> spell) {
        return Component.translatable("spell." + spell.location().getNamespace() + "." + spell.location().getPath().replace("/", "."));
    }
}
