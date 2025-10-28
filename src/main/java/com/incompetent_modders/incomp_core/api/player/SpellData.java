package com.incompetent_modders.incomp_core.api.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SpellData(Optional<ResourceKey<Spell>> selectedSpell) {
    public static final Codec<SpellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(ModRegistries.Keys.SPELL).optionalFieldOf("selected_spell").forGetter(SpellData::selectedSpell)
    ).apply(instance, SpellData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceKey.streamCodec(ModRegistries.Keys.SPELL)), SpellData::selectedSpell,
            SpellData::new
    );

    public static SpellData create(ResourceKey<Spell> spellKey) {
        return new SpellData(Optional.of(spellKey));
    }

    public static SpellData createDefault() {
        return new SpellData(Optional.empty());
    }

    public SpellData setSpell(ResourceKey<Spell> spellKey) {
        return new SpellData(Optional.of(spellKey));
    }

    public ResourceKey<Spell> getSelectedSpell() {
        return selectedSpell.orElse(null);
    }
}
