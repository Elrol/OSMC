package dev.elrol.osmc.data;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

public record AbilityEffectType<T extends AbilityEffect> (MapCodec<T> codec) {
    public static final Registry<AbilityEffectType<?>> REGISTRY = new SimpleRegistry<>(OSMCConstants.ABILITY_EFFECT_TYPE_KEY, Lifecycle.stable());
}
