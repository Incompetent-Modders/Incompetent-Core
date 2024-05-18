package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.client.DrawingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public class SpellListOverlay implements LayeredDraw.Layer {
    public static final SpellListOverlay INSTANCE = new SpellListOverlay();
    //public static final ResourceLocation castTimeIcon = new ResourceLocation(MODID, "spell_list/cast_time");
    //public static final ResourceLocation castTimeSideIcon = new ResourceLocation(MODID, "spell_list/cast_time_side");
    //public static final ResourceLocation castTimeTopIcon = new ResourceLocation(MODID, "spell_list/cast_time_top");
    //public static final ResourceLocation spellFrameIcon = new ResourceLocation(MODID, "spell_list/spell_frame");
    //public static final ResourceLocation spellSlotFrameIcon = new ResourceLocation(MODID, "spell_list/spell_slot_frame");
    public static final String spriteLoc = "spell_list";
    static final int CAST_TIME = 20;
    public static ResourceLocation classType;
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;
        
        LocalPlayer player = mc.player;
        
        //If the player has an inventory screen open, don't render the overlay
        int spellIconInsetX = 2;
        int spellIconInsetY = 2;
        if (player == null)
            return;
        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem) && !(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SpellCastingItem))
            return;
        
        int x1 = graphics.guiWidth() + spellIconInsetX;
        int y1 = graphics.guiHeight() - spellIconInsetY - 16;
        if (getSelectedSpell(player) == null)
            return;
        //ClassType classType = getWielderClassType(player);
        //ResourceLocation castTimeSideIcon = getWielderClassType(player).getSpellOverlayTexture("cast_time_side");
        //ResourceLocation castTimeTopIcon = getWielderClassType(player).getSpellOverlayTexture("cast_time_top");
        ResourceLocation spellFrameIcon = getSpellOverlayTexture("spell_frame", player);
        ResourceLocation spellSlotFrameIcon = getSpellOverlayTexture("spell_slot_frame", player);
        
        ResourceLocation spellIcon = new ResourceLocation(this.getSelectedSpell(player).getNamespace(), "spells/" + this.getSelectedSpell(player).getPath());
        Component spellName = SpellListener.getDisplayName(getSelectedSpell(player));
        
        //PoseStack poseStack = graphics.pose();
        //poseStack.pushPose();
        //poseStack.scale(1.25F, 1.25F, 1.25F);
        float totalDrawTime = SpellListener.getSpellProperties(getSelectedSpell(player)).drawTime();
        
        //DrawingUtils.drawTexturedRect(x1, y1, 0, 0, 16, 16, 16, 16);
        //DrawingUtils.drawTexturedFlippedRect(x1, y1, 0, 0, screenWidth, screenHeight, 256, 256, false, false);
        DrawingUtils.drawString(graphics, mc.font, spellName, 16, 10, 0x00FF00);
        DrawingUtils.blitSprite(graphics, spellFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
        DrawingUtils.blitSprite(graphics, spellIcon, graphics.guiWidth() - (graphics.guiWidth() - 17), graphics.guiHeight() - 37, 26, 26);
        DrawingUtils.blitSprite(graphics, spellSlotFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
        //if (getSelectedSpell(player).hasSpellCatalyst()) {
        //    graphics.renderItem(getSelectedSpell(player).getSpellCatalyst(), screenWidth - (screenWidth - 10), screenHeight - 52);
        //    if (getSelectedSpell(player).getSpellCatalyst().getCount() > 1)
        //        DrawingUtils.drawNumberString(graphics, mc.font, getSelectedSpell(player).getSpellCatalyst().getCount(), screenWidth - (screenWidth - 16), screenHeight - 52, 0xFFFFFF);
        //}
        //DrawingUtils.blitSprite(graphics, castTimeTopIcon, screenWidth - (screenWidth - 10), screenHeight - 52, (int) (20 * castCompletionPercent + (0) / 2), 3);
        //DrawingUtils.blitSprite(graphics, castTimeSideIcon, screenWidth - (screenWidth - 10), screenHeight - 52, 3, (int) (20 * (1 - castCompletionPercent)));
        //poseStack.popPose();
    }
    
    //public float getSpellCooldownPercent(ItemStack stack) {
    //    if (!(stack.getItem() instanceof SpellCastingItem staffItem))
    //        return 0;
    //    int selectedSpell = SpellUtils.getSelectedSpellSlot(stack.getOrCreateTag());
    //    if (staffItem.getCoolDown(selectedSpell, stack) == 0) {
    //        return 0;
    //    }
    //
    //    return 1 - (staffItem.getCoolDown(selectedSpell, stack) / (float) staffItem.getSelectedSpell(stack).getCoolDown());
    //}
    public ResourceLocation getSelectedSpell(LocalPlayer player) {
        if (!(getCastingItem(player).getItem() instanceof SpellCastingItem))
            return new ResourceLocation(IncompCore.MODID, "empty");
        return SpellCastingItem.getSelectedSpell(getCastingItem(player));
    }
    public ItemStack getCastingItem(LocalPlayer player) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof SpellCastingItem)
            return stack;
        return ItemStack.EMPTY;
    }
    
    public float getCastCompletionPercent(LocalPlayer player) {
        if (!(getCastingItem(player).getItem() instanceof SpellCastingItem castingItem))
            return 0;
        if (SpellListener.getSpellProperties(getSelectedSpell(player)).drawTime() == 0) {
            return 0;
        }
        
        return 1 - (castingItem.spellRemainingDrawTime(getCastingItem(player)) / (float) castingItem.getUseDuration(getCastingItem(player)));
    }
    
    public float getCastDuration(LocalPlayer player) {
        if (!(getCastingItem(player).getItem() instanceof SpellCastingItem))
            return 0;
        return SpellListener.getSpellProperties(getSelectedSpell(player)).drawTime();
    }
    
    public ResourceLocation getWielderClassType(LocalPlayer player) {
        return PlayerDataCore.ClassData.getPlayerClassType(player);
    }
    
    public ResourceLocation getSpellOverlayTexture(String spriteName, LocalPlayer player) {
        if (ClassTypeListener.getClassTypeProperties(getWielderClassType(player)) == null)
            return new ResourceLocation(IncompCore.MODID, "spell_list/" + spriteName);
        if (!ClassTypeListener.getClassTypeProperties(getWielderClassType(player)).useClassSpecificTexture())
            return new ResourceLocation(IncompCore.MODID, "spell_list/" + spriteName);
        return new ResourceLocation(getWielderClassType(player).getNamespace(), "spell_list/" + getWielderClassType(player).getPath() + "/" + spriteName);
    }
    
}
