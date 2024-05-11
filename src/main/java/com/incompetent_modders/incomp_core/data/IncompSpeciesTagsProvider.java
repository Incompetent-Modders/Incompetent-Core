package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.data.provider.SpeciesTagsProvider;
import com.incompetent_modders.incomp_core.registry.ModSpeciesTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.incompetent_modders.incomp_core.data.IncompSpeciesTags.*;

public class IncompSpeciesTagsProvider extends SpeciesTagsProvider {
    public IncompSpeciesTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.registerModTags();
    }
    protected void registerModTags() {
        tag(smiteVulnerable).addTag(undead);
        tag(undead).add(
                ModSpeciesTypes.ZOMBIE.get()
        );
        tag(ignoresHungerFromFood).add(
                ModSpeciesTypes.ZOMBIE.get()
        );
    }
    
    public static TagKey<SpeciesType> modSpeciesTag(String path) {
        return create(new ResourceLocation(IncompCore.MODID, path));
    }
    public static TagKey<SpeciesType> modSpeciesTag(String namespace, String path) {
        return create(new ResourceLocation(namespace, path));
    }
    public static TagKey<SpeciesType> forgeSpeciesTag(String path) {
        return create(new ResourceLocation("forge", path));
    }
    
    public static TagKey<SpeciesType> create(ResourceLocation name) {
        return TagKey.create(ModRegistries.SPECIES_TYPE_KEY, name);
    }
}
