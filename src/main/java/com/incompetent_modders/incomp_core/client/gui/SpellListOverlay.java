package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.api.spell.Spell;
import com.incompetent_modders.incomp_core.api.spell.Spells;
import com.incompetent_modders.incomp_core.client.DrawingUtils;
import com.incompetent_modders.incomp_core.item.ISpellCastingItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class SpellListOverlay implements IGuiOverlay {
    public static final SpellListOverlay INSTANCE = new SpellListOverlay();
    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        
        LocalPlayer player = mc.player;
        //If the player has an inventory screen open, don't render the overlay
        int spellIconInsetX = 2;
        int spellIconInsetY = 2;
        if (player == null)
            return;
        
        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ISpellCastingItem) && !(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ISpellCastingItem))
            return;
        
        Item staffMainHand = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        Spell spellMainHand = staffMainHand instanceof ISpellCastingItem staffItem ? staffItem.getCurrentSpell(player.getItemInHand(InteractionHand.MAIN_HAND)) : Spells.EMPTY.get();
        Item staffOffHand = player.getItemInHand(InteractionHand.OFF_HAND).getItem();
        Spell spellOffHand = staffOffHand instanceof ISpellCastingItem staffItem ? staffItem.getCurrentSpell(player.getItemInHand(InteractionHand.OFF_HAND)) : Spells.EMPTY.get();
        
        int x1 = screenWidth + spellIconInsetX;
        // y is upside-down so this is the other way round
        int y1 = screenHeight - spellIconInsetY - 16;
        
        ResourceLocation spellMainHandIcon = spellMainHand.getSpellIconLocation();
        ResourceLocation spellOffHandIcon = spellOffHand.getSpellIconLocation();
        boolean isMainHandSpell = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ISpellCastingItem;
        boolean isOffHandSpell = player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ISpellCastingItem;
        Component spellMainHandName = spellMainHand.getDisplayName();
        Component spellOffHandName = spellOffHand.getDisplayName();
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate((double) screenWidth / 2 - 120, screenHeight - 53, 0);
        if (isMainHandSpell || isOffHandSpell) {
            DrawingUtils.drawTexturedRect(x1, y1, 0, 0, 16, 16, 16, 16);
            DrawingUtils.drawTexturedFlippedRect(x1, y1, 0, 0, screenWidth, screenHeight, 256, 256, false, false);
            guiGraphics.drawString(mc.font, isMainHandSpell ? spellMainHandName : spellOffHandName, 16, 5, 0x00FF00);
            guiGraphics.blitSprite(isMainHandSpell ? spellMainHandIcon : spellOffHandIcon, 16, 16, 0, 0, 16, screenHeight - 16, screenWidth / 2, 16);
        }
        poseStack.popPose();
    }
}
