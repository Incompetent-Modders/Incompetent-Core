package com.incompetent_modders.incomp_core.api.class_type.ability;

import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;


public record AbilityEntry(Ability ability, ResourceLocation identifier, int minLevel, LevelBasedValue cooldown) {
    public static final Codec<AbilityEntry> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(AbilityEntry::ability),
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(AbilityEntry::identifier),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_level", 0).forGetter(AbilityEntry::minLevel),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown", LevelBasedValue.constant(0)).forGetter(AbilityEntry::cooldown)
    ).apply(instance, AbilityEntry::new));

    public AbilityEntry(Ability ability, ResourceLocation identifier) {
        this(ability, identifier, 0, LevelBasedValue.constant(0));
    }

    public AbilityEntry(Ability ability, ResourceLocation identifier, int minLevel) {
        this(ability, identifier, minLevel, LevelBasedValue.constant(0));
    }

    public AbilityEntry(Ability ability, ResourceLocation identifier, LevelBasedValue cooldown) {
        this(ability, identifier, 0, cooldown);
    }

    public ResourceLocation getIconResource() {
        return identifier().withPrefix("textures/icons/abilities/class/").withSuffix(".png");
    }

    public MutableComponent getDisplayName(Player player) {
        return Component.translatable(identifier().toLanguageKey("class_ability"));
    }

    public int getEffectiveCooldown(LivingEntity entity) {
        return Math.round(this.cooldown.calculate(PlayerDataHelper.getClassLevel(entity)));
    }

    public boolean isCorrectLevel(int classLevel) {
        return classLevel >= this.minLevel;
    }
}
