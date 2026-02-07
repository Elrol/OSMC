package dev.elrol.osmc.registries;

import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.data.functions.BlockDropLootFunction;
import dev.elrol.osmc.data.functions.MobDropLootFunction;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class OSMCLootFunctionRegistry {

    public static final LootFunctionType<BlockDropLootFunction> BLOCK_DROP_MULTIPLIER_FUNCTION_TYPE         = register(OSMCConstants.BLOCK_DROP_MULTIPLIER_EFFECT_ID,   BlockDropLootFunction.CODEC);
    public static final LootFunctionType<MobDropLootFunction> MOB_DROP_MULTIPLIER_FUNCTION_TYPE             = register(OSMCConstants.MOB_DROP_MULTIPLIER_EFFECT_ID,     MobDropLootFunction.CODEC);

    public static void init() {}

    private static <T extends ConditionalLootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, OSMCConstants.osmcID(id), new LootFunctionType<>(codec));
    }

}
