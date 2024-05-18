package com.incompetent_modders.incomp_core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ClientUtils {
    public static Minecraft mc()
    {
        return Minecraft.getInstance();
    }
    public static TextureAtlasSprite getSprite(ResourceLocation rl)
    {
        return mc().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(rl);
    }
    
    public static Font font()
    {
        return mc().font;
    }
    
    public static float partialTicks()
    {
        return mc().getFrameTime();
    }
    
    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage;
        
        try
        {
            bufferedimage = ImageIO.read(imageStream);
        } finally
        {
            IOUtils.closeQuietly(imageStream);
        }
        
        return bufferedimage;
    }
    
    public static int getDarkenedTextColour(int colour)
    {
        int r = (colour >> 16&255)/4;
        int g = (colour >> 8&255)/4;
        int b = (colour&255)/4;
        return r<<16|g<<8|b;
    }
}
