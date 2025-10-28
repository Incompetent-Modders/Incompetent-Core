package com.incompetent_modders.incomp_core.client.gui;

import com.incompetent_modders.incomp_core.IncompClient;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.client.screen.DrawingUtils;
import com.incompetent_modders.incomp_core.common.registry.ModAttachmentTypes;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.def.Spell;
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
        
        if (player == null)
            return;
        if (!player.hasData(ModAttachmentTypes.SELECTED_SPELL))
            return;

        ResourceKey<Spell> selectedSpell = getSelectedSpell();
        if (selectedSpell == null)
            return;
        
        ResourceLocation spellFrameIcon = getSpellOverlayTexture("spell_frame", player);
        ResourceLocation spellSlotFrameIcon = getSpellOverlayTexture("spell_slot_frame", player);
        
        ResourceLocation spellIcon = ResourceLocation.fromNamespaceAndPath(selectedSpell.location().getNamespace(), "textures/incompetent_spells/" + selectedSpell.location().getPath() + ".png");
        Component spellName = Spell.getDisplayName(selectedSpell);
        
        DrawingUtils.blitSprite(graphics, spellFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
        DrawingUtils.blitSpellIcon(graphics, spellIcon, graphics.guiWidth() - (graphics.guiWidth() - 17), graphics.guiHeight() - 37);
        DrawingUtils.blitSprite(graphics, spellSlotFrameIcon, graphics.guiWidth() - (graphics.guiWidth() - 10), graphics.guiHeight() - 52, 48, 48);
    }
    
    public ResourceKey<Spell> getSelectedSpell() {
        return IncompClient.getClientSpellData().spellData().getSelectedSpell();
    }
    
    public ResourceLocation getSpellOverlayTexture(String spriteName, LocalPlayer player) {
        Pair<ResourceKey<ClassType>, ClassType> classType = PlayerDataHelper.getClassTypeWithKey(player);
        if (classType.getSecond().useClassSpecificTexture()) {
            String path = classType.getFirst().location().getPath();
            return ResourceLocation.fromNamespaceAndPath(classType.getFirst().location().getNamespace(), spriteLoc + "/" + path + "/" + spriteName);
        } else {
            return ResourceLocation.fromNamespaceAndPath(IncompCore.MODID, spriteLoc + "/" + spriteName);
        }
    }
    
}
