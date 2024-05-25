package com.incompetent_modders.incomp_core.api.json.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record Catalyst(ItemStack item, boolean keepCatalyst, boolean dropLeftoverItems) {
    public static final Codec<Catalyst> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(Catalyst::item),
            Codec.BOOL.optionalFieldOf("keep_catalyst", false).forGetter(Catalyst::keepCatalyst),
            Codec.BOOL.optionalFieldOf("drop_leftover_items", false).forGetter(Catalyst::dropLeftoverItems)
    ).apply(instance, Catalyst::new));
    
    public static Catalyst EMPTY = new Catalyst(ItemStack.EMPTY, false, false);
}
