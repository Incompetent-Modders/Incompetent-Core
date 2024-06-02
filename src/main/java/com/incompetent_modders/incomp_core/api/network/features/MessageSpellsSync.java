package com.incompetent_modders.incomp_core.api.network.features;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.ClientBoundPacket;
import com.incompetent_modders.incomp_core.api.network.CustomIncompetentPayload;
import com.incompetent_modders.incomp_core.api.network.player.MessagePlayerDataSync;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.function.IntFunction;

public record MessageSpellsSync(List<ResourceLocation> spells) implements ClientBoundPacket {
    public static final Type<MessageSpellsSync> TYPE = CustomIncompetentPayload.createType("spells_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellsSync> CODEC = StreamCodec.ofMember(
            MessageSpellsSync::write,
            MessageSpellsSync::decode);
    
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeCollection(spells, ResourceLocation.STREAM_CODEC);
    }
    
    private static MessageSpellsSync decode(RegistryFriendlyByteBuf buf) {
        var spells = buf.readList(ResourceLocation.STREAM_CODEC);
        return new MessageSpellsSync(spells);
    }
    
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleOnClient(Player player) {
        try {
        
        } catch (Exception ignored) {
        }
    }
}
