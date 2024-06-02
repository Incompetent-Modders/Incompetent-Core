package com.incompetent_modders.incomp_core.client;

import com.incompetent_modders.incomp_core.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import static com.incompetent_modders.incomp_core.client.util.ClientUtil.mc;

public class ClientProxy extends CommonProxy {
    
    public static void modConstruction()
    {
        if(Minecraft.getInstance()!=null)
            initWithMC();
    }
    
    public static void initWithMC()
    {
        ClientSpellManager.getInstance();
        ClientSpeciesManager.getInstance();
        ClientClassTypeManager.getInstance();
        ClientDietManager.getInstance();
        ClientEventHandler handler = new ClientEventHandler();
        NeoForge.EVENT_BUS.register(handler);
    }
    
    @Override
    public Level getClientWorld()
    {
        return mc().level;
    }
    
    @Override
    public Player getClientPlayer()
    {
        return mc().player;
    }
}
