package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.effect.SpeciesAlteringEffect;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.json.species.*;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietProperties;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectProperties;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.registry.*;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.incompetent_modders.incomp_core.util.CommonUtils.applyDamage;

@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonForgeEvents {
    static float regenInterval = 0;
    static float classAbilityCooldownInterval = 0;
    static float speciesAbilityCooldownInterval = 0;
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ResourceLocation classType = PlayerDataCore.ClassData.getPlayerClassType(player);
            ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
            
            AttributeInstance manaRegen = player.getAttribute(ModAttributes.MANA_REGEN);
            if (PlayerDataCore.ManaData.getMana(player) < PlayerDataCore.ManaData.getMaxMana(player) && manaRegen != null && PlayerDataCore.ClassData.canRegenMana(player)) {
                AtomicReference<Float> mod = new AtomicReference<>(1.0F);
                if (!player.getActiveEffects().isEmpty()) {
                    player.getActiveEffects().forEach(effect -> {
                        PotionEffectProperties properties = PotionEffectPropertyListener.getEffectProperties(effect.getEffect().value());
                        if (properties != null) {
                            if (properties.manaRegenModifier() != 1.0F) {
                                mod.set(properties.manaRegenModifier());
                            }
                        }
                    });
                }
                 regenInterval++;
                if (regenInterval >= (20 / mod.get())) {
                    PlayerDataCore.ManaData.healMana(player, manaRegen.getValue());
                    CommonUtils.onManaHeal(player, manaRegen.getValue());
                    regenInterval = 0;
                }
            }
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
            if (maxMana != null && classTypeProperties != null) {
                PlayerDataCore.ManaData.setMaxMana(player, classTypeProperties.maxMana());
            }
            classAbilityCooldownInterval++;
            if (classAbilityCooldownInterval >= 20) {
                if (PlayerDataCore.ClassData.getAbilityCooldown(player) > 0) {
                    PlayerDataCore.ClassData.setAbilityCooldown(player, PlayerDataCore.ClassData.getAbilityCooldown(player) - 1);
                }
                classAbilityCooldownInterval = 0;
            }
            speciesAbilityCooldownInterval++;
            if (speciesAbilityCooldownInterval >= 20) {
                if (PlayerDataCore.SpeciesData.getAbilityCooldown(player) > 0) {
                    PlayerDataCore.SpeciesData.setAbilityCooldown(player, PlayerDataCore.SpeciesData.getAbilityCooldown(player) - 1);
                }
                speciesAbilityCooldownInterval = 0;
            }
            //Setting Data :3
            PlayerDataCore.setClassData(player, PlayerDataCore.getClassData(player));
            PlayerDataCore.setManaData(player, PlayerDataCore.getManaData(player));
            PlayerDataCore.setSpeciesData(player, PlayerDataCore.getSpeciesData(player));
            
            PlayerDataCore.ManaData.setMana(player, PlayerDataCore.ManaData.getMana(player));
            PlayerDataCore.ManaData.setMaxMana(player, PlayerDataCore.ManaData.getMaxMana(player));
            
            if (player.getPersistentData().contains(IncompCore.MODID + ":data")) {
                IncompCore.LOGGER.info("Player has old data format for ClassType & Mana Data, removing...");
                player.getPersistentData().remove(IncompCore.MODID + ":data");
            }
            PlayerDataCore.ClassData.getPlayerClassType(player);
            if (PlayerDataCore.ClassData.getPlayerClassType(player).equals(new ResourceLocation(IncompCore.MODID, "simple_human"))) {
                IncompCore.LOGGER.info("Player has old ID for default ClassType, setting to new default...");
                PlayerDataCore.ClassData.setPlayerClassType(player, CommonUtils.defaultClass);
            }
            
            if (classTypeProperties != null) {
                PlayerDataCore.ClassData.setPlayerClassType(player, classType);
                PlayerDataCore.ClassData.setCanRegenMana(player, classTypeProperties.canRegenerateMana(player, player.level()));
                PlayerDataCore.ClassData.setPacifist(player, classTypeProperties.pacifist());
                PlayerDataCore.ClassData.setAbility(player, classTypeProperties.ability().getType());
                PlayerDataCore.ClassData.setPassiveEffect(player, classTypeProperties.passiveEffect().getType());
                classTypeProperties.tickClassFeatures(event);
            }
            
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
            SpeciesAttributes speciesAttributes = SpeciesAttributesListener.getSpeciesTypeAttributes(speciesType);
            
            if (speciesProperties != null ) {
                PlayerDataCore.SpeciesData.setSpecies(player, speciesType);
                PlayerDataCore.SpeciesData.setInvertedHealAndHarm(player, speciesProperties.invertHealAndHarm());
                PlayerDataCore.SpeciesData.setKeepOnDeath(player, speciesProperties.keepOnDeath());
                PlayerDataCore.SpeciesData.setDiet(player, speciesProperties.dietType());
                PlayerDataCore.SpeciesData.setBehaviour(player, speciesProperties.behaviour().getType());
                PlayerDataCore.SpeciesData.setAbility(player, speciesProperties.ability().getType());
                speciesProperties.tickSpeciesAttributes(player);
                if (speciesAttributes != null) {
                    PlayerDataCore.SpeciesData.setMaxHealth(player, speciesAttributes.maxHealth());
                    PlayerDataCore.SpeciesData.setAttackDamage(player, speciesAttributes.attackDamage());
                    PlayerDataCore.SpeciesData.setAttackKnockback(player, speciesAttributes.attackKnockback());
                    PlayerDataCore.SpeciesData.setMovementSpeed(player, speciesAttributes.moveSpeed());
                    PlayerDataCore.SpeciesData.setArmor(player, speciesAttributes.armour());
                    PlayerDataCore.SpeciesData.setLuck(player, speciesAttributes.luck());
                    PlayerDataCore.SpeciesData.setBlockInteractionRange(player, speciesAttributes.blockInteractionRange());
                    PlayerDataCore.SpeciesData.setEntityInteractionRange(player, speciesAttributes.entityInteractionRange());
                    PlayerDataCore.SpeciesData.setGravity(player, speciesAttributes.gravity());
                    PlayerDataCore.SpeciesData.setJumpStrength(player, speciesAttributes.jumpStrength());
                    PlayerDataCore.SpeciesData.setKnockbackResistance(player, speciesAttributes.knockbackResistance());
                    PlayerDataCore.SpeciesData.setSafeFallDistance(player, speciesAttributes.safeFallDistance());
                    PlayerDataCore.SpeciesData.setScale(player, speciesAttributes.scale());
                    PlayerDataCore.SpeciesData.setStepHeight(player, speciesAttributes.stepHeight());
                    PlayerDataCore.SpeciesData.setArmourToughness(player, speciesAttributes.armourToughness());
                }
            }
        }
    }
    
    static AttributeModifier PACIFIST = new AttributeModifier(UUID.fromString("70eeca5e-46ed-4b8a-bf75-f102419395cc"), "Pacifist", 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            ResourceLocation classType = PlayerDataCore.ClassData.getPlayerClassType(player);
            ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
            if (maxMana != null) {
                PlayerDataCore.ManaData.setMaxMana(player, maxMana.getValue());
            }
            
            AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (classTypeProperties != null && damage != null) {
                if (classTypeProperties.pacifist() && !damage.hasModifier(PACIFIST)) {
                    damage.addPermanentModifier(PACIFIST);
                }
                if (!classTypeProperties.pacifist()) {
                    damage.removeModifier(PACIFIST);
                }
            }
        }
    }
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        SpellListener.getAllSpells().forEach(entry -> {
            if (!SpellListener.getSpellProperties(entry).isBlankSpell() && entry != CastingItemUtil.emptySpell) {
                IncompCore.LOGGER.warn("Spell {} does not have properties! This may cause issues!", entry);
            }
        });
    }
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        ItemStack weapon = attacker instanceof LivingEntity livingEntity ? livingEntity.getMainHandItem() : ItemStack.EMPTY;
        if (entity instanceof Player player) {
            applyDamage(event, player, weapon);
        }
    }
    
    @SubscribeEvent
    public static void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if (entity instanceof Player player) {
            ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            ResourceLocation dietType = SpeciesListener.getSpeciesTypeProperties(speciesType).dietType();
            if (stack.getFoodProperties(entity) != null && dietType != CommonUtils.defaultDiet) {
                if (DietListener.getDietProperties(dietType) != null) {
                    NonNullList<Ingredient> ableToConsume = DietListener.getDietProperties(dietType).ableToConsume();
                    for (Ingredient ingredient : ableToConsume) {
                        if (!ingredient.test(stack)) {
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
            ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
            ResourceLocation dietType = speciesProperties.dietType();
            DietProperties dietProperties = DietListener.getDietProperties(dietType);
            if (dietProperties != null && dietProperties.ignoreHungerFromFood()) {
                FoodProperties foodProperties = stack.getFoodProperties(entity);
                if (foodProperties != null) {
                    List<FoodProperties.PossibleEffect> effects = foodProperties.effects();
                    for (FoodProperties.PossibleEffect effect : effects) {
                        if (effect.effect().is(MobEffects.HUNGER)) {
                            if (player.hasEffect(MobEffects.HUNGER)) {
                                player.removeEffect(MobEffects.HUNGER);
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
        if (entity instanceof Player player) {
            if (event.getEffectInstance().getEffect().value() instanceof SpeciesAlteringEffect speciesAlteringEffect) {
                ResourceLocation speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
                if (speciesType != null) {
                    if (speciesAlteringEffect.getConvertTo() != null) {
                        PlayerDataCore.SpeciesData.setSpecies(player, speciesAlteringEffect.getConvertTo());
                    }
                }
            }
        }
    }
    
    
}
