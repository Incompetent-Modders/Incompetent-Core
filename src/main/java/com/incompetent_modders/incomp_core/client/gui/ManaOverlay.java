package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.screen.DrawingUtils;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class ManaOverlay implements LayeredDraw.Layer {
    public static final ManaOverlay INSTANCE = new ManaOverlay();
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        
        LocalPlayer player = mc.player;
        //If the player has an inventory screen open, don't render the overlay
        
        ResourceLocation manaBar = getManaOverlayTexture("bar", player);
        ResourceLocation manaFrame = getManaOverlayTexture("frame", player);
        ResourceLocation bubbles = getManaOverlayTexture("bubbles", player);
        
        if (player == null) return;
        if (PlayerDataHelper.getMaxMana(player) <= 0) return;
        
        double mana = PlayerDataHelper.getMana(player);
        double maxMana = PlayerDataHelper.getMaxMana(player);
        DrawingUtils.blitSprite(guiGraphics, manaBar, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, (int) (50 * getManaPercentage(player)), 16);
        //DrawingUtils.blitSprite(guiGraphics, bubbles, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage(player)), 16);
        DrawingUtils.blitSprite(guiGraphics, manaFrame, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, 50, 16);
        Component value = Component.literal(mana + " / " + maxMana);
        int color = 0x00FF00;
        guiGraphics.drawString(mc.font, value, 16, 5, color);
    }

    public double getMana(Player player) {
        return PlayerDataHelper.getMana(player);
    }
    
    public float getMaxMana(Player player) {
        return PlayerDataHelper.getMaxMana(player);
    }
    
    public double getManaPercentage(Player player) {
        return getMana(player) / getMaxMana(player);
    }
    
    public ResourceLocation getManaOverlayTexture(String spriteName, LocalPlayer player) {
        Pair<ResourceKey<ClassType>, ClassType> classType = PlayerDataHelper.getClassTypeWithKey(player);
        if (classType.getSecond().useClassSpecificTexture()) {
            String path = classType.getFirst().location().getPath();
            return ResourceLocation.fromNamespaceAndPath(classType.getFirst().location().getNamespace(), "mana_bar/" + path + "/" + spriteName);
        } else {
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "mana_bar/" + spriteName);
        }
    }
}
