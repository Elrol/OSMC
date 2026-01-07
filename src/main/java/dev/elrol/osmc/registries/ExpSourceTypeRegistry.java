package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.data.exp.BlockInteractionExpSource;import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ExpSourceTypeRegistry {

    public static final ExpSourceType<BlockBreakExpSource> BREAK_EXP_SOURCE = register(OSMCConstants.BLOCK_BREAK_EXP_ID, new ExpSourceType<>(BlockBreakExpSource.CODEC));
    public static final ExpSourceType<BlockInteractionExpSource> BLOCK_INTERACT_EXP_SOURCE = register(OSMCConstants.BLOCK_INTERACT_EXP_ID, new ExpSourceType<>(BlockInteractionExpSource.CODEC));

    public static void init() {}

    public static <T extends ExpSource> ExpSourceType<T> register(String id, ExpSourceType<T> type) {
        return Registry.register(ExpSourceType.REGISTRY, Identifier.of(OSMCConstants.MODID, id), type);
    }

}
