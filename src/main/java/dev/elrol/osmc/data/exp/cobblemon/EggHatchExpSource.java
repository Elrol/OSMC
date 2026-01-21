package dev.elrol.osmc.data.exp.cobblemon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;

import java.util.Map;

public class EggHatchExpSource extends ExpSource {

    public static final MapCodec<EggHatchExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(EggHatchExpSource::getExpFormula)
    ).apply(instance, (expGain, expFormula) -> {
        EggHatchExpSource data = new EggHatchExpSource(expGain);
        data.expFormula = expFormula;
        return data;
    }));

    private String expFormula = "xp";

    public EggHatchExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public int calculate() {
        return (int) MathUtils.calculate(getExpFormula(), Map.of("xp", (double) getExpGain()));
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.COBBLEMON_EGG_HATCH_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
