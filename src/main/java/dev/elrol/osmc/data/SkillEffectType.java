package dev.elrol.osmc.data;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

public record SkillEffectType<T extends SkillEffect> (MapCodec<T> codec) {
    public static final Registry<SkillEffectType<?>> REGISTRY = new SimpleRegistry<>(OSMCConstants.EFFECT_TYPE_KEY, Lifecycle.stable());
}
