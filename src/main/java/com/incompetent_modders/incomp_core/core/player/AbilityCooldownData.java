package com.incompetent_modders.incomp_core.core.player;

import com.incompetent_modders.incomp_core.api.class_type.ability.Ability;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.Map;

public record AbilityCooldownData(Map<ResourceLocation, Integer> cooldowns) {
    public static final AbilityCooldownData EMPTY_DATA = new AbilityCooldownData(Map.of());

    public static final Codec<Map<ResourceLocation, Integer>> COOLDOWN_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, ExtraCodecs.NON_NEGATIVE_INT);
    public static final MapCodec<AbilityCooldownData> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            COOLDOWN_CODEC.fieldOf("ability_cooldowns").forGetter(AbilityCooldownData::cooldowns)
    ).apply(instance, AbilityCooldownData::new));

    public static final Codec<AbilityCooldownData> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<FriendlyByteBuf, AbilityCooldownData> STREAM_CODEC = StreamCodec.ofMember(AbilityCooldownData::write, AbilityCooldownData::read);

    public boolean hasAbilityCooldown(ResourceLocation ability) {
        return cooldowns.containsKey(ability);
    }

    public int getAbilityCooldown(ResourceLocation ability) {
        return hasAbilityCooldown(ability) ? cooldowns.get(ability) : 0;
    }

    public AbilityCooldownData addCooldown(ResourceLocation ability, int cooldown) {
        var cooldowns = this.cooldowns;
        cooldowns.put(ability, cooldown);
        return new AbilityCooldownData(cooldowns);
    }

    public AbilityCooldownData removeCooldown(ResourceLocation ability) {
        var cooldowns = this.cooldowns;
        cooldowns.remove(ability);
        return new AbilityCooldownData(cooldowns);
    }

    public AbilityCooldownData decreaseCooldown(ResourceLocation ability, int cooldown) {
        var cooldowns = this.cooldowns;
        if (cooldown -1 == 0) {
            cooldowns.remove(ability);
            return new AbilityCooldownData(cooldowns);
        }
        cooldowns.put(ability, cooldown - 1);
        return new AbilityCooldownData(cooldowns);
    }

    public AbilityCooldownData decrementAllCooldowns(int amount) {
        var cooldowns = this.cooldowns;
        for (Map.Entry<ResourceLocation, Integer> entry : cooldowns.entrySet()) {
            int cooldown = entry.getValue();
            ResourceLocation ability = entry.getKey();
            int newCooldown = cooldown - amount;
            if (newCooldown == 0) {
                cooldowns.remove(ability);
            } else {
                cooldowns.put(ability, newCooldown);
            }
        }
        return new AbilityCooldownData(cooldowns);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeMap(cooldowns, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT);
    }

    private static AbilityCooldownData read(FriendlyByteBuf buffer) {
        return new AbilityCooldownData(
                buffer.readMap(ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT)
        );
    }
}
