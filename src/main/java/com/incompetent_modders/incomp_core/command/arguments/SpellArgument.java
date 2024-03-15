package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SpellArgument implements ArgumentType<Spell> {
    private static final Collection<String> EXAMPLES = Arrays.asList("goodberry", "incompetent_druidry:goodberry", "goodberry{foo=bar}");
    public static final DynamicCommandExceptionType invalidSpell = new DynamicCommandExceptionType(
            (input) -> Component.translatable("argument.spell.id.invalid", input));
    
    @Override
    public Spell parse(StringReader reader) throws CommandSyntaxException
    {
        String name = reader.readQuotedString();//TODO does this work properly?
        for(Spell s : getStaticSpells().toList())
            if(s.getSpellIdentifier().toString().equalsIgnoreCase(name))
                return s;
        throw invalidSpell.create(name);
    }
    public static <S> Spell getSpell(CommandContext<S> commandContext, String p_120965_) {
        return commandContext.getArgument(p_120965_, Spell.class);
    }
    
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(getStaticSpells().map(mix -> "\""+mix.getSpellIdentifier()+"\""), suggestionsBuilder);
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
