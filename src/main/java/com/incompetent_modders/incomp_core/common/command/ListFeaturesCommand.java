package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ListFeaturesCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list_features").requires(s -> s.hasPermission(2)).then(Commands.literal("spells").executes(arguments -> {
            return listSpells(arguments.getSource(), false);
        })).then(Commands.literal("class_types").executes(arguments -> {
            return listClassTypes(arguments.getSource(), false);
        }).then(Commands.literal("show_id").executes(arguments -> {
            return listClassTypes(arguments.getSource(), true);
        })).then(Commands.literal("species").executes(arguments -> {
            return listSpecies(arguments.getSource(), false);
        }).then(Commands.literal("show_id").executes(arguments -> {
            return listSpecies(arguments.getSource(), true);
        })).then(Commands.literal("diets").executes(arguments -> {
            return listDiets(arguments.getSource(), false);
        }).then(Commands.literal("show_id").executes(arguments -> {
            return listDiets(arguments.getSource(), true);
        }).then(Commands.literal("all").executes(arguments -> {
            return listAll(arguments.getSource(), false);
        })).then(Commands.literal("show_id").executes(arguments -> {
            return listAll(arguments.getSource(), true);
        }))))));
    }
    
    private static int listSpells(CommandSourceStack source, boolean showID) {
        Component result = Component.translatable("commands.incompetent_core.list_features.spells", SpellListener.getAllSpells().size()).withStyle(ClientUtil.styleFromColor(0x624a95));
        source.sendSuccess(() -> result, true);
        for (ResourceLocation spellID : SpellListener.getAllSpells()) {
            Component spellIDMessage = Component.literal("> (" + spellID.toString() + ")").withStyle(ClientUtil.styleFromColor(0x624a95));
            Component spellNameMessage = SpellListener.getDisplayName(spellID).copy().withStyle(ClientUtil.styleFromColor(0xa05baf));
            if (showID) {
                source.sendSuccess(() -> spellNameMessage, true);
                source.sendSuccess(() -> spellIDMessage, true);
            } else {
                source.sendSuccess(() -> spellNameMessage, true);
            }
        }
        return 0;
    }
    
    private static int listClassTypes(CommandSourceStack source, boolean showID) {
        Component result = Component.translatable("commands.incompetent_core.list_features.class_types", ClassTypeListener.getAllClassTypes().size()).withStyle(ClientUtil.styleFromColor(0xe19635));
        source.sendSuccess(() -> result, true);
        for (ResourceLocation classTypeID : ClassTypeListener.classTypes) {
            Component classTypeMessage = ClassTypeListener.getDisplayName(classTypeID).copy().withStyle(ClientUtil.styleFromColor(0xe5ab49));
            Component classTypeIDMessage = Component.literal("> (" + classTypeID + ")").withStyle(ClientUtil.styleFromColor(0xe19635));
            if (showID) {
                source.sendSuccess(() -> classTypeMessage, true);
                source.sendSuccess(() -> classTypeIDMessage, true);
            } else {
                source.sendSuccess(() -> classTypeMessage, true);
            }
        }
        return 0;
    }
    
    private static int listSpecies(CommandSourceStack source, boolean showID) {
        Component result = Component.translatable("commands.incompetent_core.list_features.species", SpeciesListener.getAllSpecies().size()).withStyle(ClientUtil.styleFromColor(0x4998e5));
        source.sendSuccess(() -> result, true);
        for (ResourceLocation speciesID : SpeciesListener.getAllSpecies()) {
            Component speciesMessage = SpeciesListener.getDisplayName(speciesID).copy().withStyle(ClientUtil.styleFromColor(0x6eb0ea));
            Component speciesIDMessage = Component.literal("> (" + speciesID + ")").withStyle(ClientUtil.styleFromColor(0x4998e5));
            if (showID) {
                source.sendSuccess(() -> speciesMessage, true);
                source.sendSuccess(() -> speciesIDMessage, true);
            } else {
                source.sendSuccess(() -> speciesMessage, true);
            }
        }
        return 0;
    }
    
    private static int listDiets(CommandSourceStack source, boolean showID) {
        Component result = Component.translatable("commands.incompetent_core.list_features.diets", DietListener.getAllDiets().size()).withStyle(ClientUtil.styleFromColor(0x478e47));
        source.sendSuccess(() -> result, true);
        for (ResourceLocation dietID : DietListener.getAllDiets()) {
            Component dietMessage = DietListener.getDisplayName(dietID).copy().withStyle(ClientUtil.styleFromColor(0x56ad56));
            Component dietIDMessage = Component.literal("> (" + dietID + ")").withStyle(ClientUtil.styleFromColor(0x478e47));
            if (showID) {
                source.sendSuccess(() -> dietMessage, true);
                source.sendSuccess(() -> dietIDMessage, true);
            } else {
                source.sendSuccess(() -> dietMessage, true);
            }
        }
        return 0;
    }
    
    private static int listAll(CommandSourceStack source, boolean showID) {
        listSpells(source, showID);
        listClassTypes(source, showID);
        listSpecies(source, showID);
        listDiets(source, showID);
        return 0;
    }
    
}
