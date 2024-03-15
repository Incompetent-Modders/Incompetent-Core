package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SpellParser {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPELL = new DynamicCommandExceptionType(
            p_121013_ -> Component.translatable("argument.spell.id.invalid", p_121013_)
    );
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Spell> spells;
    private final StringReader reader;
    private Either<Holder<Spell>, HolderSet<Spell>> result;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    private SpellParser(HolderLookup<Spell> spells, StringReader stringReader) {
        this.spells = spells;
        this.reader = stringReader;
    }
    public static SpellResult parseForSpell(HolderLookup<Spell> spells, StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        
        try {
            SpellParser spellParser = new SpellParser(spells, stringReader);
            spellParser.parse();
            Holder<Spell> holder = spellParser.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
            return new SpellResult(holder);
        } catch (CommandSyntaxException commandsyntaxexception) {
            stringReader.setCursor(i);
            throw commandsyntaxexception;
        }
    }
    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Spell> spells, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringreader = new StringReader(suggestionsBuilder.getInput());
        stringreader.setCursor(suggestionsBuilder.getStart());
        SpellParser spellParser = new SpellParser(spells, stringreader);
        
        try {
            spellParser.parse();
        }
        catch (CommandSyntaxException commandsyntaxexception) {
        }
        
        return spellParser.suggestions.apply(suggestionsBuilder.createOffset(stringreader.getCursor()));
    }
    private void readSpell() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
        Optional<? extends Holder<Spell>> optional = this.spells.get(ResourceKey.create(ModRegistries.SPELL.key(), resourcelocation));
        this.result = Either.left(optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ERROR_UNKNOWN_SPELL.createWithContext(this.reader, resourcelocation);
        }));
    }
    
    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestSpell;
        this.readSpell();
    }
    private CompletableFuture<Suggestions> suggestSpell(SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.spells.listElementIds().map(ResourceKey::location), suggestionsBuilder);
    }
    
    
    public static record SpellResult(Holder<Spell> spell) {
    }
}
