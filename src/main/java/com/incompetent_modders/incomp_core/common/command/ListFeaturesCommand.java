package com.incompetent_modders.incomp_core.common.command;

import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.client.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import com.incompetent_modders.incomp_core.client.ClientSpellManager;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
            Component result = Component.translatable("commands.incompetent_core.list_features.spells", ClientSpellManager.getInstance().getSpellList().size()).withStyle(ClientUtil.styleFromColor(0x624a95));
            source.sendSuccess(() -> result, false);
            for (ResourceLocation spellID : ClientSpellManager.getInstance().getSpellList().keySet()) {
                Component spellIDMessage = Component.literal("> (" + spellID.toString() + ")").withStyle(ClientUtil.styleFromColor(0x624a95));
                Component spellNameMessage = ClientSpellManager.getDisplayName(spellID).copy().withStyle(ClientUtil.styleFromColor(0xa05baf));
                if (showID) {
                    source.sendSuccess(() -> spellNameMessage, false);
                    source.sendSuccess(() -> spellIDMessage, false);
                } else {
                    source.sendSuccess(() -> spellNameMessage, false);
                }
            }
        return 0;
    }
    
    private static int listClassTypes(CommandSourceStack source, boolean showID) {
            Component result = Component.translatable("commands.incompetent_core.list_features.class_types", ClientClassTypeManager.getInstance().getClassTypeList().size()).withStyle(ClientUtil.styleFromColor(0xe19635));
            source.sendSuccess(() -> result, false);
            for (ResourceLocation classTypeID : ClientClassTypeManager.getInstance().getClassTypeList()) {
                Component classTypeMessage = ClassTypeListener.getDisplayName(classTypeID).copy().withStyle(ClientUtil.styleFromColor(0xe5ab49));
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
            Component result = Component.translatable("commands.incompetent_core.list_features.species", ClientSpeciesManager.getInstance().getSpeciesList().size()).withStyle(ClientUtil.styleFromColor(0x4998e5));
            source.sendSuccess(() -> result, false);
            for (ResourceLocation speciesID : ClientSpeciesManager.getInstance().getSpeciesList()) {
                Component speciesMessage = SpeciesListener.getDisplayName(speciesID).copy().withStyle(ClientUtil.styleFromColor(0x6eb0ea));
                Component speciesIDMessage = Component.literal("> (" + speciesID + ")").withStyle(ClientUtil.styleFromColor(0x4998e5));
                if (showID) {
                    source.sendSuccess(() -> speciesMessage, false);
                    source.sendSuccess(() -> speciesIDMessage, false);
                } else {
                    source.sendSuccess(() -> speciesMessage, false);
                }
            }
        return 0;
    }
    
    private static int listDiets(CommandSourceStack source, boolean showID) {
            Component result = Component.translatable("commands.incompetent_core.list_features.diets", ClientDietManager.getInstance().getDietList().size()).withStyle(ClientUtil.styleFromColor(0x478e47));
            source.sendSuccess(() -> result, false);
            for (ResourceLocation dietID : ClientDietManager.getInstance().getDietList()) {
                Component dietMessage = DietListener.getDisplayName(dietID).copy().withStyle(ClientUtil.styleFromColor(0x56ad56));
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
