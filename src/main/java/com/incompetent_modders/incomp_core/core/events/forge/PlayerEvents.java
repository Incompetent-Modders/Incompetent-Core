package com.incompetent_modders.incomp_core.core.events.forge;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.player.SpellData;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.client.util.ClientUtil;
import com.incompetent_modders.incomp_core.common.registry.ModAttachmentTypes;
import com.incompetent_modders.incomp_core.common.registry.ModClassAttributes;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import com.incompetent_modders.incomp_core.common.registry.ModSpeciesAttributes;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.api.class_type.core.ClassType;
import com.incompetent_modders.incomp_core.core.def.PotionProperty;
import com.incompetent_modders.incomp_core.core.def.Spell;
import com.incompetent_modders.incomp_core.core.def.attributes.class_type.ClassSpellCasting;
import com.incompetent_modders.incomp_core.core.def.attributes.species.EntityAttributeAttribute;
import com.incompetent_modders.incomp_core.core.player.helper.PlayerDataHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            EntityAttributeAttribute entityAttributeAttribute = PlayerDataHelper.getSpeciesType(player).get(ModSpeciesAttributes.ENTITY_ATTRIBUTES.get());
            if (entityAttributeAttribute != null) {
                entityAttributeAttribute.attributeModifiers.forEach((attributeModifierEntry) -> {
                    Holder<Attribute> attribute = attributeModifierEntry.attributeHolder();
                    AttributeModifier modifier = attributeModifierEntry.attributeModifier();
                    AttributeInstance instance = player.getAttribute(attribute);
                    if (instance != null) {
                        instance.addTransientModifier(modifier);
                    }
                });
                entityAttributeAttribute.baseAttributes.forEach((attributeEntry) -> {
                    Holder<Attribute> attribute = attributeEntry.attributeHolder();
                    double baseValue = attributeEntry.baseValue();
                    AttributeInstance instance = player.getAttribute(attribute);
                    if (instance != null) {
                        instance.setBaseValue(baseValue);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            PlayerDataHelper.getClassTypeProvider(player).ifPresent((prov) -> {
                if (player.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });
            PlayerDataHelper.getSpeciesTypeProvider(player).ifPresent((prov) -> {
                if (player.level().getGameTime() % 20 == 0)
                    prov.decreaseAbilityCooldown();
            });

            ClassType classType = PlayerDataHelper.getClassType(player);

            ClassSpellCasting classSpellCasting = classType.get(ModClassAttributes.SPELL_CASTING.get());
            if (classSpellCasting != null) {
                PlayerDataHelper.setMaxMana(player, classSpellCasting.maxMana);
            }

            if (player.getMainHandItem().has(ModDataComponents.SPELLS)) {
                SpellData spellData = player.getData(ModAttachmentTypes.SELECTED_SPELL);
                ResourceKey<Spell> selectedSpellKey = CastingItemUtil.getSelectedSpell(player.getMainHandItem());
                if (spellData.getSelectedSpell() != selectedSpellKey) {
                    Utils.setSelectedSpell(player, selectedSpellKey);
                }
            } else {
                Utils.clearSelectedSpell(player);
            }
            if (player.getMainHandItem().isEmpty() || (player.hasData(ModAttachmentTypes.SELECTED_SPELL) && player.getData(ModAttachmentTypes.SELECTED_SPELL).selectedSpell().isEmpty())) {
                Utils.clearSelectedSpell(player);
            }
        }
    }

    @SubscribeEvent
    public void onPotionEffectFinish(MobEffectEvent.Expired event) {
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
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
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

    public void sendWelcomeMessage(Player player, boolean isToggledOn) {
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
    public String welcomeMessage_thanks(int allFeaturesCount) {
        return switch (allFeaturesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.thanks.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.thanks.none";
            default -> IncompCore.MODID + ".welcome_message.thanks.plural";
        };
    }
    public String welcomeMessage_spells(int spellsCount) {
        return switch (spellsCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.spells.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.spells.none";
            default -> IncompCore.MODID + ".welcome_message.spells.plural";
        };
    }
    public String welcomeMessage_classTypes(int classTypesCount) {
        return switch (classTypesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.class_types.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.class_types.none";
            default -> IncompCore.MODID + ".welcome_message.class_types.plural";
        };
    }
    public String welcomeMessage_species(int speciesCount) {
        return switch (speciesCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.species.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.species.none";
            default -> IncompCore.MODID + ".welcome_message.species.plural";
        };
    }
    public String welcomeMessage_diets(int dietsCount) {
        return switch (dietsCount) {
            case 1 -> IncompCore.MODID + ".welcome_message.diets.singular";
            case 0 -> IncompCore.MODID + ".welcome_message.diets.none";
            default -> IncompCore.MODID + ".welcome_message.diets.plural";
        };
    }
}
