package dev.elrol.osmc.data.exp.abstractexps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;

public abstract class ExpSource {

    public static final Codec<ExpSource> CODEC = ExpSourceType.REGISTRY.getCodec()
            .dispatch("type", ExpSource::getType, ExpSourceType::codec);

    public static <T extends ExpSource> RecordCodecBuilder<T, Integer> commonCodec() {
        return Codec.INT.fieldOf("expGain").forGetter(ExpSource::getExpGain);
    }

    protected int expGain;

    protected ExpSource(int expGain) {
        this.expGain = expGain;
    }

    public int getExpGain() { return expGain; }

    public abstract ExpSourceType<?> getType();
    public abstract MapCodec<? extends ExpSource> getCodec();
}
