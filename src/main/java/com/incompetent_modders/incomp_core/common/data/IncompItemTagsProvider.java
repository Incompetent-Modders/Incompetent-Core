package com.incompetent_modders.incomp_core.common.data;

import com.incompetent_modders.incomp_core.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

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
                Items.ROTTEN_FLESH,
                Items.SPIDER_EYE,
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

        tag(IncompItemTags.pescetarianFriendly).addOptionalTags(
                Tags.Items.FOODS_COOKED_FISH, Tags.Items.FOODS_RAW_FISH
        );

        tag(IncompItemTags.carnivoreFriendly).addOptionalTags(
                Tags.Items.FOODS_COOKED_MEAT, Tags.Items.FOODS_RAW_MEAT, Tags.Items.FOODS_COOKED_FISH, Tags.Items.FOODS_RAW_FISH
        );

        tag(IncompItemTags.vegetarianFriendly).add(
                Items.HONEY_BOTTLE,
                Items.MILK_BUCKET,
                Items.PUMPKIN_PIE
        );

        tag(IncompItemTags.givesHunger).add(
                Items.ROTTEN_FLESH,
                Items.CHICKEN,
                Items.PUFFERFISH
        );
        tag(IncompItemTags.neutralFood).add(
                ModItems.EFFECT_POSTPONE.get(),
                Items.POTION,
                Items.ENCHANTED_GOLDEN_APPLE,
                Items.GOLDEN_APPLE
        );

        tag(IncompItemTags.canCastSpells).add(
                ModItems.SPELL_TOME.get()
        ).addOptionalTags(
                ItemTags.SWORD_ENCHANTABLE
        );
        tag(IncompItemTags.canStoreSpells).add(
                ModItems.SPELL_TOME.get()
        );
        tag(IncompItemTags.canAssignClassType).add(
                ModItems.ASSIGN_CLASS.get()
        );
        tag(IncompItemTags.canAssignSpeciesType).add(
                ModItems.ASSIGN_SPECIES.get()
        );
    }
}
