package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.api.player.ManaData;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.DrawingUtils;
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
        
        ResourceLocation manaBar = getManaOverlayTexture("bar", player);
        ResourceLocation manaFrame = getManaOverlayTexture("frame", player);
        ResourceLocation bubbles = getManaOverlayTexture("bubbles", player);
        
        if (player == null)
            return;
        
        double mana = ManaData.Get.mana(player);
        double maxMana = ManaData.Get.maxMana(player);
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
        return (float) ManaData.Get.mana(player);
    }
    
    public float getMaxMana(LocalPlayer player) {
        return (float) ManaData.Get.maxMana(player);
    }
    
    public float getManaPercentage(LocalPlayer player) {
        return getMana(player) / getMaxMana(player);
    }
    public ResourceLocation getPlayerClassType(LocalPlayer player) {
        return ClassData.Get.playerClassType(player);
    }
    
    public ResourceLocation getManaOverlayTexture(String spriteName, LocalPlayer player) {
        if (ClassTypeListener.getClassTypeProperties(getPlayerClassType(player)) == null)
            return new ResourceLocation(IncompCore.MODID, "mana_bar/" + spriteName);
        if (!ClassTypeListener.getClassTypeProperties(getPlayerClassType(player)).useClassSpecificTexture())
            return new ResourceLocation(IncompCore.MODID, "mana_bar/" + spriteName);
        return new ResourceLocation(getPlayerClassType(player).getNamespace(), "mana_bar/" + getPlayerClassType(player).getPath() + "/" + spriteName);
    }
}
