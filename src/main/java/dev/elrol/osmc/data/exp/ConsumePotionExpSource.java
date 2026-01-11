package dev.elrol.osmc.data.exp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class ConsumePotionExpSource extends ExpSource {

    public static final MapCodec<ConsumePotionExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(StatusEffect.ENTRY_CODEC.listOf().fieldOf("items").forGetter(ConsumePotionExpSource::getEffects))
            .and(Codec.STRING.fieldOf("formula").forGetter(ConsumePotionExpSource::getFormula)
    ).apply(instance, (expGain, effects, formula) -> {
        ConsumePotionExpSource data = new ConsumePotionExpSource(expGain);
        data.effects.addAll(effects);
        return data;
    }));

    private final List<RegistryEntry<StatusEffect>> effects = new ArrayList<>();
    private final String formula;

    public ConsumePotionExpSource(int expGain) {
        super(expGain);
        formula = "exp + duration + amplifier";
    }

    public ConsumePotionExpSource(int expGain, String formula) {
        super(expGain);
        this.formula = formula;
    }

    public boolean isValid(RegistryEntry<StatusEffect> effect) {
        return getEffects().contains(effect);
    }

    public void addEffect(RegistryEntry<StatusEffect> effect) {
        effects.add(effect);
    }

    public List<RegistryEntry<StatusEffect>> getEffects() { return effects; }

    public String getFormula() { return formula; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.CONSUME_POTION_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
