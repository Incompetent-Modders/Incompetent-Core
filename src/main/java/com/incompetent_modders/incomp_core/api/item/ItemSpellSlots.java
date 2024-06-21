package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ItemSpellSlots(List<Entry> spells) {
    public static final ItemSpellSlots EMPTY = new ItemSpellSlots(List.of());
    public static final Codec<ItemSpellSlots> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSpellSlots> STREAM_CODEC;
    
    public ItemSpellSlots(List<Entry> spells) {
        this.spells = spells;
    }
    
    public ItemSpellSlots withSpellAdded(Entry spell) {
        return new ItemSpellSlots(Util.copyAndAdd(this.spells, spell));
    }
    
    public List<Entry> spells() {
        return this.spells;
    }
    
    public ResourceLocation getSpell(int slot) {
        return this.spells.stream().filter((entry) -> entry.slot == slot).findFirst().map(Entry::spell).orElse(CastingItemUtil.emptySpell);
    }
    static {
        CODEC = Entry.CODEC.listOf().xmap(ItemSpellSlots::new, ItemSpellSlots::spells);
        STREAM_CODEC = Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ItemSpellSlots::new, ItemSpellSlots::spells);
    }
    
    public record Entry(ResourceLocation spell, int slot) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ResourceLocation.CODEC.fieldOf("spell").forGetter(Entry::getSpell), Codec.INT.fieldOf("slot").forGetter(Entry::getSlot)).apply(instance, Entry::new);
        });
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;
        
        public Entry(ResourceLocation spell, int slot) {
            this.spell = spell;
            this.slot = slot;
        }
        
        public ResourceLocation getSpell() {
            return this.spell;
        }
        
        public int getSlot() {
            return this.slot;
        }
        
        static {
            STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, Entry::getSpell, ByteBufCodecs.VAR_INT, Entry::getSlot, Entry::new);
        }
    }
}
