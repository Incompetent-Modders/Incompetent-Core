package com.incompetent_modders.incomp_core.common.data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class IncompDatagen {
    
    public static void gatherDataEvent(GatherDataEvent event) {
        IncompCore.LOGGER.info("[Incompetent Core] Data Generation starts.");
        String modId = IncompCore.MODID;
        DataGenerator dataGenerator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        IncompBlockTagsProvider blockTags = new IncompBlockTagsProvider(packOutput, lookupProvider, modId, fileHelper);
        IncompItemTagsProvider itemTags = new IncompItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter());
        dataGenerator.addProvider(event.includeServer(), blockTags);
        dataGenerator.addProvider(event.includeServer(), itemTags);
        dataGenerator.addProvider(event.includeClient(), new IncompLangProvider(packOutput, modId, "en_us"));

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                        .add(ModRegistries.Keys.SPELL, ModSpells::bootstrap)
                        .add(ModRegistries.Keys.DIET, ModDiets::bootstrap)
                        .add(ModRegistries.Keys.SPECIES_TYPE, ModSpeciesTypes::bootstrap)
                        .add(ModRegistries.Keys.CLASS_TYPE, ModClassTypes::bootstrap)
                        .add(ModRegistries.Keys.POTION_PROPERTY, ModPotionProperties::bootstrap)
                        , Set.of(IncompCore.MODID))
        );

    }
    
}
