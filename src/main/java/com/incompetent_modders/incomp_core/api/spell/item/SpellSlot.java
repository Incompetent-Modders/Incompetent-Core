package com.incompetent_modders.incomp_core.api.spell.item;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SpellSlot(int slot, Holder<Spell> spell) {
    
    public static final Codec<SpellSlot> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("slot").forGetter(SpellSlot::getSlot),
                Spell.DIRECT_CODEC.fieldOf("spell").forGetter(SpellSlot::getSpell)
        ).apply(instance, SpellSlot::new);
    });
    public static final StreamCodec<ByteBuf, SpellSlot> STREAM_CODEC;
    
    public int getSlot() {
        return slot;
    }

    public Holder<Spell> getSpell() {
        return spell;
    }
    
    
    static {
        STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    }
    
}
