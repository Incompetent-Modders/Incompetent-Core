package com.incompetent_modders.incomp_core.api.json.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record SpellProperties(int manaCost, int drawTime, int cooldown, ItemStack catalyst) {
    public static final Codec<SpellProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("mana_cost").forGetter(SpellProperties::manaCost),
            Codec.INT.fieldOf("draw_time").forGetter(SpellProperties::drawTime),
            Codec.INT.fieldOf("cooldown").forGetter(SpellProperties::cooldown),
            ItemStack.ITEM_WITH_COUNT_CODEC.optionalFieldOf("catalyst", ItemStack.EMPTY).forGetter(SpellProperties::catalyst)
    ).apply(instance, SpellProperties::new));
    
}
