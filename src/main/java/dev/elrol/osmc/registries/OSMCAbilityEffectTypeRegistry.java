package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.data.ability_effects.DurationAbilityEffect;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;

public class OSMCAbilityEffectTypeRegistry {

    public static final AbilityEffectType<DurationAbilityEffect> DURATION_ABILITY_EFFECT = register(OSMCConstants.DURATION_ABILITY_EFFECT_ID, new AbilityEffectType<DurationAbilityEffect>(DurationAbilityEffect.CODEC));

    public static void init() {}

    public static <T extends AbilityEffect> AbilityEffectType<T> register(String id, AbilityEffectType<T> type) {
        return Registry.register(AbilityEffectType.REGISTRY, OSMCConstants.osmcID(id), type);
    }
}
