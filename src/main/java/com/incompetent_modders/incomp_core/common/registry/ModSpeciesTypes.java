package com.incompetent_modders.incomp_core.common.registry;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.UndeadSpeciesBehaviour;
import com.incompetent_modders.incomp_core.common.util.AttributeEntry;
import com.incompetent_modders.incomp_core.core.def.SpeciesType;
import com.incompetent_modders.incomp_core.core.def.params.SpeciesAttributes;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ModSpeciesTypes {

    public static final ResourceKey<SpeciesType> HUMAN = create("human");
    public static final ResourceKey<SpeciesType> ZOMBIE = create("zombie");

    private static ResourceKey<SpeciesType> create(String name) {
        return ResourceKey.create(ModRegistries.Keys.SPECIES_TYPE, IncompCore.makeId(name));
    }

    private static void registerSpecies(BootstrapContext<SpeciesType> context) {
        register(context, HUMAN, SpeciesType.builder(context));
        register(context, ZOMBIE, SpeciesType.builder(context).behaviour(new UndeadSpeciesBehaviour(true)).diet(ModDiets.CARNIVORE).invertPotionHealAndHarm().keepOnDeath(true).addAttributeModifiers(
                SpeciesAttributes.builder()
                        .addAttribute(createAttributeModifier(Attributes.ARMOR, 4.0, AttributeModifier.Operation.ADD_VALUE))
                        .addAttribute(createAttributeModifier(Attributes.ATTACK_DAMAGE, 3.0, AttributeModifier.Operation.ADD_VALUE))
                        .addAttribute(createAttributeModifier(Attributes.MOVEMENT_SPEED, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                        .addAttribute(createAttributeModifier(Attributes.SCALE, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE))
                        .addAttribute(createAttributeModifier(Attributes.MAX_HEALTH, 10.0, AttributeModifier.Operation.ADD_VALUE))
                        .addAttribute(createAttributeModifier(Attributes.ARMOR_TOUGHNESS, 3.0, AttributeModifier.Operation.ADD_VALUE))
        ));
    }

    private static AttributeEntry createAttributeModifier(Holder<Attribute> attributeHolder, double value, AttributeModifier.Operation operation) {
        String name = "species_%s_modifier".formatted(attributeHolder.getKey().location().getPath().replace(".", "_"));
        AttributeModifier attributeModifier = new AttributeModifier(IncompCore.makeId(name), value, operation);
        return AttributeEntry.of(attributeHolder, attributeModifier);
    }

    private static void register(BootstrapContext<SpeciesType> context, ResourceKey<SpeciesType> key, SpeciesType.Builder dietBuilder) {
        context.register(key, dietBuilder.build());
    }

    public static void bootstrap(BootstrapContext<SpeciesType> context) {
        registerSpecies(context);
    }
}
