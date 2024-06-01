package com.incompetent_modders.incomp_core.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CommonProxy {
    public void onWorldLoad()
    {
    }
    public Level getClientWorld()
    {
        return null;
    }
    
    public Player getClientPlayer()
    {
        return null;
    }
}
