package com.incompetent_modders.incomp_core.api.network.packets;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.network.Packet;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class IncompPlayerDataSyncPacket extends Packet {
    
    public static final ResourceLocation ID = new ResourceLocation(IncompCore.MODID, "player_data_sync");
    private final CompoundTag classData;
    private final CompoundTag manaData;
    public IncompPlayerDataSyncPacket(Player pe) {
        this.classData = PlayerDataCore.getClassData(pe);
        this.manaData = PlayerDataCore.getManaData(pe);
    }
    
    public IncompPlayerDataSyncPacket(FriendlyByteBuf buffer) {
        classData = buffer.readNbt();
        manaData = buffer.readNbt();
    }
    
    @Override
    public void handleServer(PlayPayloadContext context) {
    
    }
    
    @Override
    public void handleClient(PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            // Update client-side nbt
            Level world = ClientUtil.getWorld();
            Player player = ClientUtil.getPlayer();
            if (world != null) {
                PlayerDataCore.setClassData(player, classData);
                PlayerDataCore.setManaData(player, manaData);
            }
        });
    }
    
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(classData);
        buf.writeNbt(manaData);
    }
    
    @Override
    public ResourceLocation id() {
        return ID;
    }
}
