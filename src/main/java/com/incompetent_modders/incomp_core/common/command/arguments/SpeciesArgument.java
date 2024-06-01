package com.incompetent_modders.incomp_core.common.command.arguments;

import com.incompetent_modders.incomp_core.api.json.species.SpeciesListener;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SpeciesArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("human", "incompetent_core:human", "human{foo=bar}");
    
    public SpeciesArgument() {
    }
    
    @Override
    public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
        return ResourceLocation.read(stringReader);
    }
    
    public static <S> ResourceLocation getSpecies(CommandContext<S> commandContext, String name) {
        return commandContext.getArgument(name, ResourceLocation.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(SpeciesListener.getAllSpecies(), suggestionsBuilder);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
