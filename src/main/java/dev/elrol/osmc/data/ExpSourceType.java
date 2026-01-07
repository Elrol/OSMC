package dev.elrol.osmc.data;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

public record ExpSourceType<T extends ExpSource> (MapCodec<T> codec) {
    public static final Registry<ExpSourceType<?>> REGISTRY = new SimpleRegistry<>(OSMCConstants.EXP_TYPE_KEY, Lifecycle.stable());
}
