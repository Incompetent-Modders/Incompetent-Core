package com.incompetent_modders.incomp_core.data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.data.spell_properties.SpellPropertiesProvider;
import com.incompetent_modders.incomp_core.registry.dev.DevSpells;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

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
        dataGenerator.addProvider(event.includeServer(), blockTags);
        dataGenerator.addProvider(event.includeServer(), new IncompSpellPropertiesProvider(packOutput, modId));
    }
    
}
