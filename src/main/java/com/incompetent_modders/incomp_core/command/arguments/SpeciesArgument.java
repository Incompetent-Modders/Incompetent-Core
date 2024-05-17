package com.incompetent_modders.incomp_core.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SpeciesArgument implements ArgumentType<SpeciesInput> {
    private static final Collection<String> EXAMPLES = Arrays.asList("human", "incompetent_core:human", "human{foo=bar}");
    private final SpeciesParser parser;
    
    public SpeciesArgument(CommandBuildContext commandBuildContext) {
        this.parser = new SpeciesParser(commandBuildContext);
    }
    
    @Override
    public SpeciesInput parse(StringReader p_120962_) throws CommandSyntaxException {
        SpeciesParser.SpeciesResult speciesTypeResult = this.parser.parse(p_120962_);
        return new SpeciesInput(speciesTypeResult.speciesTypeHolder());
    }
    
    public static <S> SpeciesInput getSpecies(CommandContext<S> commandContext, String name) {
        return commandContext.getArgument(name, SpeciesInput.class);
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return this.parser.fillSuggestions(suggestionsBuilder);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
