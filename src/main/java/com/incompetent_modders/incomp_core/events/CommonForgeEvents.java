package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.ClientUtil;
import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.mana.IManaCap;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.api.spell.PreCastSpell;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.registry.ModEffects;
import com.incompetent_modders.incomp_core.util.ClientUtils;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.DoubleStream;

import static com.incompetent_modders.incomp_core.api.player.PlayerDataCore.DATA_ID;

@Mod.EventBusSubscriber(modid = IncompCore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer player) {
            var data = PlayerDataCore.getIncompCoreData(player);
            PlayerDataCore.setIncompCoreData(player, data);
            if (PlayerDataCore.getPlayerClassType(player) == null)
                PlayerDataCore.setPlayerClassType(player, ModClassTypes.SIMPLE_HUMAN.get());
            ClassType classType = ModRegistries.CLASS_TYPE.get(new ResourceLocation(data.getString("class_type")));
            @NotNull LazyOptional<IManaCap> inst = player.getCapability(ModCapabilities.MANA_CAPABILITY);
            if (classType != null) {
                PlayerDataCore.setCanRegenMana(player, classType.canRegenerateMana());
                if (inst.isPresent()) {
                    AttributeInstance manaRegen = player.getAttribute(ModAttributes.MANA_REGEN.get());
                    IManaCap mana = inst.orElseThrow(RuntimeException::new);
                    if (mana.getCurrentMana() < mana.getMaxMana() && manaRegen != null && PlayerDataCore.canRegenMana(player)) {
                        mana.healMana(manaRegen.getValue());
                        CommonUtils.onManaHeal(player, manaRegen.getValue());
                    }
                    PlayerDataCore.setMana(player, mana.getCurrentMana());
                }
            }
            
        }
    }
    static AttributeModifier PACIFIST = new AttributeModifier(UUID.fromString("70eeca5e-46ed-4b8a-bf75-f102419395cc"), "Pacifist", 0.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAdded(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            CompoundTag tag = player.getPersistentData().getCompound(DATA_ID);
            ClassType classType = ModRegistries.CLASS_TYPE.get(new ResourceLocation(tag.getString("class_type")));
            AttributeInstance inst = player.getAttribute(ModAttributes.MAX_MANA.get());
            @NotNull LazyOptional<IManaCap> manaCap = player.getCapability(ModCapabilities.MANA_CAPABILITY);
            if (manaCap.isPresent()) {
                IManaCap mana = manaCap.orElseThrow(RuntimeException::new);
                boolean hasClassMana = classType != null && inst != null && mana.getMaxMana() == classType.getMaxMana();
                if (inst != null && classType != null) {
                    if (!hasClassMana)
                        inst.setBaseValue(classType.getMaxMana());
                }
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
                ClientUtil.createCubeOutlineParticle(pos, level);
                //BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
                //double minX = Math.min(mutable.getX(), mutable.getX() + 1);
                //double minY = Math.min(mutable.getY(), mutable.getY() + 1);
                //double minZ = Math.min(mutable.getZ(), mutable.getZ() + 1);
                //double maxX = Math.max(mutable.getX(), mutable.getX() + 1);
                //double maxY = Math.max(mutable.getY(), mutable.getY() + 1);
                //double maxZ = Math.max(mutable.getZ(), mutable.getZ() + 1);
                //List<Double> xList = ClientUtil.generateSequenceDoubleStream(minX, maxX, 0.1);
                //List<Double> yList = ClientUtil.generateSequenceDoubleStream(minY, maxY, 0.1);
                //List<Double> zList = ClientUtil.generateSequenceDoubleStream(minZ, maxZ, 0.1);
                //xList.forEach(x -> yList.forEach(y -> zList.forEach(z -> {
                //    if (Objects.equals(x, y) || Objects.equals(x, z) || Objects.equals(y, z))
                //        return;
                //    level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
                //})));
                //for (double x = minX; x <= maxX; x++) {
                //    for (double y = minY; y <= maxY; y++) {
                //        for (double z = minZ; z <= maxZ; z++) {
                //            int components = 0;
                //            if (x == minX || x == maxX) components++;
                //            if (y == minY || y == maxY) components++;
                //            if (z == minZ || z == maxZ) components++;
                //            if (components >= 3) {
                //                level.addParticle(ParticleTypes.ELECTRIC_SPARK, x, y, z, 0, 0, 0);
                //            }
                //        }
                //    }
                //}
                
                
            }
        }
    }
}
