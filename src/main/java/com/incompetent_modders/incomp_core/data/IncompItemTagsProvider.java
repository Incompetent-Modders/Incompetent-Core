package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.registry.ModItems;
import com.incompetent_modders.incomp_core.registry.dev.DevItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class IncompItemTagsProvider extends ItemTagsProvider {
    
    
    public IncompItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, CompletableFuture<TagLookup<Block>> completableFutureBlock) {
        super(output, completableFuture, completableFutureBlock);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }
    
    @SuppressWarnings("unchecked")
    protected void registerModTags() {
        tag(IncompItemTags.carnivoreFriendly).add(
                Items.BEEF,
                Items.CHICKEN,
                Items.MUTTON,
                Items.PORKCHOP,
                Items.RABBIT,
                Items.COOKED_BEEF,
                Items.COOKED_CHICKEN,
                Items.COOKED_MUTTON,
                Items.COOKED_PORKCHOP,
                Items.COOKED_RABBIT,
                Items.COOKED_COD,
                Items.COOKED_SALMON,
                Items.COD,
                Items.SALMON,
                Items.PUFFERFISH,
                Items.ROTTEN_FLESH,
                Items.SPIDER_EYE,
                Items.TROPICAL_FISH,
                Items.RABBIT_STEW
        );
        tag(IncompItemTags.veganFriendly).add(
                Items.APPLE,
                Items.GOLDEN_APPLE,
                Items.ENCHANTED_GOLDEN_APPLE,
                Items.BEETROOT,
                Items.BEETROOT_SOUP,
                Items.CARROT,
                Items.CARROT_ON_A_STICK,
                Items.CHORUS_FRUIT,
                Items.DRIED_KELP,
                Items.GOLDEN_CARROT,
                Items.GOLDEN_APPLE,
                Items.MELON_SLICE,
                Items.POTATO,
                Items.MUSHROOM_STEW,
                Items.SWEET_BERRIES,
                Items.WHEAT,
                Items.WHEAT_SEEDS,
                Items.BREAD,
                Items.GLOW_BERRIES,
                Items.SUSPICIOUS_STEW,
                Items.COOKIE
        );
        
        tag(IncompItemTags.veganFriendly).addTags(
                IncompItemTags.effectPostpones
        );
        tag(IncompItemTags.vegetarianFriendly).addTag(
                IncompItemTags.veganFriendly
        );
        tag(IncompItemTags.vegetarianFriendly).add(
                Items.HONEY_BOTTLE,
                Items.MILK_BUCKET,
                Items.PUMPKIN_PIE
        );
        tag(IncompItemTags.omnivoreFriendly).addTags(
                IncompItemTags.carnivoreFriendly,
                IncompItemTags.vegetarianFriendly,
                IncompItemTags.effectPostpones
        );
        tag(IncompItemTags.carnivoreFriendly).addTags(
                IncompItemTags.effectPostpones
        );
        tag(IncompItemTags.givesHunger).add(
                Items.ROTTEN_FLESH,
                Items.CHICKEN,
                Items.PUFFERFISH
        );
        tag(IncompItemTags.effectPostpones).add(
                ModItems.EFFECT_POSTPONE.get()
        );
    }
}
