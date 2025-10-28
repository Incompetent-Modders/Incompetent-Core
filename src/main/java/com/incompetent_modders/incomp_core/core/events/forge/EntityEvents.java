package com.incompetent_modders.incomp_core.core.events.forge;

import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.species.core.SpeciesType;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.def.PotionProperty;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassSpellCasting;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassTickFrequency;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassTickable;
import com.incompetent_modders.incomp_core.core.def.attributes.species.EntityAttributeAttribute;
import com.incompetent_modders.incomp_core.core.def.attributes.species.TickFrequencyAttribute;
import com.incompetent_modders.incomp_core.core.def.attributes.species.SpeciesTickable;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

public class EntityEvents {

    @SubscribeEvent
    public void livingTickEvent(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            PlayerDataHelper.getSpeciesTypeProvider(entity).ifPresent((prov) -> {
                if (entity.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });
            PlayerDataHelper.getSpeciesTypeProvider(entity).ifPresent((prov) -> {
                if (entity.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });

            ClassType classType = PlayerDataHelper.getClassType(entity);

            ClassTickFrequency classTickFrequency = classType.get(ModClassAttributes.TICK_FREQUENCY.get());
            for (ClassTickable attribute : classType.getOfType(ClassTickable.class)) {
                int tickFrequency = 1;
                if (classTickFrequency != null) {
                    tickFrequency = classTickFrequency.getFrequency(attribute.getType());
                }
                if (tickFrequency > 0 && entity.tickCount % tickFrequency == 0) {
                    attribute.tick(entity.level(), entity);
                }
            }
            ClassSpellCasting classSpellCasting = classType.get(ModClassAttributes.SPELL_CASTING.get());
            if (classSpellCasting != null) {
                PlayerDataHelper.setMaxMana(entity, classSpellCasting.maxMana);

                float manaRegenModifier = 1.0F;
                Registry<PotionProperty> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.POTION_PROPERTY);
                if (!entity.getActiveEffects().isEmpty()) {
                    for (MobEffectInstance effect : entity.getActiveEffects()) {
                        for (Holder<PotionProperty> holder : registry.holders().toList()) {
                            if (holder.value().effect().equals(effect.getEffect())) {
                                manaRegenModifier *= holder.value().manaRegenModifier();
                            }
                        }
                    }
                }
                if (PlayerDataHelper.getMana(entity) > PlayerDataHelper.getMaxMana(entity)) {
                    PlayerDataHelper.setMana(entity, PlayerDataHelper.getMaxMana(entity));
                }
                PlayerDataHelper.regenerateMana(entity, manaRegenModifier, classSpellCasting);
            }







            SpeciesType speciesType = PlayerDataHelper.getSpeciesType(entity);

            TickFrequencyAttribute tickFrequencyAttribute = speciesType.get(ModSpeciesAttributes.TICK_FREQUENCY.get());
            for (SpeciesTickable attribute : speciesType.getOfType(SpeciesTickable.class)) {
                int tickFrequency = 1;
                if (tickFrequencyAttribute != null) {
                    tickFrequency = tickFrequencyAttribute.getFrequency(attribute.getType());
                }
                if (tickFrequency > 0 && entity.tickCount % tickFrequency == 0) {
                    attribute.tick(entity.level(), entity);
                }
            }

            EntityAttributeAttribute entityAttributeAttribute = speciesType.get(ModSpeciesAttributes.ENTITY_ATTRIBUTES.get());
            if (entityAttributeAttribute != null) {
                entityAttributeAttribute.attributeModifiers.forEach((attributeModifierEntry) -> {
                    Holder<Attribute> attribute = attributeModifierEntry.attributeHolder();
                    AttributeModifier modifier = attributeModifierEntry.attributeModifier();
                    AttributeInstance instance = entity.getAttribute(attribute);
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        LootParams lootparams = new LootParams.Builder(serverLevel)
                                .withParameter(LootContextParams.THIS_ENTITY, entity)
                                .withParameter(LootContextParams.ORIGIN, entity.position())
                                .withParameter(LootContextParams.DAMAGE_SOURCE, entity.damageSources().generic())
                                .create(LootContextParamSets.ENTITY);
                        LootContext context = new LootContext.Builder(lootparams).create(Optional.empty());
                        if (instance != null) {
                            if (attributeModifierEntry.shouldApply(context)) {
                                if (!instance.hasModifier(modifier.id()))
                                    instance.addTransientModifier(modifier);
                            } else {
                                instance.removeModifier(modifier);
                            }
                        }
                    }
                });

            }
        }
    }
}
