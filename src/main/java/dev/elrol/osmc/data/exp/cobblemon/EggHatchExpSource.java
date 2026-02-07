package dev.elrol.osmc.data.exp.cobblemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;

import java.util.List;

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

    public int calculate(Pokemon pokemon) {
        return (int) MathUtils.calculate(getExpFormula(), getVariables(), pokemon);
    }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.COBBLEMON_EGG_HATCH_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.EGG_HATCH);
    }
}
