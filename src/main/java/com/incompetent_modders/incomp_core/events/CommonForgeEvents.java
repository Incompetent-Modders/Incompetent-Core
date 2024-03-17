package com.incompetent_modders.incomp_core.events;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.ModRegistries;
import com.incompetent_modders.incomp_core.api.class_type.ClassType;
import com.incompetent_modders.incomp_core.api.mana.IManaCap;
import com.incompetent_modders.incomp_core.api.player.PlayerDataCore;
import com.incompetent_modders.incomp_core.registry.ModAttributes;
import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.registry.ModClassTypes;
import com.incompetent_modders.incomp_core.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
    
    
}
