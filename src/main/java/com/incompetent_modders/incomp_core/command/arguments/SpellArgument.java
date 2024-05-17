package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
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
import java.util.stream.Stream;

public class SpellArgument implements ArgumentType<SpellInput> {
    private static final Collection<String> EXAMPLES = Arrays.asList("test_spell", "incompetent_core:test_spell", "test_spell{foo=bar}");
    private final SpellParser parser;
    public SpellArgument(CommandBuildContext commandBuildContext) {
        this.parser = new SpellParser(commandBuildContext);
    }
    @Override
    public SpellInput parse(StringReader stringReader) throws CommandSyntaxException {
        SpellParser.SpellResult spellResult = this.parser.parse(stringReader);
        return new SpellInput(spellResult.spellHolder());
    }
    public static <S> Spell getSpell(CommandContext<S> commandContext, String name) {
        return commandContext.getArgument(name, Spell.class);
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return this.parser.fillSuggestions(suggestionsBuilder);
    }
    private Stream<Spell> getStaticSpells()
    {
        return ModRegistries.SPELL.stream();
    }
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
