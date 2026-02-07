package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class ConsumePotionExpSource extends ExpSource {

    public static final MapCodec<ConsumePotionExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(OSMCConstants.TARGET_STATUS_EFFECT_CODEC.listOf().fieldOf("effects").forGetter(ConsumePotionExpSource::getEffects))
            .and(Codec.STRING.fieldOf("formula").forGetter(ConsumePotionExpSource::getFormula)
    ).apply(instance, (expGain, effects, formula) -> {
        ConsumePotionExpSource data = new ConsumePotionExpSource(expGain, formula);
        data.effects.addAll(effects);
        return data;
    }));

    private final List<Either<RegistryKey<StatusEffect>, TagKey<StatusEffect>>> effects = new ArrayList<>();
    private final String formula;

    public ConsumePotionExpSource(int expGain) {
        super(expGain);
        formula = "xp + duration + amplifier";
    }

    public ConsumePotionExpSource(int expGain, String formula) {
        super(expGain);
        this.formula = formula;
    }

    public void addEffect(RegistryKey<StatusEffect> effect) {
        effects.add(Either.left(effect));
    }

    public List<Either<RegistryKey<StatusEffect>, TagKey<StatusEffect>>> getEffects() { return effects; }

    public String getFormula() { return formula; }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.CONSUME_POTION_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.CONSUME);
    }
}
