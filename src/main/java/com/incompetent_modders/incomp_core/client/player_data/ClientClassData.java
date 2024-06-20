package com.incompetent_modders.incomp_core.client.player_data;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.AbilityType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffectType;
import net.minecraft.resources.ResourceLocation;

public class ClientClassData {
    private static ClientClassData instance;
    public static final String PLAYER_DATA_ID = IncompCore.MODID + ":PlayerData";
    private ResourceLocation playerClassType;
    private boolean canRegenMana;
    private boolean isPacifist;
    private int abilityCooldown;
    private ClassPassiveEffectType<?> passiveEffect;
    private AbilityType<?> ability;
    
    private ClientClassData() {
        IncompCore.LOGGER.info("[CLIENT ONLY] Initialized client class data");
    }
    
    public static ClientClassData getInstance() {
        if (instance == null) {
            instance = new ClientClassData();
        }
        return instance;
    }
    
    public ResourceLocation getPlayerClassType() {
        return playerClassType;
    }
    public void setPlayerClassType(ResourceLocation playerClassType) {
        this.playerClassType = playerClassType;
    }
    
    public boolean canRegenMana() {
        return canRegenMana;
    }
    public void setCanRegenMana(boolean canRegenMana) {
        this.canRegenMana = canRegenMana;
    }
    
    public boolean isPacifist() {
        return isPacifist;
    }
    public void setIsPacifist(boolean isPacifist) {
        this.isPacifist = isPacifist;
    }
    
    public int abilityCooldown() {
        return abilityCooldown;
    }
    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }
    
    public ClassPassiveEffectType<?> passiveEffect() {
        return passiveEffect;
    }
    public void setPassiveEffect(ClassPassiveEffectType<?> passiveEffect) {
        this.passiveEffect = passiveEffect;
    }
    
    public AbilityType<?> ability() {
        return ability;
    }
    public void setAbility(AbilityType<?> ability) {
        this.ability = ability;
    }
}
