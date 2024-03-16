package com.incompetent_modders.incomp_core;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientUtil {
    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }
    
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }
    
    public static Vec3 offsetRandomly(Vec3 vec, RandomSource r, float radius) {
        return new Vec3(vec.x + (r.nextFloat() - .5f) * 2 * radius, vec.y + (r.nextFloat() - .5f) * 2 * radius,
                vec.z + (r.nextFloat() - .5f) * 2 * radius);
    }
    
    public static int mixColors(int color1, int color2, float w) {
        int a1 = (color1 >> 24);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = (color2 >> 24);
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        return
                ((int) (a1 + (a2 - a1) * w) << 24) +
                        ((int) (r1 + (r2 - r1) * w) << 16) +
                        ((int) (g1 + (g2 - g1) * w) << 8) +
                        ((int) (b1 + (b2 - b1) * w));
    }
}
