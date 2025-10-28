package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityEntry;
import com.incompetent_modders.incomp_core.api.player_data.class_type.mana_regen_condition.DefaultManaRegenCondition;
import com.incompetent_modders.incomp_core.common.registry.content.abilities.ApplyEffectAbility;
import com.incompetent_modders.incomp_core.common.registry.content.passive_effects.ApplyEffectPassiveEffect;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassApplyEffect;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassSpellCasting;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassTickFrequency;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffects;

public class ModClassTypes {
    public static final ResourceKey<ClassType> NONE = create("none");
    public static final ResourceKey<ClassType> PLAGUE_DOCTOR = create("plague_doctor");

    private static ResourceKey<ClassType> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.CLASS_TYPE, IncompCore.makeId(name));
    }

    private static void registerClasses(BootstrapContext<ClassType> context) {
        register(context, NONE, ClassType.builder());
        register(context, PLAGUE_DOCTOR, ClassType.builder()
                .attributes(
                        new ClassSpellCasting(100, new DefaultManaRegenCondition(),
                                ClassSpellCasting.SpellRestriction.builder()
                                        .allow(IncompCore.makeId("necromancy"))
                                        .whitelist()
                                        .build()),
                        new ClassApplyEffect(MobEffects.NIGHT_VISION, 200, 0)
                ).abilities(
                        new AbilityEntry(new ApplyEffectAbility(MobEffects.DAMAGE_RESISTANCE, 300, 2), IncompCore.makeId("plague_doctor_resistance"),
                                400, 1.05f),
                        new AbilityEntry(new ApplyEffectAbility(MobEffects.DAMAGE_BOOST, 150, 1), IncompCore.makeId("plague_doctor_strength"),
                                300, 1.05f)
                )
        );
    }

    private static void register(BootstrapContext<ClassType> context, ResourceKey<ClassType> key, ClassType.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<ClassType> context) {
        registerClasses(context);
    }
}
