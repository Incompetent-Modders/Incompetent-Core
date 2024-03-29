package com.incompetent_modders.incomp_core;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

public class ClientUtil {
    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }
    
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
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
    public static void createCubeOutlineParticle(BlockPos pos, Level level) {
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
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, x, mutable.getY(), mutable.getZ() - 0.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, x, mutable.getY(), mutable.getZ() + 1.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, x, mutable.getY() + 1, mutable.getZ() - 0.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, x, mutable.getY() + 1, mutable.getZ() + 1.01, 0, 0, 0);
            });
            zList.forEach(z -> {
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() - 0.01, mutable.getY(), z, 0, 0, 0);
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() + 1.01, mutable.getY(), z, 0, 0, 0);
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() + 0.01, mutable.getY() + 1, z, 0, 0, 0);
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() + 1.01, mutable.getY() + 1, z, 0, 0, 0);
            });
            yList.forEach(y -> {
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() - 0.01, y + 0.05, mutable.getZ() - 0.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() + 0.99, y + 0.05, mutable.getZ() - 0.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX() + 1.01, y + 0.05, mutable.getZ() + 1.01, 0, 0, 0);
                level.addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, mutable.getX(), y + 0.05, mutable.getZ() + 1.01, 0, 0, 0);
            });
        }
        
    }
}
