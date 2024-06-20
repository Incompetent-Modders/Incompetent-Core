package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.client.player_data.ClientClassData;
import com.incompetent_modders.incomp_core.client.player_data.ClientManaData;
import com.incompetent_modders.incomp_core.client.screen.DrawingUtils;
import net.minecraft.client.DeltaTracker;
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
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        
        LocalPlayer player = mc.player;
        //If the player has an inventory screen open, don't render the overlay
        
        ResourceLocation manaBar = getManaOverlayTexture("bar");
        ResourceLocation manaFrame = getManaOverlayTexture("frame");
        ResourceLocation bubbles = getManaOverlayTexture("bubbles");
        
        if (player == null)
            return;
        
        double mana = ClientManaData.getInstance().getMana();
        double maxMana = ClientManaData.getInstance().getMaxMana();
        DrawingUtils.blitSprite(guiGraphics, manaBar, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, (int) (50 * getManaPercentage()), 16);
        //DrawingUtils.blitSprite(guiGraphics, bubbles, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage(player)), 16);
        DrawingUtils.blitSprite(guiGraphics, manaFrame, guiGraphics.guiWidth() / 2 + 120, guiGraphics.guiHeight() - 53, 50, 16);
        Component value = Component.literal(mana + " / " + maxMana);
        int color = 0x00FF00;
        guiGraphics.drawString(mc.font, value, 16, 5, color);
    }
    public void renderBar(GuiGraphics graphics, ResourceLocation bar, ResourceLocation bubbles, int screenWidth, int screenHeight) {
        DrawingUtils.blitSprite(graphics, bar, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage()), 16);
        DrawingUtils.blitSprite(graphics, bubbles, screenWidth / 2 + 120, screenHeight - 53, (int) (50 * getManaPercentage()), 16);
    }
    public float getMana() {
        return (float) ClientManaData.getInstance().getMana();
    }
    
    public float getMaxMana() {
        return (float) ClientManaData.getInstance().getMaxMana();
    }
    
    public float getManaPercentage() {
        return getMana() / getMaxMana();
    }
    public ResourceLocation getPlayerClassType() {
        return ClientClassData.getInstance().getPlayerClassType();
    }
    
    public ResourceLocation getManaOverlayTexture(String spriteName) {
        if (ClassTypeListener.getClassTypeProperties(getPlayerClassType()) == null)
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "mana_bar/" + spriteName);
        if (!ClassTypeListener.getClassTypeProperties(getPlayerClassType()).useClassSpecificTexture())
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "mana_bar/" + spriteName);
        return ResourceLocation.fromNamespaceAndPath(getPlayerClassType().getNamespace(), "mana_bar/" + getPlayerClassType().getPath() + "/" + spriteName);
    }
}
