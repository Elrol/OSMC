package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ExpGain {

    public static Codec<ExpGain> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("levelRequired").forGetter(ExpGain::getLevelRequired),
            Codec.INT.fieldOf("expGained").forGetter(ExpGain::getExpGained)
    ).apply(instance, ExpGain::new));

    int levelRequired = 0;
    int expGained = 0;

    public ExpGain(int level, int exp) {
        levelRequired = level;
        expGained = exp;
    }

    public int getLevelRequired() { return levelRequired; }

    public int getExpGained() { return expGained; }
}
