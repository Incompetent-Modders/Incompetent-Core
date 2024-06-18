package com.incompetent_modders.incomp_core.client.screen;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Random;

public final class DrawingUtils {
    public static final int BLACK = 1;
    
    public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        graphics.blitSprite(sprite, x, y, width, height);
    }
    public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
        graphics.blitSprite(sprite, x, y, width, height, u, v, uWidth, vHeight);
    }
    public static void blitSpellIcon(GuiGraphics graphics, ResourceLocation sprite, int x, int y) {
        graphics.blit(sprite, x, y, 0, 0, 26, 26, 26, 26);
    }
}
