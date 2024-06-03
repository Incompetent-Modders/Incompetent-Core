package com.incompetent_modders.incomp_core.common;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.effect.SpeciesAlteringEffect;
import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeListener;
import com.incompetent_modders.incomp_core.api.json.class_type.ClassTypeProperties;
import com.incompetent_modders.incomp_core.api.json.potion.PotionEffectProperties;
import com.incompetent_modders.incomp_core.api.json.potion.PotionEffectPropertyListener;
import com.incompetent_modders.incomp_core.api.json.species.*;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietListener;
import com.incompetent_modders.incomp_core.api.json.species.diet.DietProperties;
import com.incompetent_modders.incomp_core.api.json.spell.SpellListener;
import com.incompetent_modders.incomp_core.api.network.features.MessageClassTypesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageDietsSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpeciesSync;
import com.incompetent_modders.incomp_core.api.network.features.MessageSpellsSync;
import com.incompetent_modders.incomp_core.api.player.ClassData;
import com.incompetent_modders.incomp_core.api.player.ManaData;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.player.SpeciesData;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.client.ClientClassTypeManager;
import com.incompetent_modders.incomp_core.client.ClientDietManager;
import com.incompetent_modders.incomp_core.client.ClientSpeciesManager;
import com.incompetent_modders.incomp_core.client.ClientSpellManager;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.common.registry.*;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Stream;

import static com.incompetent_modders.incomp_core.api.player.PlayerDataCore.PLAYER_DATA_ID;
import static com.incompetent_modders.incomp_core.common.util.Utils.applyDamage;

@EventBusSubscriber(modid = IncompCore.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonForgeEvents {
    //private static boolean syncingData = false;
    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            //Setting Data :3
            //if (!syncingData) {
            //    syncingData = true;
            CompoundTag playerData = PlayerDataCore.getPlayerData(player);
            if (player.getPersistentData().getCompound(PLAYER_DATA_ID) != playerData) {
                PlayerDataCore.setPlayerData(player, playerData);
            }
            //    syncingData = false;
            //}
            PlayerDataCore.handleClassDataTick(player, event);
            PlayerDataCore.handleSpeciesDataTick(player, event);
            
            ManaData.Set.mana(player, ManaData.Get.mana(player));
            ManaData.Set.maxMana(player, ManaData.Get.maxMana(player));
            
            if (player.getPersistentData().contains(IncompCore.MODID + ":ClassData")) {
                IncompCore.LOGGER.info("{} has old data format for Class Data, removing...", player.getName().getString());
                player.getPersistentData().remove(IncompCore.MODID + ":ClassData");
            }
            if (player.getPersistentData().contains(IncompCore.MODID + ":SpeciesData")) {
                IncompCore.LOGGER.info("{} has old data format for Species Data, removing...", player.getName().getString());
                player.getPersistentData().remove(IncompCore.MODID + ":SpeciesData");
            }
            if (player.getPersistentData().contains(IncompCore.MODID + ":ManaData")) {
                IncompCore.LOGGER.info("{} has old data format for Mana Data, removing...", player.getName().getString());
                player.getPersistentData().remove(IncompCore.MODID + ":ManaData");
            }
            
            
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        Stream<ServerPlayer> relevantPlayers = event.getRelevantPlayers();
        var msgSpellList = new MessageSpellsSync(SpellListener.getAllSpells());
        var msgSpeciesList = new MessageSpeciesSync(SpeciesListener.getAllSpecies());
        var msgClassTypeList = new MessageClassTypesSync(ClassTypeListener.getAllClassTypes());
        var msgDietList = new MessageDietsSync(DietListener.getAllDiets());
        for (ServerPlayer player : relevantPlayers.toList()) {
            if (!new HashSet<>(ClientSpellManager.getInstance().getSpellList()).containsAll(SpellListener.getAllSpells()))
                PacketDistributor.sendToPlayer(player, msgSpellList);
            if (!new HashSet<>(ClientSpeciesManager.getInstance().getSpeciesList()).containsAll(SpeciesListener.getAllSpecies()))
                PacketDistributor.sendToPlayer(player, msgSpeciesList);
            if (!new HashSet<>(ClientClassTypeManager.getInstance().getClassTypeList()).containsAll(ClassTypeListener.getAllClassTypes()))
                PacketDistributor.sendToPlayer(player, msgClassTypeList);
            if (!new HashSet<>(ClientDietManager.getInstance().getDietList()).containsAll(DietListener.getAllDiets()))
                PacketDistributor.sendToPlayer(player, msgDietList);
        }
    }
    
    static AttributeModifier PACIFIST = new AttributeModifier(UUID.fromString("70eeca5e-46ed-4b8a-bf75-f102419395cc"), "Pacifist", 0.25F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            ResourceLocation classType = ClassData.Get.playerClassType(player);
            ClassTypeProperties classTypeProperties = ClassTypeListener.getClassTypeProperties(classType);
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA);
            if (maxMana != null) {
                ManaData.Set.maxMana(player, maxMana.getValue());
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
            ClassData.initialize(player);
            SpeciesData.initialize(player);
        }
    }
    @SubscribeEvent
    public static void serverStarting(ServerStartingEvent event) {
        SpellListener.getAllSpells().forEach(entry -> {
            if (SpellListener.getSpellProperties(entry).isBlankSpell() && entry != CastingItemUtil.emptySpell) {
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
            if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SpellCastingItem && !(stack.getItem() instanceof SpellCastingItem)) {
                ResourceLocation selectedSpell = CastingItemUtil.getSelectedSpell(player.getItemInHand(InteractionHand.MAIN_HAND));
                if (selectedSpell != null) {
                    ItemStack catalyst = SpellListener.getSpellProperties(selectedSpell).catalyst().item();
                    if (catalyst != null && !catalyst.isEmpty()) {
                        if (player.getItemInHand(InteractionHand.OFF_HAND).equals(catalyst) && catalyst.getItem() instanceof BundleItem) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
            ResourceLocation speciesType = SpeciesData.Get.playerSpecies(player);
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
            if (speciesProperties != null) {
                ResourceLocation dietType = speciesProperties.dietType();
                if (stack.getFoodProperties(entity) != null && dietType != Utils.defaultDiet) {
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
    }
    @SubscribeEvent
    public static void onItemFinishUse(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();
        if (entity instanceof Player player) {
            ResourceLocation speciesType = SpeciesData.Get.playerSpecies(player);
            SpeciesProperties speciesProperties = SpeciesListener.getSpeciesTypeProperties(speciesType);
            if (speciesProperties != null) {
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
    }
    
    @SubscribeEvent
    public static void onPotionEffectFinish(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        PotionEffectProperties properties = PotionEffectPropertyListener.getEffectProperties(event.getEffectInstance().getEffect().value());
        if (entity instanceof Player player) {
            if (event.getEffectInstance().getEffect().value() instanceof SpeciesAlteringEffect speciesAlteringEffect) {
                if (speciesAlteringEffect.getConvertTo() != null) {
                    SpeciesData.Set.playerSpecies(player, speciesAlteringEffect.getConvertTo());
                }
            }
            if (properties != null) {
                properties.applySpeciesConvert(player);
                properties.applyClassConvert(player);
            }
        }
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
        int spellsCount = ClientSpellManager.getInstance().getSpellList().size();
        int classTypesCount = ClientClassTypeManager.getInstance().getClassTypeList().size();
        int speciesCount = ClientSpeciesManager.getInstance().getSpeciesList().size();
        int dietsCount = ClientDietManager.getInstance().getDietList().size();
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
    
}
