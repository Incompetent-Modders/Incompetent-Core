package com.incompetent_modders.incomp_core.common.command.arguments;

import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
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
import java.util.stream.Stream;

public class SpellArgument implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("test_spell", "incompetent_core:test_spell", "test_spell{foo=bar}");
    public SpellArgument() {
    }
    @Override
    public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
        return ResourceLocation.read(stringReader);
    }
    public static <S> ResourceLocation getSpell(CommandContext<S> commandContext, String name) {
        return commandContext.getArgument(name, ResourceLocation.class);
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(SpellListener.getAllSpells(), suggestionsBuilder);
    }
    private Stream<ResourceLocation> getSpells()
    {
        return SpellListener.getAllSpells().stream();
    }
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
