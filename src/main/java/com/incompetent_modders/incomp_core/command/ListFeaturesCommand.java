package com.incompetent_modders.incomp_core.command;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ListFeaturesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list_features").requires(s -> s.hasPermission(2)).then(Commands.literal("spells").executes(arguments -> {
            return listSpells(arguments.getSource());
        })).then(Commands.literal("class_types").executes(arguments -> {
            return listClassTypes(arguments.getSource());
        })).then(Commands.literal("species").executes(arguments -> {
            return listSpecies(arguments.getSource());
        })).then(Commands.literal("diets").executes(arguments -> {
            return listDiets(arguments.getSource());
        }));
    }
    
    private static int listSpells(CommandSourceStack source) {
        Component result = Component.translatable("commands.incomp_core.list_features.spells", SpellListener.getAllSpells().size());
        source.sendSuccess(() -> result, true);
        for (ResourceLocation spellID : SpellListener.getAllSpells()) {
            Component message = Component.literal(spellID.toString()).withStyle(IncompCore.DESCRIPTION_FORMAT);
            source.sendSuccess(() -> message, true);
        }
        return 0;
    }
    
    private static int listClassTypes(CommandSourceStack source) {
        Component result = Component.translatable("commands.incomp_core.list_features.class_types", ClassTypeListener.getAllClassTypes().size());
        source.sendSuccess(() -> result, true);
        for (ResourceLocation classTypeID : ClassTypeListener.classTypes) {
            Component message = Component.literal(classTypeID.toString()).withStyle(IncompCore.DESCRIPTION_FORMAT);
            source.sendSuccess(() -> message, true);
        }
        return 0;
    }
    
    private static int listSpecies(CommandSourceStack source) {
        Component result = Component.translatable("commands.incomp_core.list_features.species", SpeciesListener.getAllSpecies().size());
        source.sendSuccess(() -> result, true);
        for (ResourceLocation speciesID : SpeciesListener.getAllSpecies()) {
            Component message = Component.literal(speciesID.toString()).withStyle(IncompCore.DESCRIPTION_FORMAT);
            source.sendSuccess(() -> message, true);
        }
        return 0;
    }
    
    private static int listDiets(CommandSourceStack source) {
        Component result = Component.translatable("commands.incomp_core.list_features.diets", DietListener.getAllDiets().size());
        source.sendSuccess(() -> result, true);
        for (ResourceLocation dietID : DietListener.getAllDiets()) {
            Component message = Component.literal(dietID.toString()).withStyle(IncompCore.DESCRIPTION_FORMAT);
            source.sendSuccess(() -> message, true);
        }
        return 0;
    }
    
}
