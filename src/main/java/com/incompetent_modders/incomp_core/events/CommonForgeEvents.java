package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.network.IncompNetwork;
import com.incompetent_modders.incomp_core.api.network.packets.IncompPlayerDataSyncPacket;
import com.incompetent_modders.incomp_core.api.network.packets.SpellSlotScrollPacket;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.spell.PreCastSpell;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.UUID;

import static com.incompetent_modders.incomp_core.api.player.PlayerDataCore.CLASS_DATA_ID;

@Mod.EventBusSubscriber(modid = IncompCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {
    static int regenInterval = 0;
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer player) {
            IncompNetwork.sendToPlayer(new IncompPlayerDataSyncPacket(player), player);
            ClassType classType = ModRegistries.CLASS_TYPE.get(new ResourceLocation(PlayerDataCore.getClassData(player).getString("classType")));
            if (classType != null) {
                AttributeInstance manaRegen = player.getAttribute(ModAttributes.MANA_REGEN.get());
                    if (PlayerDataCore.ManaData.getMana(player) < PlayerDataCore.ManaData.getMaxMana(player) && manaRegen != null && PlayerDataCore.ClassData.canRegenMana(player)) {
                         regenInterval++;
                        if (regenInterval >= 20) {
                            PlayerDataCore.ManaData.healMana(player, manaRegen.getValue());
                            CommonUtils.onManaHeal(player, manaRegen.getValue());
                            regenInterval = 0;
                        }
                    }
                AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA.get());
                if (maxMana != null) {
                    PlayerDataCore.ManaData.setMaxMana(player, maxMana.getValue());
                }
            }
            //Setting Data :3
            PlayerDataCore.setClassData(player, PlayerDataCore.getClassData(player));
            PlayerDataCore.setManaData(player, PlayerDataCore.getManaData(player));
            
            PlayerDataCore.ManaData.setMana(player, PlayerDataCore.ManaData.getMana(player));
            PlayerDataCore.ManaData.setMaxMana(player, PlayerDataCore.ManaData.getMaxMana(player));
            
            if (player.getPersistentData().contains(IncompCore.MODID + ":data")) {
                IncompCore.LOGGER.info("Player has old data format for ClassType & Mana Data, removing...");
                player.getPersistentData().remove(IncompCore.MODID + ":data");
            }
            if (PlayerDataCore.ClassData.getPlayerClassType(player) == null)
                PlayerDataCore.ClassData.setPlayerClassType(player, ModClassTypes.SIMPLE_HUMAN.get());
            if (classType != null) {
                PlayerDataCore.ClassData.setPlayerClassType(player, classType);
                PlayerDataCore.ClassData.setCanRegenMana(player, classType.canRegenerateMana());
                PlayerDataCore.ClassData.setPacifist(player, classType.isPacifist());
            }
        }
    }
    static AttributeModifier PACIFIST = new AttributeModifier(UUID.fromString("70eeca5e-46ed-4b8a-bf75-f102419395cc"), "Pacifist", 0.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            CompoundTag classData = player.getPersistentData().getCompound(CLASS_DATA_ID);
            ClassType classType = ModRegistries.CLASS_TYPE.get(new ResourceLocation(classData.getString("classType")));
            AttributeInstance maxMana = player.getAttribute(ModAttributes.MAX_MANA.get());
            if (maxMana != null) {
                PlayerDataCore.ManaData.setMaxMana(player, maxMana.getValue());
            }
            
            AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (classType != null && damage != null) {
                if (classType.isPacifist()) {
                    damage.addPermanentModifier(PACIFIST);
                }
            }
        }
    }
    @SubscribeEvent
    public static void levelOpened(PlayerEvent.PlayerLoggedInEvent event) {
        List<LivingEntity> preCastEntities = PreCastSpell.selectedEntities;
        if (preCastEntities.isEmpty())
            return;
        for (LivingEntity entity : preCastEntities) {
            if (entity.hasEffect(ModEffects.ARCANE_SELECTION.get())) {
                if (entity.hasEffect(MobEffects.GLOWING)) {
                    entity.removeEffect(MobEffects.GLOWING);
                }
                entity.removeEffect(ModEffects.ARCANE_SELECTION.get());
            }
        }
    }
    @SubscribeEvent
    public static void levelEmptied(PlayerEvent.PlayerLoggedOutEvent event) {
        Level level = event.getEntity().level();
        if (level instanceof ServerLevel serverLevel) {
            List<ServerPlayer> players = serverLevel.players();
            if (players.isEmpty()) {
                IncompCore.LOGGER.info("Level is empty, clearing all Pre-Cast data.");
                PreCastSpell.selectedEntities.clear();
                PreCastSpell.selectedPositions.clear();
            }
        }
    }
    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        
        if (level.isClientSide()) {
            List<BlockPos> positions = PreCastSpell.selectedPositions;
            if (positions.isEmpty())
                return;
            for (BlockPos pos : positions) {
                ClientUtil.createCubeOutlineParticle(pos, level, ParticleTypes.ELECTRIC_SPARK);
            }
        }
    }
}
