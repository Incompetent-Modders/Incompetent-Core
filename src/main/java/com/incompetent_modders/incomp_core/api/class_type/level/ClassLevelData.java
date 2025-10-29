package com.incompetent_modders.incomp_core.api.class_type.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public record ClassLevelData(int level, int totalExp, float levelProgress) {
    public static final ClassLevelData EMPTY_DATA = new ClassLevelData(0, 0, 0.0F);

    public static final Codec<ClassLevelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("level").forGetter(ClassLevelData::level),
            Codec.INT.fieldOf("totalExp").forGetter(ClassLevelData::totalExp),
            Codec.FLOAT.fieldOf("levelProgress").forGetter(ClassLevelData::levelProgress)
    ).apply(instance, ClassLevelData::new));

    public static final StreamCodec<ByteBuf, ClassLevelData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClassLevelData::level,
            ByteBufCodecs.INT, ClassLevelData::totalExp,
            ByteBufCodecs.FLOAT, ClassLevelData::levelProgress,
            ClassLevelData::new
    );


    public ClassLevelData giveExperiencePoints(int xpPoints) {
        AtomicInteger level = new AtomicInteger(this.level);
        AtomicInteger totalExp = new AtomicInteger(this.totalExp);
        AtomicReference<Float> levelProgress = new AtomicReference<>(this.levelProgress);
        levelProgress.set(this.levelProgress + (float)xpPoints / (float)this.getXpNeededForNextLevel(level.get()));
        totalExp.set(Mth.clamp(totalExp.get() + xpPoints, 0, Integer.MAX_VALUE));

        while (levelProgress.get() < 0.0F) {
            float prog = levelProgress.get() * (float)this.getXpNeededForNextLevel(level.get());
            if (level.get() > 0) {
                this.giveExperienceLevels(-1, level.get(), level::set, totalExp::set, levelProgress::set);
                levelProgress.set(1.0F + prog / (float)this.getXpNeededForNextLevel(level.get()));
            } else {
                this.giveExperienceLevels(-1, level.get(), level::set, totalExp::set, levelProgress::set);
                levelProgress.set(0.0F);
            }
        }

        while (levelProgress.get() >= 1.0F) {
            levelProgress.set((levelProgress.get() - 1.0F) * (float)this.getXpNeededForNextLevel(level.get()));
            this.giveExperienceLevels(1, level.get(), level::set, totalExp::set, levelProgress::set);
            levelProgress.set(levelProgress.get() / (float)this.getXpNeededForNextLevel(level.get()));
        }
        return new ClassLevelData(level.get(), totalExp.get(), levelProgress.get());
    }

    public void giveExperienceLevels(int levels, int currentLevel, Consumer<Integer> levelConsumer, Consumer<Integer> totalConsumer, Consumer<Float> progressConsumer) {
        levelConsumer.accept(currentLevel + levels);
        if (currentLevel < 0) {
            levelConsumer.accept(0);
            progressConsumer.accept(0.0F);
            totalConsumer.accept(0);
        }
    }

    public int getXpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 8 : 7 + level * 4;
        }
    }
}
