package com.incompetent_modders.incomp_core.data.properties;

import com.google.gson.JsonObject;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ability.ClassAbility;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.ManaRegenCondition;
import com.incompetent_modders.incomp_core.api.player_data.class_type.passive.ClassPassiveEffect;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ClassPropertiesProvider implements DataProvider {
    private final String modid;
    private final PackOutput packOutput;
    public static Map<ClassType, ClassTypeProperties> classTypeProperties = new HashMap<>();
    
    public ClassPropertiesProvider(PackOutput packOutput, String modid) {
        this.modid = modid;
        this.packOutput = packOutput;
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<CompletableFuture<?>> builder = new HashSet<>();
        classTypeProperties.forEach((classType, classTypeProperties) -> {
            builder.add(
                    DataProvider.saveStable(
                            pOutput,
                            this.toJson(classTypeProperties.canCastSpells(), classTypeProperties.maxMana(), classTypeProperties.pacifist(), classTypeProperties.useClassSpecificTexture(), classTypeProperties.manaRegenCondition(), classTypeProperties.passiveEffect(), classTypeProperties.ability(), classTypeProperties.passiveEffectTickFrequency(), classTypeProperties.abilityCooldown()),
                            this.packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(classType.getClassTypeIdentifier().getNamespace()).resolve("spell_properties").resolve("%s.json".formatted(classType.getClassTypeIdentifier().getPath())))
            );
        });
        
        return CompletableFuture.allOf(
                builder.toArray(CompletableFuture[]::new)
        );
    }
    
    @Override
    public String getName() {
        return "Class Definitions: " + modid;
    }
    
    private JsonObject toJson(boolean canCastSpells, int maxMana, boolean pacifist, boolean useClassSpecificTexture, ManaRegenCondition manaRegenCondition, ClassPassiveEffect passiveEffect, ClassAbility ability, int passiveEffectTickFrequency, int abilityCooldown) {
        JsonObject jsonObject = new JsonObject();
        JsonObject properties = new JsonObject();
        properties.addProperty("can_cast_spells", canCastSpells);
        properties.addProperty("max_mana", maxMana);
        properties.addProperty("pacifist", pacifist);
        properties.addProperty("use_class_specific_texture", useClassSpecificTexture);
        JsonObject manaRegenCon = new JsonObject();
        manaRegenCon.addProperty("type", manaRegenCondition.getType().toString());
        jsonObject.addProperty("mana_regen_condition", manaRegenCondition.getType().toString());
        JsonObject passiveEff = new JsonObject();
        passiveEff.addProperty("type", passiveEffect.getType().toString());
        jsonObject.addProperty("passive_effect", passiveEffect.getType().toString());
        JsonObject abil = new JsonObject();
        abil.addProperty("type", ability.getType().toString());
        jsonObject.addProperty("ability", ability.getType().toString());
        properties.addProperty("passive_effect_tick_frequency", passiveEffectTickFrequency);
        properties.addProperty("ability_cooldown", abilityCooldown);
        jsonObject.add("properties", properties);
        return jsonObject;
    }
}

