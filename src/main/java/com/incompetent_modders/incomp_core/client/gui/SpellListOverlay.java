package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.incompetent_modders.incomp_core.client.player_data.ClientClassData;
import com.incompetent_modders.incomp_core.client.screen.DrawingUtils;
import net.minecraft.client.DeltaTracker;
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
    public static final String spriteLoc = "spell_list";
    static final int CAST_TIME = 20;
    public static ResourceLocation classType;
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
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
        
        ResourceLocation spellFrameIcon = getSpellOverlayTexture("spell_frame");
        ResourceLocation spellSlotFrameIcon = getSpellOverlayTexture("spell_slot_frame");
        
        ResourceLocation spellIcon = ResourceLocation.fromNamespaceAndPath(this.getSelectedSpell(player).getNamespace(), "textures/incompetent_spells/" + this.getSelectedSpell(player).getPath() + ".png");
        Component spellName = ClientSpellManager.getDisplayName(getSelectedSpell(player));
        
        DrawingUtils.blitSprite(graphics, spellFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
        DrawingUtils.blitSpellIcon(graphics, spellIcon, graphics.guiWidth() - (graphics.guiWidth() - 17), graphics.guiHeight() - 37);
        DrawingUtils.blitSprite(graphics, spellSlotFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
    }
    
    public ResourceLocation getSelectedSpell(LocalPlayer player) {
        if (!(getCastingItem(player).getItem() instanceof SpellCastingItem))
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "empty");
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
        if (ClientSpellManager.getSpellProperties(getSelectedSpell(player)).drawTime() == 0) {
            return 0;
        }
        
        return 1 - (castingItem.spellRemainingDrawTime(getCastingItem(player)) / (float) castingItem.getUseDuration(getCastingItem(player)));
    }
    
    public float getCastDuration(LocalPlayer player) {
        if (!(getCastingItem(player).getItem() instanceof SpellCastingItem))
            return 0;
        return ClientSpellManager.getSpellProperties(getSelectedSpell(player)).drawTime();
    }
    
    public ResourceLocation getWielderClassType() {
        return ClientClassData.getInstance().getPlayerClassType();
    }
    
    public ResourceLocation getSpellOverlayTexture(String spriteName) {
        if (ClassTypeListener.getClassTypeProperties(getWielderClassType()) == null)
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "spell_list/" + spriteName);
        if (!ClassTypeListener.getClassTypeProperties(getWielderClassType()).useClassSpecificTexture())
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, "spell_list/" + spriteName);
        return ResourceLocation.fromNamespaceAndPath(getWielderClassType().getNamespace(), "spell_list/" + getWielderClassType().getPath() + "/" + spriteName);
    }
    
}
