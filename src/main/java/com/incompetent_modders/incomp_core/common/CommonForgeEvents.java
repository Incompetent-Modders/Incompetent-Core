package com.incompetent_modders.incomp_core.common;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviour;
import com.incompetent_modders.incomp_core.api.player_data.species.behaviour_type.SpeciesBehaviourType;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.core.def.ClassType;
import com.incompetent_modders.incomp_core.core.def.Diet;
import com.incompetent_modders.incomp_core.core.def.PotionProperty;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;
import java.util.function.Predicate;
//import static com.incompetent_modders.incomp_core.common.util.Utils.applyDamage;

@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonForgeEvents {
    //private static boolean syncingData = false;
    
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            PlayerDataHelper.getClassTypeProvider(player).ifPresent((prov) -> {
                if (player.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });
            PlayerDataHelper.getSpeciesTypeProvider(player).ifPresent((prov) -> {
                if (player.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });

            ClassType playerClass = PlayerDataHelper.getClassType(player);
            PlayerDataHelper.setMaxMana(player, playerClass.maxMana());
        }
    }

    @SubscribeEvent
    public static void livingTickEvent(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            PlayerDataHelper.getSpeciesTypeProvider(entity).ifPresent((prov) -> {
                if (entity.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });
            PlayerDataHelper.getSpeciesTypeProvider(entity).ifPresent((prov) -> {
                if (entity.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });

            ClassType entityClass = PlayerDataHelper.getClassType(entity);
            PlayerDataHelper.setMaxMana(entity, entityClass.maxMana());

            int passiveTickFreq = entityClass.passiveEffectTickFrequency();

            if (passiveTickFreq > 0 && entity.tickCount % passiveTickFreq == 0) {
                entityClass.passiveEffect().apply(entity.level(), entity);
            }

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
            PlayerDataHelper.regenerateMana(entity, manaRegenModifier);

            SpeciesBehaviour behaviourType = PlayerDataHelper.getSpeciesType(entity).behaviour();
            behaviourType.apply(entity.level(), entity);

            PlayerDataHelper.getSpeciesType(entity).attributes().attributeModifiers().forEach((attributeModifierEntry) -> {
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
    //@SubscribeEvent(priority = EventPriority.HIGH)
    //public static void onDatapackSync(OnDatapackSyncEvent event) {
    //    Stream<ServerPlayer> relevantPlayers = event.getRelevantPlayers();
    //    FeaturesSyncer.syncAllToAll(relevantPlayers.toList());
    //}
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            PlayerDataHelper.getSpeciesType(player).attributes().attributeModifiers().forEach((attributeModifierEntry) -> {
                Holder<Attribute> attribute = attributeModifierEntry.attributeHolder();
                AttributeModifier modifier = attributeModifierEntry.attributeModifier();
                AttributeInstance instance = player.getAttribute(attribute);
                if (instance != null) {
                    instance.addTransientModifier(modifier);
                }
            });
            PlayerDataHelper.getSpeciesType(player).attributes().attributes().forEach((attributeEntry) -> {
                Holder<Attribute> attribute = attributeEntry.attributeHolder();
                double baseValue = attributeEntry.baseValue();
                AttributeInstance instance = player.getAttribute(attribute);
                if (instance != null) {
                    instance.setBaseValue(baseValue);
                }
            });
        }
    }
    
    @SubscribeEvent
    public static void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        //Diet dietType = PlayerDataHelper.getDiet(entity);
        //Predicate<ItemStack> dietPredicate = s -> s.getFoodProperties(entity) != null && dietType.ableToConsume().stream().noneMatch(ingredient -> ingredient.test(s));
        //if (dietPredicate.test(stack)) {
        //    event.setCanceled(true);
        //}
        if (entity instanceof Player player) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem && !(stack.getItem() instanceof SpellCastingItem)) {
                ResourceKey<Spell> selectedSpell = CastingItemUtil.getSelectedSpell(player.getItemInHand(InteractionHand.MAIN_HAND));
                if (selectedSpell != null) {
                    ItemStack catalyst = CastingItemUtil.getSelectedSpellInstance(stack, player).definition().conditions().catalyst().item();
                    if (catalyst != null && !catalyst.isEmpty()) {
                        if (player.getItemInHand(InteractionHand.OFF_HAND).equals(catalyst) && catalyst.getItem() instanceof BundleItem) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemFinishUse(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if (entity instanceof Player player) {
            if (PlayerDataHelper.getSpeciesType(player) != null) {
                Holder<Diet> dietType = PlayerDataHelper.getSpeciesType(player).dietType();
                if (dietType != null && dietType.value().ignoreHunger()) {
                    FoodProperties foodProperties = stack.getFoodProperties(entity);
                    if (foodProperties != null) {
                        List<FoodProperties.PossibleEffect> effects = foodProperties.effects();
                        for (FoodProperties.PossibleEffect effect : effects) {
                            if (effect.effect().is(MobEffects.HUNGER)) {
                                foodProperties.effects().remove(effect);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onPotionEffectFinish(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        Registry<PotionProperty> registry = entity.level().registryAccess().registryOrThrow(ModRegistries.Keys.POTION_PROPERTY);
        registry.holders().forEach((holder) -> {
            if (holder.value().effect().equals(event.getEffectInstance().getEffect())) {
                if (entity instanceof Player player) {
                    holder.value().applyConverts(player);
                }
            }
        });
    }
    
    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CompoundTag playerNBT = player.getPersistentData();
        if (!playerNBT.contains(IncompCore.MODID + ":welcome_message")) {
            IncompCore.LOGGER.info("First join for player {}", player.getName().getString());
            if (player.isLocalPlayer())
                sendWelcomeMessage(player, false);
            playerNBT.putBoolean(IncompCore.MODID + ":welcome_message", false);
        } else {
            if (playerNBT.getBoolean(IncompCore.MODID + ":welcome_message")) {
                if (player.isLocalPlayer())
                    sendWelcomeMessage(player, true);
            }
        }
        
    }
    
    public static void sendWelcomeMessage(Player player, boolean isToggledOn) {
        RegistryAccess registryAccess = player.level().registryAccess();
        int spellsCount = registryAccess.registryOrThrow(ModRegistries.Keys.SPELL).size();
        int classTypesCount = registryAccess.registryOrThrow(ModRegistries.Keys.CLASS_TYPE).size();
        int speciesCount = registryAccess.registryOrThrow(ModRegistries.Keys.SPECIES_TYPE).size();
        int dietsCount = registryAccess.registryOrThrow(ModRegistries.Keys.DIET).size();
        int allFeaturesCount = spellsCount + classTypesCount + speciesCount + dietsCount;
        Component firstJoinNotification = Component.translatable(welcomeMessage_thanks(allFeaturesCount), allFeaturesCount).withStyle(ClientUtil.styleFromColor(0x418d7e));
        Component spellsNotification = Component.translatable(welcomeMessage_spells(spellsCount), spellsCount).withStyle(ClientUtil.styleFromColor(0x624a95));
        Component classTypesNotification = Component.translatable(welcomeMessage_classTypes(classTypesCount), classTypesCount).withStyle(ClientUtil.styleFromColor(0xe19635));
        Component speciesNotification = Component.translatable(welcomeMessage_species(speciesCount), speciesCount).withStyle(ClientUtil.styleFromColor(0x4998e5));
        Component dietsNotification = Component.translatable(welcomeMessage_diets(dietsCount), dietsCount).withStyle(ClientUtil.styleFromColor(0x478e47));
        player.sendSystemMessage(firstJoinNotification);
        player.sendSystemMessage(spellsNotification);
        player.sendSystemMessage(classTypesNotification);
        player.sendSystemMessage(speciesNotification);
        player.sendSystemMessage(dietsNotification);
        if (isToggledOn) {
            Component command = Component.literal("/i_c toggleWelcomeMessage").withStyle(ClientUtil.styleFromColor(0x71f9f0).withUnderlined(true));
            Component notifier = Component.translatable(IncompCore.MODID + ".welcome_message.enabled").withStyle(ClientUtil.styleFromColor(0x2cc0d3));
            Component toggleNotification = Component.translatable(IncompCore.MODID + ".welcome_message.enabled.toggle", command).withStyle(ClientUtil.styleFromColor(0x4fdce2));
            player.sendSystemMessage(notifier);
            player.sendSystemMessage(toggleNotification);
        }
    }
    public static String welcomeMessage_thanks(int allFeaturesCount) {
        return switch (allFeaturesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.thanks.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.thanks.none";
            default -> IncompCore.MODID + ".welcome_message.thanks.plural";
        };
    }
    public static String welcomeMessage_spells(int spellsCount) {
        return switch (spellsCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.spells.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.spells.none";
            default -> IncompCore.MODID + ".welcome_message.spells.plural";
        };
    }
    public static String welcomeMessage_classTypes(int classTypesCount) {
        return switch (classTypesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.class_types.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.class_types.none";
            default -> IncompCore.MODID + ".welcome_message.class_types.plural";
        };
    }
    public static String welcomeMessage_species(int speciesCount) {
        return switch (speciesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.species.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.species.none";
            default -> IncompCore.MODID + ".welcome_message.species.plural";
        };
    }
    public static String welcomeMessage_diets(int dietsCount) {
        return switch (dietsCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.diets.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.diets.none";
            default -> IncompCore.MODID + ".welcome_message.diets.plural";
        };
    }
    
    //@SubscribeEvent
    //private static void onSetup(FMLCommonSetupEvent event) {
    //    SetupEvent.EVENT.invoke(new SetupEvent(event::enqueueWork));
    //}
}
