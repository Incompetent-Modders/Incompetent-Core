package com.incompetent_modders.incomp_core.common.data;

import com.incompetent_modders.incomp_core.IncompCore;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class IncompBlockTagsProvider extends BlockTagsProvider {
    public IncompBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.registerModTags();
    }
    
    @SuppressWarnings("unchecked")
    protected void registerModTags() {
        tag(modBlockTag("spell_transparent")).addTags(
                BlockTags.SMALL_FLOWERS,
                BlockTags.CORAL_PLANTS,
                BlockTags.CLIMBABLE,
                BlockTags.FIRE
        );
        tag(modBlockTag("spell_transparent")).add(
                Blocks.AIR,
                Blocks.CAVE_AIR,
                Blocks.VOID_AIR
        );
    }
    
    
    
    
    public static TagKey<Block> forgeBlockTag(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", path));
    }
    public static TagKey<Block> modBlockTag(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, path));
    }
    public static TagKey<Block> modBlockTag(String namespace, String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
