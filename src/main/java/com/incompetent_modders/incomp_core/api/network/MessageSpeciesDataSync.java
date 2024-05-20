package com.incompetent_modders.incomp_core.api.network;

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

public record MessageSpeciesDataSync(CompoundTag speciesData) implements CustomPacketPayload {
    public static final Type<MessageSpeciesDataSync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "species_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpeciesDataSync> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            MessageSpeciesDataSync::speciesData,
            MessageSpeciesDataSync::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageSpeciesDataSync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            CompoundTag speciesData = message.speciesData();
            synchronized(speciesData) {
                PlayerDataCore.setSpeciesData(player, speciesData);
            }
        });
    }
}
