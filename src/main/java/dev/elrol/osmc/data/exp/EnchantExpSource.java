package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantExpSource extends ExpSource {

    private static final Codec<Either<RegistryEntry<Enchantment>, TagKey<Enchantment>>> TARGET_CODEC = Codec.either(
            Enchantment.ENTRY_CODEC,
            TagKey.codec(RegistryKeys.ENCHANTMENT)
    );

    public static final MapCodec<EnchantExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            Codec.STRING.fieldOf("expFormula").forGetter(EnchantExpSource::getFormula),
            TARGET_CODEC.listOf().fieldOf("targets").forGetter(EnchantExpSource::getTargets)
    ).apply(instance, (expGain, expFormula, targets) -> {
        EnchantExpSource data = new EnchantExpSource(expGain);
        data.expFormula = expFormula;
        data.targets.addAll(targets);
        return data;
    }));

    List<Either<RegistryEntry<Enchantment>, TagKey<Enchantment>>> targets = new ArrayList<>();
    String expFormula = "xp + (level * 2)";

    protected EnchantExpSource(int expGain) {
        super(expGain);
    }

    public double calculate(double level, double enchantPower, double xpSpent) {
        Map<String, Double> variables = new HashMap<>();
        variables.put("level", level);
        variables.put("power", enchantPower);
        variables.put("spent", xpSpent);

        return MathUtils.calculate(getFormula(), variables);
    }

    public String getFormula() { return expFormula; }
    public List<Either<RegistryEntry<Enchantment>, TagKey<Enchantment>>> getTargets() { return targets; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.ENCHANT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
