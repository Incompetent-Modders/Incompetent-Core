package com.incompetent_modders.incomp_core.api.network;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MessageClassDataSync(CompoundTag classData) implements CustomPacketPayload {
    public static final Type<MessageClassDataSync> TYPE = new Type<>(new ResourceLocation(IncompCore.MODID, "class_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageClassDataSync> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            MessageClassDataSync::classData,
            MessageClassDataSync::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(final MessageClassDataSync message, final IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            PlayerDataCore.setClassData(player, message.classData());
        });
    }
}
