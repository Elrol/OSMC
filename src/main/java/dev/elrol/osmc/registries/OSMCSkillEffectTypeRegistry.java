package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.SkillEffectType;
import dev.elrol.osmc.data.effects.BlockDropMultiplierSkillEffect;
import dev.elrol.osmc.data.effects.DamageMitigationSkillEffect;
import dev.elrol.osmc.data.effects.MobDropMultiplierSkillEffect;
import dev.elrol.osmc.data.effects.StatModifierSkillEffect;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;

public class OSMCSkillEffectTypeRegistry {

    public static final SkillEffectType<BlockDropMultiplierSkillEffect> BLOCK_DROP_MULTIPLIER_SKILL_EFFECT =            register(OSMCConstants.BLOCK_DROP_MULTIPLIER_EFFECT_ID,     new SkillEffectType<>(BlockDropMultiplierSkillEffect.CODEC));
    public static final SkillEffectType<MobDropMultiplierSkillEffect> MOB_DROP_MULTIPLIER_SKILL_EFFECT =                register(OSMCConstants.MOB_DROP_MULTIPLIER_EFFECT_ID,       new SkillEffectType<>(MobDropMultiplierSkillEffect.CODEC));
    public static final SkillEffectType<DamageMitigationSkillEffect> DAMAGE_MITIGATION_SKILL_EFFECT =                   register(OSMCConstants.DAMAGE_MITIGATION_EFFECT_ID,         new SkillEffectType<>(DamageMitigationSkillEffect.CODEC));
    public static final SkillEffectType<StatModifierSkillEffect> STAT_MODIFIER_SKILL_EFFECT =                           register(OSMCConstants.STAT_MODIFIER_EFFECT_ID,             new SkillEffectType<>(StatModifierSkillEffect.CODEC));

    public static void init() {}

    public static <T extends SkillEffect> SkillEffectType<T> register(String id, SkillEffectType<T> type) {
        return Registry.register(SkillEffectType.REGISTRY, OSMCConstants.osmcID(id), type);
    }

}
