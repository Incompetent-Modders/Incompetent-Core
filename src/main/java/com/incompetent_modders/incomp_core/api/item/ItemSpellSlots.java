package com.incompetent_modders.incomp_core.api.item;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.common.registry.ModSpells;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.incompetent_modders.incomp_core.core.def.params.SpellResults;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ItemSpellSlots(List<Entry> spells, int maxSlots, int selectedSlot) {
    public static final ItemSpellSlots EMPTY = new ItemSpellSlots(List.of(), 0, 0);
    public static final Codec<ItemSpellSlots> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemSpellSlots> STREAM_CODEC;
    
    public ItemSpellSlots(List<Entry> spells, int maxSlots, int selectedSlot) {
        this.spells = spells;
        this.maxSlots = maxSlots;
        this.selectedSlot = selectedSlot;
    }
    
    public ItemSpellSlots withSpellAdded(Entry spell) {
        return new ItemSpellSlots(Util.copyAndAdd(this.spells, spell), Math.max(this.maxSlots, spell.slot + 1), this.selectedSlot);
    }

    public ItemSpellSlots setSelectedSlot(int selectedSpellSlot) {
        return new ItemSpellSlots(this.spells, this.maxSlots, selectedSpellSlot);
    }
    
    public List<Entry> spells() {
        return this.spells;
    }
    
    public ResourceKey<Spell> getSpell(int slot) {
        for (Entry entry : this.spells) {
            if (entry.slot == slot) {
                return entry.spell;
            }
        }
        return ModSpells.EMPTY;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Entry.CODEC.listOf().fieldOf("spells").forGetter(ItemSpellSlots::spells),
                Codec.INT.fieldOf("max_slots").forGetter(ItemSpellSlots::maxSlots),
                Codec.INT.fieldOf("selected_slot").forGetter(ItemSpellSlots::selectedSlot)
        ).apply(instance, ItemSpellSlots::new));
        STREAM_CODEC = StreamCodec.composite(
                Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemSpellSlots::spells,
                ByteBufCodecs.VAR_INT, ItemSpellSlots::maxSlots,
                ByteBufCodecs.VAR_INT, ItemSpellSlots::selectedSlot,
                ItemSpellSlots::new
        );
    }



    public record Entry(ResourceKey<Spell> spell, int slot) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceKey.codec(ModRegistries.Keys.SPELL).fieldOf("spell").forGetter(Entry::getSpell),
                Codec.INT.fieldOf("slot").forGetter(Entry::getSlot)
        ).apply(instance, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;
        
        public Entry(ResourceKey<Spell> spell, int slot) {
            this.spell = spell;
            this.slot = slot;
        }
        
        public ResourceKey<Spell> getSpell() {
            return this.spell;
        }
        
        public int getSlot() {
            return this.slot;
        }
        
        static {
            STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(ModRegistries.Keys.SPELL), Entry::getSpell, ByteBufCodecs.VAR_INT, Entry::getSlot, Entry::new);
        }
    }
}
