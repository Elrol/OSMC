package dev.elrol.osmc.data.skill_effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.SkillEffectType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCSkillEffectTypeRegistry;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DamageMitigationSkillEffect extends SkillEffect {

    public static final MapCodec<DamageMitigationSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("damageReductionFormula").forGetter(DamageMitigationSkillEffect::getDamageReductionFormula))
            .and(OSMCConstants.TARGET_DAMAGE_TYPE_CODEC.listOf().fieldOf("damageTypes").forGetter(DamageMitigationSkillEffect::getDamageTypes)
    ).apply(instance, (reqLevel, expGainFormula, damageReductionFormula, damageTypes) -> {
        DamageMitigationSkillEffect data = new DamageMitigationSkillEffect(reqLevel, expGainFormula, damageReductionFormula);
        data.damageTypes.addAll(damageTypes);
        return data;
    }));

    private final String damageReductionFormula;
    private final List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> damageTypes = new ArrayList<>();

    public DamageMitigationSkillEffect(int reqLevel, String expGainFormula, String damageReductionFormula) {
        super(reqLevel, expGainFormula);
        this.damageReductionFormula = damageReductionFormula;
    }

    public void addDamageType(RegistryKey<DamageType> type) {
        damageTypes.add(Either.left(type));
    }

    public void addDamageTag(TagKey<DamageType> tagKey) {
        damageTypes.add(Either.right(tagKey));
    }

    public String getDamageReductionFormula() { return damageReductionFormula; }
    public List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> getDamageTypes() { return damageTypes; }

    public float calculateDamage(int skillLevel, float damage) {
        return (float) MathUtils.calculate(getDamageReductionFormula(), Map.of(
                "level", (double) skillLevel,
                "damage", (double) damage));
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.DAMAGE_MITIGATION_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.DAMAGE_RECEIVED);
    }
}
