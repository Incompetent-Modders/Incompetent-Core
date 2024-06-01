package com.incompetent_modders.incomp_core.common.registry.content.spell_results;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.incompetent_modders.incomp_core.common.registry.ModSpellResultTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static com.incompetent_modders.incomp_core.api.spell.SpellUtils.isInventoryFull;

public class GiveItemsResult extends SpellResult {
    public static final MapCodec<GiveItemsResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(GiveItemsResult::getItem)
    ).apply(instance, GiveItemsResult::new));
    
    private final ItemStack items;
    
    public GiveItemsResult(ItemStack items) {
        this.items = items;
    
    }
    
    public ItemStack getItem() {
        return items;
    }
    
    
    @Override
    public void execute(Player player) {
        if (isInventoryFull(player)) {
            for (int i = 0; i < getItem().getCount(); i++) {
                ItemStack singleItem = getItem().copyWithCount(1);
                player.drop(singleItem, false);
            }
        }
        player.getInventory().add(getItem());
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return ModSpellResultTypes.GIVE_ITEMS.get();
    }
}
