package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.effect.SpeciesAlteringEffect;
import com.incompetent_modders.incomp_core.api.json.species.DietType;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectProperties;
import com.incompetent_modders.incomp_core.api.player_data.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.json.spell.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.json.spell.SpellPropertyListener;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player_data.species.SpeciesType;
import com.incompetent_modders.incomp_core.data.IncompItemTags;
import com.incompetent_modders.incomp_core.data.IncompSpeciesTags;
import com.incompetent_modders.incomp_core.registry.*;
import com.incompetent_modders.incomp_core.util.CommonUtils;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.incompetent_modders.incomp_core.util.CommonUtils.applyDamage;

@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonForgeEvents {
    static float regenInterval = 0;
    static float abilityCooldownInterval = 0;
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer player) {
            ClassType classType = PlayerDataCore.ClassData.getPlayerClassType(player);
            SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            if (classType != null) {
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
                if (maxMana != null) {
                    PlayerDataCore.ManaData.setMaxMana(player, classType.getMaxMana());
                }
                abilityCooldownInterval++;
                if (abilityCooldownInterval >= 20) {
                    if (PlayerDataCore.ClassData.getAbilityCooldown(player) > 0) {
                        PlayerDataCore.ClassData.setAbilityCooldown(player, PlayerDataCore.ClassData.getAbilityCooldown(player) - 1);
                    }
                    abilityCooldownInterval = 0;
                }
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
            if (PlayerDataCore.ClassData.getPlayerClassType(player) != null) {
                if (PlayerDataCore.ClassData.getPlayerClassType(player).getClassTypeIdentifier().equals(new ResourceLocation(IncompCore.MODID, "simple_human"))) {
                    PlayerDataCore.ClassData.setPlayerClassType(player, ModClassTypes.NONE.get());
                }
            }
            if (PlayerDataCore.ClassData.getPlayerClassType(player) == null)
                PlayerDataCore.ClassData.setPlayerClassType(player, ModClassTypes.NONE.get());
            if (classType != null) {
                PlayerDataCore.ClassData.setPlayerClassType(player, classType);
                PlayerDataCore.ClassData.setCanRegenMana(player, classType.canRegenerateMana(player, player.level()));
                PlayerDataCore.ClassData.setPacifist(player, classType.isPacifist());
                PlayerDataCore.ClassData.setAbilityCooldown(player, classType.abilityCooldown());
                PlayerDataCore.ClassData.setAbility(player, classType.classAbility().getType());
                PlayerDataCore.ClassData.setPassiveEffect(player, classType.classPassiveEffect().getType());
            }
            if (speciesType != null) {
                PlayerDataCore.SpeciesData.setSpecies(player, speciesType);
                PlayerDataCore.SpeciesData.setInvertedHealAndHarm(player, speciesType.isInvertedHealAndHarm());
                PlayerDataCore.SpeciesData.setKeepOnDeath(player, speciesType.keepOnDeath());
                PlayerDataCore.SpeciesData.setDiet(player, speciesType.dietType());
                PlayerDataCore.SpeciesData.setMaxHealth(player, speciesType.maxHealth());
                PlayerDataCore.SpeciesData.setAttackDamage(player, speciesType.attackDamage());
                PlayerDataCore.SpeciesData.setAttackKnockback(player, speciesType.attackKnockback());
                PlayerDataCore.SpeciesData.setMovementSpeed(player, speciesType.movementSpeed());
                PlayerDataCore.SpeciesData.setArmor(player, speciesType.armor());
                PlayerDataCore.SpeciesData.setLuck(player, speciesType.luck());
                PlayerDataCore.SpeciesData.setBlockInteractionRange(player, speciesType.blockInteractionRange());
                PlayerDataCore.SpeciesData.setEntityInteractionRange(player, speciesType.entityInteractionRange());
                PlayerDataCore.SpeciesData.setGravity(player, speciesType.gravity());
                PlayerDataCore.SpeciesData.setJumpStrength(player, speciesType.jumpStrength());
                PlayerDataCore.SpeciesData.setKnockbackResistance(player, speciesType.knockbackResistance());
                PlayerDataCore.SpeciesData.setSafeFallDistance(player, speciesType.safeFallDistance());
                PlayerDataCore.SpeciesData.setScale(player, speciesType.scale());
                PlayerDataCore.SpeciesData.setStepHeight(player, speciesType.stepHeight());
                PlayerDataCore.SpeciesData.setArmourToughness(player, speciesType.armourToughness());
                PlayerDataCore.SpeciesData.setBehaviour(player, speciesType.speciesBehaviour().getType());
            }
        }
    }
    
    static AttributeModifier PACIFIST = new AttributeModifier(UUID.fromString("70eeca5e-46ed-4b8a-bf75-f102419395cc"), "Pacifist", 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            ClassType classType = PlayerDataCore.ClassData.getPlayerClassType(player);
            SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
            if (maxMana != null) {
                PlayerDataCore.ManaData.setMaxMana(player, maxMana.getValue());
            }
            
            AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (classType != null && damage != null) {
                if (classType.isPacifist() && !damage.hasModifier(PACIFIST)) {
                    damage.addPermanentModifier(PACIFIST);
                }
                if (!classType.isPacifist()) {
                    damage.removeModifier(PACIFIST);
                }
            }
            if (speciesType == null) {
                PlayerDataCore.SpeciesData.setSpecies(player, ModSpeciesTypes.HUMAN.get());
            }
        }
    }
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        ModRegistries.SPELL.entrySet().forEach(entry -> {
            if (!SpellPropertyListener.spellHasProperties(entry.getValue())) {
                IncompCore.LOGGER.warn("Spell {} does not have properties! This may cause issues!", entry.getValue().getSpellIdentifier());
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
            applyDamage(event, player, weapon, IncompSpeciesTags.smiteVulnerable, Enchantments.SMITE);
            applyDamage(event, player, weapon, IncompSpeciesTags.arthropod, Enchantments.BANE_OF_ARTHROPODS);
            applyDamage(event, player, weapon, IncompSpeciesTags.impalingVulnerable, Enchantments.IMPALING);
        }
    }
    
    @SubscribeEvent
    public static void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if (entity instanceof Player player) {
            SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            DietType dietType = speciesType.dietType();
            if (stack.getFoodProperties(entity) != null && dietType != DietType.OMNIVORE) {
                TagKey<Item> tag = dietType.getTag();
                if (tag != null) {
                    if (!stack.is(tag)) {
                        event.setCanceled(true);
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
            SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
            if (speciesType.is(IncompSpeciesTags.ignoresHungerFromFood)) {
                if (stack.is(IncompItemTags.givesHunger)) {
                    if (player.hasEffect(MobEffects.HUNGER)) {
                        player.removeEffect(MobEffects.HUNGER);
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
                SpeciesType speciesType = PlayerDataCore.SpeciesData.getSpecies(player);
                if (speciesType != null) {
                    if (speciesAlteringEffect.getConvertTo() != null) {
                        PlayerDataCore.SpeciesData.setSpecies(player, speciesAlteringEffect.getConvertTo());
                    }
                }
            }
        }
    }
    
    
}
