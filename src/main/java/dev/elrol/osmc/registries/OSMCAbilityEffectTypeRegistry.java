package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.data.ability_effects.*;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;

public class OSMCAbilityEffectTypeRegistry {

    public static final AbilityEffectType<DurationAbilityEffect> DURATION_ABILITY_EFFECT = register(OSMCConstants.DURATION_ABILITY_EFFECT_ID, new AbilityEffectType<>(DurationAbilityEffect.CODEC));
    public static final AbilityEffectType<CooldownAbilityEffect> COOLDOWN_ABILITY_EFFECT = register(OSMCConstants.COOLDOWN_ABILITY_EFFECT_ID, new AbilityEffectType<>(CooldownAbilityEffect.CODEC));
    public static final AbilityEffectType<ShapeBreakAbilityEffect> SHAPE_BREAK_ABILITY_EFFECT = register(OSMCConstants.SHAPE_BREAK_ABILITY_EFFECT_ID, new AbilityEffectType<>(ShapeBreakAbilityEffect.CODEC));
    public static final AbilityEffectType<ChainBreakAbilityEffect> CHAIN_BREAK_ABILITY_EFFECT = register(OSMCConstants.CHAIN_BREAK_ABILITY_EFFECT_ID, new AbilityEffectType<>(ChainBreakAbilityEffect.CODEC));
    public static final AbilityEffectType<ParticleAbilityEffect> PARTICLE_ABILITY_EFFECT = register(OSMCConstants.PARTICLE_ABILITY_EFFECT_ID, new AbilityEffectType<>(ParticleAbilityEffect.CODEC));
    public static final AbilityEffectType<StatModifierAbilityEffect> STAT_MODIFIER_ABILITY_EFFECT = register(OSMCConstants.STAT_MODIFIER_ABILITY_EFFECT_ID, new AbilityEffectType<>(StatModifierAbilityEffect.CODEC));

    public static void init() {}

    public static <T extends AbilityEffect> AbilityEffectType<T> register(String id, AbilityEffectType<T> type) {
        return Registry.register(AbilityEffectType.REGISTRY, OSMCConstants.osmcID(id), type);
    }
}
