package com.incompetent_modders.incomp_core.api.json.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record Catalyst(ItemStack catalyst) {
    public static final Codec<Catalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("catalyst", ItemStack.EMPTY).forGetter(Catalyst::catalyst)
    ).apply(instance, Catalyst::new));
    
}
