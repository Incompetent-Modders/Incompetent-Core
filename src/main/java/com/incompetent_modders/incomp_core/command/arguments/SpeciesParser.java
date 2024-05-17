package com.incompetent_modders.incomp_core.command.arguments;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
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

public class SpeciesParser {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPECIES_TYPE = new DynamicCommandExceptionType(
            p_121013_ -> Component.translatable("argument.species_type.id.invalid", p_121013_)
    );
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<SpeciesType> speciesTypes;
    private Either<Holder<SpeciesType>, HolderSet<SpeciesType>> result;
    private final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;
    public SpeciesParser(HolderLookup.Provider provider) {
        this.speciesTypes = provider.lookupOrThrow(ModRegistries.SPECIES_TYPE.key());
    }
    public SpeciesResult parse(StringReader stringReader) throws CommandSyntaxException {
        final MutableObject<Holder<SpeciesType>> mutableobject = new MutableObject<>();
        this.parse(stringReader, new Visitor() {
            public void visitSpecies(Holder<SpeciesType> speciesTypeHolder) {
                mutableobject.setValue(speciesTypeHolder);
            }
        });
        Holder<SpeciesType> holder = Objects.requireNonNull(mutableobject.getValue(), "Parser gave no species type");
        return new SpeciesResult(holder);
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
    
    private CompletableFuture<Suggestions> suggestSpeciesType(SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.speciesTypes.listElementIds().map(ResourceKey::location), suggestionsBuilder);
    }
    
    public static record SpeciesResult(Holder<SpeciesType> speciesTypeHolder) {
        public SpeciesResult(Holder<SpeciesType> speciesTypeHolder) {
            this.speciesTypeHolder = speciesTypeHolder;
        }
        
        public Holder<SpeciesType> item() {
            return this.speciesTypeHolder;
        }
    }
    
    public interface Visitor {
        default void visitSpecies(Holder<SpeciesType> speciesTypeHolder) {
        }
        
        default void visitSuggestions(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> p_335635_) {
        }
    }
    
    static class SuggestionsVisitor implements Visitor {
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;
        
        SuggestionsVisitor() {
            this.suggestions = SpeciesParser.SUGGEST_NOTHING;
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
            this.visitor.visitSpecies(SpeciesParser.this.speciesTypes.get(ResourceKey.create(ModRegistries.SPECIES_TYPE.key(), resourcelocation)).orElseThrow(() -> {
                this.reader.setCursor(i);
                return ERROR_UNKNOWN_SPECIES_TYPE.createWithContext(this.reader, resourcelocation);
            }));
        }
        
        private CompletableFuture<Suggestions> suggestAssignment(SuggestionsBuilder suggestionsBuilder) {
            if (suggestionsBuilder.getRemaining().isEmpty()) {
                suggestionsBuilder.suggest(String.valueOf('='));
            }
            
            return suggestionsBuilder.buildFuture();
        }
        
        private CompletableFuture<Suggestions> suggestSpecies(SuggestionsBuilder suggestionsBuilder) {
            return SharedSuggestionProvider.suggestResource(SpeciesParser.this.speciesTypes.listElementIds().map(ResourceKey::location), suggestionsBuilder);
        }
    }
}
