package com.incompetent_modders.incomp_core.api.spell;

import com.incompetent_modders.incomp_core.registry.ModCapabilities;
import com.incompetent_modders.incomp_core.ModRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class SpellUtils {
    public static void giveItems(Level level, Player player, int amount, Item item) {
        ItemStack stack = new ItemStack(item);
        stack.setCount(amount);
        if (!level.isClientSide) {
            player.getInventory().add(stack);
        }
    }
    public static boolean isInventoryFull(Player player) {
        return player.getInventory().getFreeSlot() == -1;
    }
    
    public static void removeMana(Player player, int amount) {
        ModCapabilities.getMana(player).ifPresent(cap -> cap.removeMana(amount));
    }
    public static HitResult genericSpellRayTrace(Player playerCaster) {
        return SpellUtils.rayTrace(playerCaster, 160 + playerCaster.getEntityReach(), 0, true);
    }
    
    public static Spell deserializeFromSlot(CompoundTag tag, int slot) {
        if (tag == null) {
            return Spells.EMPTY.get();
        }
        if (tag.contains("spellSlot_" + slot)) {
            //The NBT formats the spells as modid:spellname. We need to separate them into two strings.
            String spellModid = tag.getString("spellSlot_" + slot).split(":")[0];
            String spellName = tag.getString("spellSlot_" + slot).split(":")[1];
            return ModRegistries.SPELL.get(new ResourceLocation(spellModid, spellName));
        }
        return Spells.EMPTY.get();
        
    }
    public static void serializeToSlot(CompoundTag tag, int slot, Spell spell) {
        if (tag == null) {
            return;
        }
        tag.putString("spellSlot_" + slot, spell.getSpellIdentifier().toString());
    }
    
    public static int getSelectedSpellSlot(CompoundTag tag) {
        if (tag.contains("selectedSpellSlot")) {
            return tag.getInt("selectedSpellSlot");
        }
        return 0;
    }
    public static boolean isPreCasting(CompoundTag tag) {
        return tag.contains("preCasting") && tag.getBoolean("preCasting");
    }
    public static void setPreCasting(CompoundTag tag, boolean preCasting) {
        tag.putBoolean("preCasting", preCasting);
    }
    public static @Nullable EntityHitResult traceEntities(Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter, double distance) {
        Level world = shooter.level();
        double d0 = distance;
        Entity entity = null;
        Vec3 vec3d = null;
        
        for (Entity entity1 : world.getEntities(shooter, boundingBox, filter)) {
            AABB axisalignedbb = entity1.getBoundingBox().inflate(entity1.getPickRadius());
            Optional<Vec3> optional = axisalignedbb.clip(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vec3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3 vec3d1 = optional.get();
                double d1 = startVec.distanceToSqr(vec3d1);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == shooter.getRootVehicle() && !entity1.canRiderInteract()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            vec3d = vec3d1;
                        }
                    } else {
                        entity = entity1;
                        vec3d = vec3d1;
                        d0 = d1;
                    }
                }
            }
        }
        
        return entity == null ? null : new EntityHitResult(entity, vec3d);
    }
    public static @Nullable EntityHitResult getLookedAtEntity(Entity entity, int range) {
        Vec3 vec3d = entity.getEyePosition(1.0f);
        Vec3 vec3d1 = entity.getViewVector(1.0F);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * range, vec3d1.y * range, vec3d1.z * range);
        AABB axisalignedbb = entity.getBoundingBox().expandTowards(vec3d1.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        return traceEntities(entity, vec3d, vec3d2, axisalignedbb, (e) -> !e.isSpectator() && e.isPickable(), range);
    }
    public static HitResult rayTrace(Entity entity, double length, float lookOffset, boolean hitLiquids) {
        HitResult result = entity.pick(length, lookOffset, hitLiquids);
        EntityHitResult entityLookedAt = getLookedAtEntity(entity, 25);
        return entityLookedAt == null ? result : entityLookedAt;
    }
}
