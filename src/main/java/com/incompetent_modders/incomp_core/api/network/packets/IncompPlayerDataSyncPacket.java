package com.incompetent_modders.incomp_core.api.network.packets;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.NetworkEvent;

public class IncompPlayerDataSyncPacket {
    
    private final CompoundTag data;
    
    public IncompPlayerDataSyncPacket(Player pe) {
        this.data = PlayerDataCore.getIncompCoreData(pe);
    }
    
    public IncompPlayerDataSyncPacket(FriendlyByteBuf buffer) {
        data = buffer.readNbt();
    }
    
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeNbt(data);
    }
    
    public void handle(NetworkEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            // Update client-side nbt
            Level world = ClientUtil.getWorld();
            Player player = ClientUtil.getPlayer();
            if (world != null) {
                PlayerDataCore.setIncompCoreData(player, data);
            }
        });
        ctx.setPacketHandled(true);
    }
}
