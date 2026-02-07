package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantExpSource extends ExpSource {

    public static final MapCodec<EnchantExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(EnchantExpSource::getFormula))
            .and(OSMCConstants.TARGET_ENCHANTMENT_CODEC.listOf().fieldOf("targets").forGetter(EnchantExpSource::getTargets)
    ).apply(instance, (expGain, expFormula, targets) -> {
        EnchantExpSource data = new EnchantExpSource(expGain);
        data.expFormula = expFormula;
        data.targets.addAll(targets);
        return data;
    }));

    List<Either<RegistryKey<Enchantment>, TagKey<Enchantment>>> targets = new ArrayList<>();
    String expFormula = "xp + (level * 2)";

    public EnchantExpSource(int expGain) {
        super(expGain);
    }

    public double calculate(double level, double enchantPower, double xpSpent) {
        Map<String, Double> variables = new HashMap<>();
        variables.put("xp", (double) getExpGain());
        variables.put("level", level);
        variables.put("power", enchantPower);
        variables.put("spent", xpSpent);

        return MathUtils.calculate(getFormula(), variables);
    }

    public String getFormula() { return expFormula; }
    public List<Either<RegistryKey<Enchantment>, TagKey<Enchantment>>> getTargets() { return targets; }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.ENCHANT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    public void addTargetEntry(RegistryKey<Enchantment> target) {
        targets.add(Either.left(target));
    }

    public void addTargetTag(TagKey<Enchantment> target) {
        targets.add(Either.right(target));
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.ENCHANT);
    }
}
