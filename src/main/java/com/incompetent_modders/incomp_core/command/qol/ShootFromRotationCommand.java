package com.incompetent_modders.incomp_core.command.qol;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;

public class ShootFromRotationCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("shoot_from_rotation").requires(s -> s.hasPermission(2)).then(Commands.argument("power", FloatArgumentType.floatArg()).then(Commands.argument("uncertainty", FloatArgumentType.floatArg()).then(Commands.argument("item", ItemArgument.item(context)).executes(arguments -> {
            return shootFromRotation(arguments.getSource(), ItemArgument.getItem(arguments, "item").createItemStack(1, true), FloatArgumentType.getFloat(arguments, "power"), FloatArgumentType.getFloat(arguments, "uncertainty"));
        }))));
    }
    
    private static int shootFromRotation(CommandSourceStack source, ItemStack item, float power, float uncertainty) {
        if (item.getItem() instanceof ProjectileItem projectileItem) {
            Projectile projectile = projectileItem.asProjectile(source.getLevel(), source.getEntity().position().add(0, 2.5, 0), item, source.getEntity().getDirection());
            projectile.shootFromRotation(source.getEntity(), source.getEntity().getXRot(), source.getEntity().getYRot(), 0.0F, power, uncertainty);
            source.getEntity().level().addFreshEntity(projectile);
        }
        return 0;
    }
}
