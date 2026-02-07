package dev.elrol.osmc.data;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.libs.MathUtils;

import java.util.List;
import java.util.Map;

public abstract class SkillEffect {

    public static final Codec<SkillEffect> CODEC = SkillEffectType.REGISTRY.getCodec()
            .dispatch("type", SkillEffect::getType, SkillEffectType::codec);

    private final int reqLevel;
    private final String expGainFormula;

    protected SkillEffect(int reqLevel, String expGainFormula) {
        this.reqLevel = reqLevel;
        this.expGainFormula = expGainFormula;
    }

    protected SkillEffect(String expGainFormula) {
        this.reqLevel = 0;
        this.expGainFormula = expGainFormula;
    }

    public int getReqLevel() { return reqLevel; }
    public String getExpGainFormula() { return expGainFormula; }
    public abstract SkillEffectType<?> getType();
    public abstract MapCodec<? extends SkillEffect> getCodec();
    public abstract List<SkillTrigger> getTriggers();

    public double calculateExp(int level, int extra) {
        return MathUtils.calculate(getExpGainFormula(), Map.of(
                "level", (double) level,
                "extra", (double) extra
        ));
    }

    public static <T extends SkillEffect> Products.P2<RecordCodecBuilder.Mu<T>, Integer, String> getCommonCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.fieldOf("reqLevel").forGetter(SkillEffect::getReqLevel),
                Codec.STRING.fieldOf("expGainFormula").forGetter(SkillEffect::getExpGainFormula));
    }

}
