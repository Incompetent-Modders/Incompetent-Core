package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.common.registry.content.spell_results.AddEffectResult;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.incompetent_modders.incomp_core.core.def.conditions.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;

public class ModSpells {

    public static final ResourceKey<Spell> EMPTY = create("empty");
    public static final ResourceKey<Spell> TEST = create("test");

    private static ResourceKey<Spell> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.SPELL, IncompCore.makeId(name));
    }

    private static void registerSpells(BootstrapContext<Spell> context) {
        register(context, EMPTY, toMsgId(EMPTY), Spell.SpellDefinition.builder(0.0, 0));
        register(context, TEST, toMsgId(TEST),
                Spell.SpellDefinition
                        .builder(1.0, 5)
                        .results(SpellResults.create(new AddEffectResult(MobEffects.DAMAGE_BOOST, 200, 1)))
        );
    }

    private static void register(BootstrapContext<Spell> context, ResourceKey<Spell> key, String msgId, Spell.SpellDefinition.Builder definition) {
        context.register(key, new Spell(Component.translatable(msgId), definition.build()));
    }

    private static void register(BootstrapContext<Spell> context, ResourceKey<Spell> key, String msgId, SpellCategory category, double manaCost, int drawTime, CatalystCondition catalyst, ClassTypeCondition classType, SpeciesTypeCondition speciesType, SpellResults results, SoundEvent castSound) {
        context.register(key, new Spell(Component.translatable(msgId), new Spell.SpellDefinition(category, manaCost, drawTime, results, castSound, Spell.SpellConditions.builder().catalyst(catalyst).classType(classType).speciesType(speciesType).build())));
    }

    private static String toMsgId(ResourceKey<Spell> key) {
        String path = key.location().getPath();
        return "spell." + IncompCore.MODID + "." + path.replace('/', '.');
    }

    public static void bootstrap(BootstrapContext<Spell> context) {
        registerSpells(context);
    }
}
