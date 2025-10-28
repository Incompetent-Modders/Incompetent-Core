package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.api.species.diet.Diet;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ListFeaturesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list_features").requires(s -> s.hasPermission(2))
           .then(Commands.literal("spells").executes(arguments -> listSpells(arguments.getSource(), false))
                   .then(Commands.literal("show_id").executes(arguments -> listSpells(arguments.getSource(), true))))
                .then(Commands.literal("class_types").executes(arguments -> listClassTypes(arguments.getSource(), false))
                        .then(Commands.literal("show_id").executes(arguments -> listClassTypes(arguments.getSource(), true))))
                .then(Commands.literal("species").executes(arguments -> listSpecies(arguments.getSource(), false))
                        .then(Commands.literal("show_id").executes(arguments -> listSpecies(arguments.getSource(), true))))
                .then(Commands.literal("diets").executes(arguments -> listDiets(arguments.getSource(), false))
                        .then(Commands.literal("show_id").executes(arguments -> listDiets(arguments.getSource(), true))))
                .then(Commands.literal("all").executes(arguments -> list(arguments.getSource(), false))
                        .then(Commands.literal("show_id").executes(arguments -> list(arguments.getSource(), true))));
    }
    
    private static int listSpells(CommandSourceStack source, boolean showID) {
        Registry<Spell> registry = source.registryAccess().registryOrThrow(ModRegistries.Keys.SPELL);
        Component result = Component.translatable("commands.incompetent_core.list_features.spells", registry.size()).withStyle(ClientUtil.styleFromColor(0x624a95));
        source.sendSuccess(() -> result, false);
        for (Spell spell : registry) {
            Holder<Spell> spellHolder = registry.wrapAsHolder(spell);
            ResourceLocation spellID = spellHolder.getKey().location();
            Component spellMessage = spell.description().copy().withStyle(ClientUtil.styleFromColor(0xa05baf));
            Component spellIDMessage = Component.literal("> (" + spellID + ")").withStyle(ClientUtil.styleFromColor(0x624a95));
            if (showID) {
                source.sendSuccess(() -> spellMessage, false);
                source.sendSuccess(() -> spellIDMessage, false);
            } else {
                source.sendSuccess(() -> spellMessage, false);
            }
        }
        return 0;
    }
    
    private static int listClassTypes(CommandSourceStack source, boolean showID) {
        Registry<ClassType> registry = source.registryAccess().registryOrThrow(ModRegistries.Keys.CLASS_TYPE);
        Component result = Component.translatable("commands.incompetent_core.list_features.class_types", registry.size()).withStyle(ClientUtil.styleFromColor(0xe19635));
        source.sendSuccess(() -> result, false);
        for (ClassType classType : registry) {
            Holder<ClassType> classHolder = registry.wrapAsHolder(classType);
            ResourceLocation classTypeID = classHolder.getKey().location();
            Component classTypeMessage = ClassType.getDisplayName(classTypeID).copy().withStyle(ClientUtil.styleFromColor(0xe5ab49));
            Component classTypeIDMessage = Component.literal("> (" + classTypeID + ")").withStyle(ClientUtil.styleFromColor(0xe19635));
            if (showID) {
                source.sendSuccess(() -> classTypeMessage, false);
                source.sendSuccess(() -> classTypeIDMessage, false);
            } else {
                source.sendSuccess(() -> classTypeMessage, false);
            }
        }
        return 0;
    }
    
    private static int listSpecies(CommandSourceStack source, boolean showID) {
        Registry<SpeciesType> registry = source.registryAccess().registryOrThrow(ModRegistries.Keys.SPECIES_TYPE);
        Component result = Component.translatable("commands.incompetent_core.list_features.species", registry.size()).withStyle(ClientUtil.styleFromColor(0x4998e5));
        source.sendSuccess(() -> result, false);
        for (SpeciesType speciesType : registry) {
            Holder<SpeciesType> speciesHolder = registry.wrapAsHolder(speciesType);
            ResourceLocation speciesTypeID = speciesHolder.getKey().location();
            Component speciesTypeMessage = SpeciesType.getDisplayName(speciesTypeID).copy().withStyle(ClientUtil.styleFromColor(0x6eb0ea));
            Component speciesTypeIDMessage = Component.literal("> (" + speciesTypeID + ")").withStyle(ClientUtil.styleFromColor(0x4998e5));
            if (showID) {
                source.sendSuccess(() -> speciesTypeMessage, false);
                source.sendSuccess(() -> speciesTypeIDMessage, false);
            } else {
                source.sendSuccess(() -> speciesTypeMessage, false);
            }
        }
        return 0;
    }
    
    private static int listDiets(CommandSourceStack source, boolean showID) {
        Registry<Diet> registry = source.registryAccess().registryOrThrow(ModRegistries.Keys.DIET);
        Component result = Component.translatable("commands.incompetent_core.list_features.diets", registry.size()).withStyle(ClientUtil.styleFromColor(0x478e47));
        source.sendSuccess(() -> result, false);
        for (Diet diet : registry) {
            Holder<Diet> dietHolder = registry.wrapAsHolder(diet);
            ResourceLocation dietID = dietHolder.getKey().location();
            Component dietMessage = Diet.getDisplayName(dietID).copy().withStyle(ClientUtil.styleFromColor(0x56ad56));
            Component dietIDMessage = Component.literal("> (" + dietID + ")").withStyle(ClientUtil.styleFromColor(0x478e47));
            if (showID) {
                source.sendSuccess(() -> dietMessage, false);
                source.sendSuccess(() -> dietIDMessage, false);
            } else {
                source.sendSuccess(() -> dietMessage, false);
            }
        }
        return 0;
    }
    
    private static int list(CommandSourceStack source, boolean showID) {
        listSpells(source, showID);
        listClassTypes(source, showID);
        listSpecies(source, showID);
        listDiets(source, showID);
        return 0;
    }
    
}
