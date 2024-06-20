package com.incompetent_modders.incomp_core.mixin;

import com.incompetent_modders.incomp_core.api.syncing.FeaturesSyncer;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    @Final
    private List<ServerPlayer> players;
    
    @Shadow
    @Final
    private MinecraftServer server;
    
    @Inject(
            method = "placeNewPlayer",
            at = @At("TAIL")
    )
    private void incompetentCore$afterSyncData(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        if (FMLEnvironment.dist.isClient()) {
            FeaturesSyncer.syncAllToPlayer(serverPlayer);
        }
    }
    @Inject(
            method = "reloadResources",
            at = @At("TAIL")
    )
    private void incompetentCore$afterSyncDataToAll(CallbackInfo ci) {
        if (FMLEnvironment.dist.isClient()) {
            FeaturesSyncer.syncAllToAll(server);
        }
    }
}
