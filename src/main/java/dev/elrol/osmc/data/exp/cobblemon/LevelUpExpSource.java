package dev.elrol.osmc.data.exp.cobblemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;

import java.util.Map;

public class LevelUpExpSource extends ExpSource {

    public static final MapCodec<LevelUpExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(LevelUpExpSource::getExpFormula)
    ).apply(instance, (expGain, expFormula) -> {
        LevelUpExpSource data = new LevelUpExpSource(expGain);
        data.expFormula = expFormula;
        return data;
    }));

    private String expFormula = "xp * p_level";

    public LevelUpExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public int calculate(Pokemon pokemon) {
        return (int) MathUtils.calculate(getExpFormula(), getVariables(), pokemon);
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.COBBLEMON_LEVEL_UP_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
