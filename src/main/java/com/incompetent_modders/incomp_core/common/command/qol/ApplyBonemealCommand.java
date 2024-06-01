package com.incompetent_modders.incomp_core.common.command.qol;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ApplyBonemealCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("apply_bonemeal").requires(s -> s.hasPermission(2)).then(Commands.argument("location", Vec3Argument.vec3()).executes(arguments -> {
            return applyBonemeal(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"));
        }).then(Commands.argument("radius", IntegerArgumentType.integer(1, 50)).executes(arguments -> {
            return applyBonemeal(arguments.getSource(), Vec3Argument.getCoordinates(arguments, "location"), IntegerArgumentType.getInteger(arguments, "radius"));
        })));
    }
    
    
    private static int applyBonemeal(CommandSourceStack source, Coordinates coordinates) {
        Level level = source.getLevel();
        BlockPos blockPos = coordinates.getBlockPos(source).offset(0, -1, 0);
        BlockState blockstate = source.getLevel().getBlockState(blockPos);
        Block var7 = blockstate.getBlock();
            if (var7 instanceof BonemealableBlock bonemealableblock) {
                if (bonemealableblock.isValidBonemealTarget(level, blockPos, blockstate)) {
                    if (level instanceof ServerLevel serverLevel) {
                        if (bonemealableblock.isBonemealSuccess(serverLevel, level.random, blockPos, blockstate)) {
                            bonemealableblock.performBonemeal(serverLevel, level.random, blockPos, blockstate);
                        }
                    }
                }
            }
            return 0;
    }
    
    private static int applyBonemeal(CommandSourceStack source, Coordinates coordinates, int radius) {
        Level level = source.getLevel();
        BlockPos blockPos = coordinates.getBlockPos(source);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = blockPos.offset(x, -1, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof BonemealableBlock bonemealableblock) {
                        if (bonemealableblock.isValidBonemealTarget(level, pos, state)) {
                            if (level instanceof ServerLevel serverLevel) {
                                if (bonemealableblock.isBonemealSuccess(serverLevel, level.random, pos, state)) {
                                    bonemealableblock.performBonemeal(serverLevel, level.random, pos, state);
                                }
                            }
                        }
                    }
            }
        }
        return 0;
    }
}
