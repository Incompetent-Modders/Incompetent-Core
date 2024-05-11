package com.incompetent_modders.incomp_core.data.provider;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class SpeciesTagsProvider extends IntrinsicHolderTagsProvider<SpeciesType> {
    public SpeciesTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ModRegistries.SPECIES_TYPE.key(), lookupProvider, speciesType -> speciesType.builtInRegistryHolder().key(), modId, existingFileHelper);
    }
}
