package com.incompetent_modders.incomp_core.api.network.player;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessagePlayerDataSync(CompoundTag playerData) implements CustomPacketPayload {
    public static final Type<MessagePlayerDataSync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "player_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessagePlayerDataSync> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            MessagePlayerDataSync::playerData,
            MessagePlayerDataSync::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessagePlayerDataSync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            CompoundTag playerData = message.playerData();
            CompoundTag oldData = PlayerDataCore.getPlayerData(player);
            if (oldData != playerData) {
                PlayerDataCore.setPlayerData(player, playerData);
            }
        });
    }
}
