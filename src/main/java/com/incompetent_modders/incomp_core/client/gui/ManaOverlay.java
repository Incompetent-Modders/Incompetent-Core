package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.DrawingUtils;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class ManaOverlay implements LayeredDraw.Layer {
    public static final ManaOverlay INSTANCE = new ManaOverlay();
    @Override
    public void render(GuiGraphics guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        
        LocalPlayer player = mc.player;
        //If the player has an inventory screen open, don't render the overlay
        
        ResourceLocation manaBar = getPlayerClassType(player).getManaOverlayTexture("bar");
        ResourceLocation manaFrame = getPlayerClassType(player).getManaOverlayTexture("frame");
        ResourceLocation bubbles = getPlayerClassType(player).getManaOverlayTexture("bubbles");
        
        if (player == null)
            return;
        
        double mana = PlayerDataCore.ManaData.getMana(player);
        double maxMana = PlayerDataCore.ManaData.getMaxMana(player);
        DrawingUtils.blitSprite(guiGraphics, manaBar, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, (int) (50 * getManaPercentage(player)), 16);
        //DrawingUtils.blitSprite(guiGraphics, bubbles, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage(player)), 16);
        DrawingUtils.blitSprite(guiGraphics, manaFrame, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, 50, 16);
        Component value = Component.literal(mana + " / " + maxMana);
        int color = 0x00FF00;
        guiGraphics.drawString(mc.font, value, 16, 5, color);
    }
    public void renderBar(GuiGraphics graphics, ResourceLocation bar, ResourceLocation bubbles, int screenWidth, int screenHeight, LocalPlayer player) {
        DrawingUtils.blitSprite(graphics, bar, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage(player)), 16);
        DrawingUtils.blitSprite(graphics, bubbles, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage(player)), 16);
    }
    public float getMana(LocalPlayer player) {
        return (float) PlayerDataCore.ManaData.getMana(player);
    }
    
    public float getMaxMana(LocalPlayer player) {
        return (float) PlayerDataCore.ManaData.getMaxMana(player);
    }
    
    public float getManaPercentage(LocalPlayer player) {
        return getMana(player) / getMaxMana(player);
    }
    public ClassType getPlayerClassType(LocalPlayer player) {
        if (PlayerDataCore.ClassData.getPlayerClassType(player) == null)
            return ModClassTypes.NONE.get();
        return PlayerDataCore.ClassData.getPlayerClassType(player);
    }
}
