package com.incompetent_modders.incomp_core.devtest.spell_results;

import com.incompetent_modders.incomp_core.api.spell.data.SpellResult;
import com.incompetent_modders.incomp_core.api.spell.data.SpellResultType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class DoNothingResult extends SpellResult {
    
    public static final MapCodec<DoNothingResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("hotbar", true).forGetter(result -> {
                return result.hotbar;
            })
    ).apply(instance, DoNothingResult::new));
    
    private final boolean hotbar;
    public DoNothingResult(boolean hotbar) {
        this.hotbar = hotbar;
    }
    @Override
    public void execute(Player player) {
        player.displayClientMessage(Component.translatable("incompetent_core.do_nothing"), hotbar);
    }
    
    @Override
    public SpellResultType<? extends SpellResult> getType() {
        return null;
    }
}
