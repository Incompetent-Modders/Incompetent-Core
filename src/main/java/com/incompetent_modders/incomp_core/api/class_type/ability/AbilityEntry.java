package com.incompetent_modders.incomp_core.api.class_type.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;


public record AbilityEntry(Ability ability, ResourceLocation identifier, int minLevel, int cooldown, float cooldownLevelScale) {
    public static final Codec<AbilityEntry> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ability.DIRECT_CODEC.fieldOf("ability").forGetter(AbilityEntry::ability),
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(AbilityEntry::identifier),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_level", 0).forGetter(AbilityEntry::minLevel),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("cooldown", 0).forGetter(AbilityEntry::cooldown),
            Codec.FLOAT.optionalFieldOf("cooldown_level_scale", 1.0f).forGetter(AbilityEntry::cooldownLevelScale)
    ).apply(instance, AbilityEntry::new));

    public AbilityEntry(Ability ability, ResourceLocation identifier) {
        this(ability, identifier, 0, 0, 1.0f);
    }

    public AbilityEntry(Ability ability, ResourceLocation identifier, int minLevel) {
        this(ability, identifier, minLevel, 0, 1.0f);
    }

    public AbilityEntry(Ability ability, ResourceLocation identifier, int cooldown, float cooldownLevelScale) {
        this(ability, identifier, 0, cooldown, cooldownLevelScale);
    }

    public ResourceLocation getIconResource() {
        return identifier().withPrefix("textures/icons/abilities/class/").withSuffix(".png");
    }

    public MutableComponent getDisplayName(Player player) {
        return Component.translatable(identifier().toLanguageKey("class_ability"));
    }

    public int getEffectiveCooldown(int classLevel) {
        int effectiveCooldown = this.cooldown;
        int lvl = classLevel - this.minLevel;
        while (lvl > 0) {
            effectiveCooldown = Math.round(effectiveCooldown * cooldownLevelScale);
            lvl -= 1;
        }
        return effectiveCooldown;
    }

    public boolean isCorrectLevel(int classLevel) {
        return classLevel >= this.minLevel;
    }
}
