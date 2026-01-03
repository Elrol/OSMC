package dev.elrol.osmc.data.skills;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

public record SkillDataType<T extends ISkillData> (MapCodec<T> codec) {
    public static final Registry<SkillDataType<?>> REGISTRY = new SimpleRegistry<>(OSMCConstants.SKILL_TYPE_KEY, Lifecycle.stable());
}
