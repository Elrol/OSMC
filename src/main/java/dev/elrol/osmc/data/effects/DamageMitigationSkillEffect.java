package dev.elrol.osmc.data.effects;

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
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(DamageMitigationSkillEffect::getChanceFormula))
            .and(OSMCConstants.TARGET_DAMAGE_TYPE_CODEC.listOf().fieldOf("damageTypes").forGetter(DamageMitigationSkillEffect::damageTypes)
    ).apply(instance, (reqLevel, expGainFormula, chanceFormula, damageTypes) -> {
        DamageMitigationSkillEffect data = new DamageMitigationSkillEffect(reqLevel, expGainFormula, chanceFormula);
        data.damageTypes.addAll(damageTypes);
        return data;
    }));

    private final String chanceFormula;
    private final List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> damageTypes = new ArrayList<>();

    public DamageMitigationSkillEffect(int reqLevel, String expGainFormula, String chanceFormula) {
        super(reqLevel, expGainFormula);
        this.chanceFormula = chanceFormula;
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<Either<RegistryKey<DamageType>, TagKey<DamageType>>> damageTypes() { return damageTypes; }

    public float calculateDamage(int skillLevel, float damage) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
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
