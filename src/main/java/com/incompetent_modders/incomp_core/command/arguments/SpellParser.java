package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
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
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class SpellParser {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPELL = new DynamicCommandExceptionType(
            p_121013_ -> Component.translatable("argument.spell.id.invalid", p_121013_)
    );
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Spell> spells;
    private Either<Holder<Spell>, HolderSet<Spell>> result;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    public SpellParser(HolderLookup.Provider provider) {
        this.spells = provider.lookupOrThrow(ModRegistries.SPELL.key());
    }
    public SpellResult parse(StringReader stringReader) throws CommandSyntaxException {
        final MutableObject<Holder<Spell>> mutableobject = new MutableObject<>();
        this.parse(stringReader, new Visitor() {
            public void visitSpell(Holder<Spell> spellHolder) {
                mutableobject.setValue(spellHolder);
            }
        });
        Holder<Spell> holder = Objects.requireNonNull(mutableobject.getValue(), "Parser gave no species type");
        return new SpellResult(holder);
    }
    public void parse(StringReader stringReader, Visitor visitor) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        
        try {
            (new State(stringReader, visitor)).parse();
        } catch (CommandSyntaxException var5) {
            stringReader.setCursor(i);
            throw var5;
        }
    }
    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder) {
        StringReader stringreader = new StringReader(suggestionsBuilder.getInput());
        stringreader.setCursor(suggestionsBuilder.getStart());
        SuggestionsVisitor suggestionsVisitor = new SuggestionsVisitor();
        State state = new State(stringreader, suggestionsVisitor);
        
        try {
            state.parse();
        } catch (CommandSyntaxException ignored) {
        }
        
        return suggestionsVisitor.resolveSuggestions(suggestionsBuilder, stringreader);
    }
    
    private CompletableFuture<Suggestions> suggestSpell(SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.spells.listElementIds().map(ResourceKey::location), suggestionsBuilder);
    }
    
    public static record SpellResult(Holder<Spell> spellHolder) {
        public SpellResult(Holder<Spell> spellHolder) {
            this.spellHolder = spellHolder;
        }
        
        public Holder<Spell> spell() {
            return this.spellHolder;
        }
    }
    
    
    public interface Visitor {
        default void visitSpell(Holder<Spell> spellHolder) {
        }
        
        default void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestionsBuilderCompletableFutureFunction) {
        }
    }
    
    static class SuggestionsVisitor implements Visitor {
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;
        
        SuggestionsVisitor() {
            this.suggestions = SUGGEST_NOTHING;
        }
        
        public void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {
            this.suggestions = suggestions;
        }
        
        public CompletableFuture<Suggestions> resolveSuggestions(SuggestionsBuilder suggestionsBuilder, StringReader stringReader) {
            return this.suggestions.apply(suggestionsBuilder.createOffset(stringReader.getCursor()));
        }
    }
    
    class State {
        private final StringReader reader;
        private final Visitor visitor;
        
        State(StringReader stringReader, Visitor visitor) {
            this.reader = stringReader;
            this.visitor = visitor;
        }
        
        public void parse() throws CommandSyntaxException {
            this.visitor.visitSuggestions(this::suggestSpecies);
            this.readSpecies();
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.visitor.visitSuggestions(SUGGEST_NOTHING);
            }
            
        }
        
        private void readSpecies() throws CommandSyntaxException {
            int i = this.reader.getCursor();
            ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
            this.visitor.visitSpell(SpellParser.this.spells.get(ResourceKey.create(ModRegistries.SPELL.key(), resourcelocation)).orElseThrow(() -> {
                this.reader.setCursor(i);
                return ERROR_UNKNOWN_SPELL.createWithContext(this.reader, resourcelocation);
            }));
        }
        
        private CompletableFuture<Suggestions> suggestAssignment(SuggestionsBuilder suggestionsBuilder) {
            if (suggestionsBuilder.getRemaining().isEmpty()) {
                suggestionsBuilder.suggest(String.valueOf('='));
            }
            
            return suggestionsBuilder.buildFuture();
        }
        
        private CompletableFuture<Suggestions> suggestSpecies(SuggestionsBuilder suggestionsBuilder) {
            return SharedSuggestionProvider.suggestResource(SpellParser.this.spells.listElementIds().map(ResourceKey::location), suggestionsBuilder);
        }
    }
}
