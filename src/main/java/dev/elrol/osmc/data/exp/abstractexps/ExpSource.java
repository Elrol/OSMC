package dev.elrol.osmc.data.exp.abstractexps;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;

public abstract class ExpSource {

    public static final Codec<ExpSource> CODEC = ExpSourceType.REGISTRY.getCodec()
            .dispatch("type", ExpSource::getType, ExpSourceType::codec);

    protected int expGain;

    protected ExpSource(int expGain) {
        this.expGain = expGain;
    }

    public int getExpGain() { return expGain; }

    public abstract ExpSourceType<?> getType();
    public abstract MapCodec<? extends ExpSource> getCodec();

    public static <T extends ExpSource> Products.P1<RecordCodecBuilder.Mu<T>, Integer> getCommonCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.fieldOf("expGain").forGetter(ExpSource::getExpGain)
        );
    }
}
