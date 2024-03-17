package com.incompetent_modders.incomp_core.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.RenderTypeHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public final class DrawingUtils {
    public static final int BLACK = 1;
    
    public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        graphics.blitSprite(sprite, x, y, width, height);
    }
    public static void renderCooldown(GuiGraphics graphics, RenderType renderType, int x, int y, float cooldownPercent, int imageSize) {
        int i1 = y + Mth.floor(imageSize * (1.0F - cooldownPercent));
        int j1 = i1 + Mth.ceil(imageSize * cooldownPercent);
        if (cooldownPercent > 0.0F) {
            graphics.fill(renderType, x, i1, x + imageSize, j1, Integer.MAX_VALUE);
        }
    }
    public static void drawString(GuiGraphics graphics, Font font, Component text, int x, int y, int colour) {
        graphics.drawString(font, text, x, y, colour);
    }
    public static void drawNumberString(GuiGraphics graphics, Font font, int number, int x, int y, int colour) {
        graphics.drawString(font, String.valueOf(number), x, y, colour);
    }
    public static void drawTexturedRect(int x, int y, int width, int height){
        drawTexturedRect(x, y, 0, 0, width, height, width, height);
    }
    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight){
        DrawingUtils.drawTexturedFlippedRect(x, y, u, v, width, height, textureWidth, textureHeight, false, false);
    }
    
    public static void drawTexturedFlippedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY){
        
        float f = 1F / (float)textureWidth;
        float f1 = 1F / (float)textureHeight;
        
        int u1 = flipX ? u + width : u;
        int u2 = flipX ? u : u + width;
        int v1 = flipY ? v + height : v;
        int v2 = flipY ? v : v + height;
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        buffer.vertex(x, y + height, 0).uv(((float)(u1) * f), ((float)(v2) * f1)).endVertex();
        buffer.vertex(x + width, y + height, 0).uv(((float)(u2) * f), ((float)(v2) * f1)).endVertex();
        buffer.vertex(x + width, y, 		  0).uv(((float)(u2) * f), ((float)(v1) * f1)).endVertex();
        buffer.vertex(x, y, 		  0).uv(((float)(u1) * f), ((float)(v1) * f1)).endVertex();
        
        tessellator.end();
    }
    
    public static void drawTexturedStretchedRect(int x, int y, int u, int v, int finalWidth, int finalHeight, int width,
                                                 int height){
        
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        
        buffer.vertex((x), y + finalHeight, 0).uv(u, v + height).endVertex();
        buffer.vertex(x + finalWidth, y + finalHeight, 0).uv(u + width, v + height).endVertex();
        buffer.vertex(x + finalWidth, (y), 0).uv(u + width, v).endVertex();
        buffer.vertex((x), (y), 0).uv(u, v).endVertex();
        
        tessellator.end();
    }
    
    public static void drawGlitchRect(Random random, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY){
        for(int i=0; i<height; i++){
            if(flipY) i = height - i - 1;
            int offset = random.nextInt(4) == 0 ? random.nextInt(6) - 3 : 0;
            drawTexturedFlippedRect(x + offset, y + i, u, v + i, width, 1, textureWidth, textureHeight, flipX, flipY);
        }
    }
    
    public static int mix(int colour1, int colour2, float proportion){
        
        proportion = Mth.clamp(proportion, 0, 1);
        
        int r1 = colour1 >> 16 & 255;
        int g1 = colour1 >> 8 & 255;
        int b1 = colour1 & 255;
        int r2 = colour2 >> 16 & 255;
        int g2 = colour2 >> 8 & 255;
        int b2 = colour2 & 255;
        
        int r = (int)(r1 + (r2-r1) * proportion);
        int g = (int)(g1 + (g2-g1) * proportion);
        int b = (int)(b1 + (b2-b1) * proportion);
        
        return (r << 16) + (g << 8) + b;
    }
    
    public static int makeTranslucent(int colour, float opacity){
        return colour + ((int)(opacity * 0xff) << 24);
    }
}
