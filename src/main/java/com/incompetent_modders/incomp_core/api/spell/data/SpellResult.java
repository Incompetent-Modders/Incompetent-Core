package com.incompetent_modders.incomp_core.api.spell.data;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;

public abstract class SpellResult {
    public static final Codec<SpellResult> DIRECT_CODEC = Codec.lazyInitialized(ModRegistries.SPELL_RESULT_TYPE::byNameCodec)
            .dispatch(SpellResult::getType, SpellResultType::codec);
    
    public abstract void execute(Player player);
    
    public abstract SpellResultType<? extends SpellResult> getType();
}
