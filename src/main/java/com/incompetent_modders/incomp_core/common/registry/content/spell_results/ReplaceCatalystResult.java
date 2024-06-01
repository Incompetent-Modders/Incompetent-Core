package com.incompetent_modders.incomp_core.common.registry.content.spell_results;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.common.registry.ModSpellResultTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReplaceCatalystResult extends SpellResult {
    public static final MapCodec<ReplaceCatalystResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(ReplaceCatalystResult::getItem),
            Codec.BOOL.fieldOf("drop_leftover_items").forGetter(ReplaceCatalystResult::shouldDropLeftoverItems)
    ).apply(instance, ReplaceCatalystResult::new));
    
    private final ItemStack item;
    private final boolean dropLeftoverItems;
    
    public ReplaceCatalystResult(ItemStack item, boolean dropLeftoverItems) {
        this.item = item;
        this.dropLeftoverItems = dropLeftoverItems;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public boolean shouldDropLeftoverItems() {
        return dropLeftoverItems;
    }
    
    @Override
    public void execute(Player player) {
        ItemStack catalyst = player.getOffhandItem();
        player.setItemInHand(InteractionHand.OFF_HAND, getItem());
        if (shouldDropLeftoverItems()) {
            player.drop(catalyst, false);
        }
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return ModSpellResultTypes.REPLACE_CATALYST.get();
    }
}
