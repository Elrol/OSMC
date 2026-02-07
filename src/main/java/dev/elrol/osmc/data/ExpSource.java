package dev.elrol.osmc.data;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;

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
    public abstract List<SkillTrigger> getTriggers();

    protected Map<String, Double> getVariables() { return Map.of("xp", (double) getExpGain()); }

    public static <T extends ExpSource> Products.P1<RecordCodecBuilder.Mu<T>, Integer> getCommonCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.fieldOf("expGain").forGetter(ExpSource::getExpGain)
        );
    }
}
