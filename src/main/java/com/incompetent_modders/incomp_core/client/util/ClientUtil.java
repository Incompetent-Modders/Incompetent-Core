package com.incompetent_modders.incomp_core.client.util;

import com.incompetent_modders.incomp_core.api.item.SpellCastingItem;
import com.incompetent_modders.incomp_core.api.json.spell.SpellProperties;
import com.incompetent_modders.incomp_core.api.spell.item.CastingItemUtil;
import com.incompetent_modders.incomp_core.client.managers.ClientSpellManager;
import com.incompetent_modders.incomp_core.common.util.Utils;
import com.incompetent_modders.incomp_core.common.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.DoubleStream;

import static com.incompetent_modders.incomp_core.IncompCore.*;
import static java.util.stream.Collectors.toList;

public class ClientUtil {
    public static Style styleFromColor(ChatFormatting color) {
        return Style.EMPTY.applyFormat(color);
    }
    public static Style styleFromColor(int hex) {
        return Style.EMPTY.withColor(hex);
    }
    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting BUNDLE_CONTENTS_FORMAT = ChatFormatting.DARK_PURPLE;
    public static final ChatFormatting ERROR_FORMAT = ChatFormatting.RED;
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    
    private static final Component SELECTED_SPELL_TITLE = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(MODID,"spellcasting.selected_spell"))
    ).withStyle(TITLE_FORMAT);
    private static final Component AVAILABLE_SPELLS_TITLE = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(MODID,"spellcasting.available_spells"))
    ).withStyle(TITLE_FORMAT);
    private static final Component SPELL_INFO_TITLE = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(MODID,"spellcasting.spell_info"))
    ).withStyle(TITLE_FORMAT);
    private static final Component PRECAST_INFO_TITLE = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(MODID,"spellcasting.pre_cast_info"))
    ).withStyle(TITLE_FORMAT);
    public static Minecraft mc()
    {
        return Minecraft.getInstance();
    }
    
    public static Vec3 offsetRandomly(Vec3 vec, RandomSource r, float radius) {
        return new Vec3(vec.x + (r.nextFloat() - .5f) * 2 * radius, vec.y + (r.nextFloat() - .5f) * 2 * radius,
                vec.z + (r.nextFloat() - .5f) * 2 * radius);
    }
    
    public static int mixColors(int color1, int color2, float w) {
        int a1 = (color1 >> 24);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = (color2 >> 24);
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        return
                ((int) (a1 + (a2 - a1) * w) << 24) +
                        ((int) (r1 + (r2 - r1) * w) << 16) +
                        ((int) (g1 + (g2 - g1) * w) << 8) +
                        ((int) (b1 + (b2 - b1) * w));
    }
    
    public static List<Double> generateSequenceDoubleStream(double start, double end, double step) {
        return DoubleStream.iterate(start, d -> d <= end, d -> d + step)
                .boxed()
                .collect(toList());
    }
    public static void createCubeOutlineParticle(BlockPos pos, Level level, ParticleOptions particle) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        if (level.isClientSide()) {
            double minX = mutable.getX();
            double minY = mutable.getY();
            double minZ = mutable.getZ();
            double maxX = mutable.getX() + 1;
            double maxY = mutable.getY() + 1;
            double maxZ = mutable.getZ() + 1;
            List<Double> xList = generateSequenceDoubleStream(minX, maxX, 0.1);
            List<Double> yList = generateSequenceDoubleStream(minY, maxY, 0.1);
            List<Double> zList = generateSequenceDoubleStream(minZ, maxZ, 0.1);
            xList.forEach(x -> {
                level.addParticle(particle, x, mutable.getY(), mutable.getZ() - 0.01, 0, 0, 0);
                level.addParticle(particle, x, mutable.getY(), mutable.getZ() + 1.01, 0, 0, 0);
                level.addParticle(particle, x, mutable.getY() + 1, mutable.getZ() - 0.01, 0, 0, 0);
                level.addParticle(particle, x, mutable.getY() + 1, mutable.getZ() + 1.01, 0, 0, 0);
            });
            zList.forEach(z -> {
                level.addParticle(particle, mutable.getX() - 0.01, mutable.getY(), z, 0, 0, 0);
                level.addParticle(particle, mutable.getX() + 1.01, mutable.getY(), z, 0, 0, 0);
                level.addParticle(particle, mutable.getX() + 0.01, mutable.getY() + 1, z, 0, 0, 0);
                level.addParticle(particle, mutable.getX() + 1.01, mutable.getY() + 1, z, 0, 0, 0);
            });
            yList.forEach(y -> {
                level.addParticle(particle, mutable.getX() - 0.01, y + 0.05, mutable.getZ() - 0.01, 0, 0, 0);
                level.addParticle(particle, mutable.getX() + 0.99, y + 0.05, mutable.getZ() - 0.01, 0, 0, 0);
                level.addParticle(particle, mutable.getX() + 1.01, y + 0.05, mutable.getZ() + 1.01, 0, 0, 0);
                level.addParticle(particle, mutable.getX(), y + 0.05, mutable.getZ() + 1.01, 0, 0, 0);
            });
        }
    }
    private static final Component manaCost = Component.translatable("item." + MODID + ".spellcasting.mana_cost").withStyle(TITLE_FORMAT);
    private static final Component castTime = Component.translatable("item." + MODID + ".spellcasting.cast_time").withStyle(TITLE_FORMAT);
    private static final Component requiredCatalyst = Component.translatable("item." + MODID + ".spellcasting.required_catalyst").withStyle(TITLE_FORMAT);
    private static final Component selectedEntitiesComp = Component.translatable("item." + MODID + ".spellcasting.selected_entities").withStyle(TITLE_FORMAT);
    private static final Component selectedPositionsComp = Component.translatable("item." + MODID + ".spellcasting.selected_positions").withStyle(TITLE_FORMAT);
    public static void createSelectedSpellTooltip(List<Component> tooltip, ItemStack castingStack) {
        ResourceLocation spell = SpellCastingItem.getSelectedSpell(castingStack);
        SpellProperties spellProperties = ClientSpellManager.getInstance().getSpellProperties(spell);
        tooltip.add(SELECTED_SPELL_TITLE);
        tooltip.add(CommonComponents.space().append(ClientSpellManager.getDisplayName(spell)).withStyle(DESCRIPTION_FORMAT).withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.EMPTY);
        Player player = Minecraft.getInstance().player;
        tooltip.add(SPELL_INFO_TITLE);
        tooltip.add(CommonComponents.space().append(manaCost));
        tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(String.valueOf(spellProperties.getManaCost(player))).withStyle(DESCRIPTION_FORMAT));
        tooltip.add(CommonComponents.space().append(castTime));
        tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(Utils.timeFromTicks(spellProperties.drawTime(), 1)).withStyle(DESCRIPTION_FORMAT));
        if (spellProperties.hasSpellCatalyst()) {
            tooltip.add(CommonComponents.space().append(requiredCatalyst));
            tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(spellProperties.catalyst().item().getDisplayName().getString().replace("[", "").replace("]", "")).append(spellProperties.catalyst().item().getCount() > 1 ? " x" + spellProperties.catalyst().item().getCount() : "").withStyle(DESCRIPTION_FORMAT).append(spellProperties.playerIsHoldingSpellCatalyst(player) ? " ✔" : " ✘").withStyle(DESCRIPTION_FORMAT));
            if (spellProperties.catalyst().item().has(DataComponents.BUNDLE_CONTENTS)) {
                BundleContents bundleContents = spellProperties.catalyst().item().get(DataComponents.BUNDLE_CONTENTS);
                if (bundleContents == null)
                    return;
                for (ItemStack content : bundleContents.itemCopyStream().toList()) {
                    tooltip.add(CommonComponents.space().append(CommonComponents.space()).append(CommonComponents.space()).append("> " + content.getDisplayName().getString().replace("[", "").replace("]", "")).withStyle(BUNDLE_CONTENTS_FORMAT).append(content.getCount() > 1 ? " x" + content.getCount() : "").withStyle(BUNDLE_CONTENTS_FORMAT));
                }
            }
        }
        tooltip.add(CommonComponents.EMPTY);
    }
    public static void createAvailableSpellsTooltip(List<Component> tooltip, ItemStack castingStack, SpellCastingItem spellCastingItem) {
        tooltip.add(AVAILABLE_SPELLS_TITLE);
        DataComponentMap componentMap = castingStack.getComponents();
        if (componentMap.get(ModDataComponents.MAX_SPELL_SLOTS.get()) != null) {
            for (int i = 0; i < castingStack.getOrDefault(ModDataComponents.MAX_SPELL_SLOTS, 6); i++) {
                if (i == CastingItemUtil.getSelectedSpellSlot(castingStack)) {
                    continue;
                }
                tooltip.add(CommonComponents.space().append(SpellCastingItem.getSpellNameInSlot(castingStack, i)).withStyle(DESCRIPTION_FORMAT).withStyle(DESCRIPTION_FORMAT));
            }
        }
        
    }
    public static String itemName(ItemStack stack) {
        return stack.getDisplayName().getString().replace("[", "").replace("]", "");
    }
    //public static void createCastingInfoTooltip(List<Component> tooltip, ItemStack castingStack) {
    //    Spell spell = SpellUtils.getSpellCasting(castingStack).getSelectedSpell().getSpell();
    //    //if (spell.getManaCost(caster) > PlayerDataCore.ManaData.getMana(caster)) {
    //    //    tooltip.add(Component.translatable("item." + MODID + ".spellcasting.not_enough_mana").withStyle(ERROR_FORMAT));
    //    //}
    //    //if (spell.hasSpellCatalyst() && !SpellUtils.playerIsHoldingSpellCatalyst(caster, spell)) {
    //    //    tooltip.add(Component.translatable("item." + MODID + ".spellcasting.not_holding_catalyst").withStyle(ERROR_FORMAT));
    //    //}
    //    if (SpellCastingItem.isCoolDown(SpellUtils.getSelectedSpellSlot(tag), castingStack)) {
    //        tooltip.add(Component.translatable("item." + MODID + ".spellcasting.on_cooldown").withStyle(ERROR_FORMAT));
    //    }
    //}
}
