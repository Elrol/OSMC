package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.ExpGain;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.Map;

public class OSMCEventRegistry {

    public static void init() {
        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, pos, blockState, blockEntity) -> {
             if(playerEntity instanceof ServerPlayerEntity player) {
                 String blockID = Registries.BLOCK.getId(blockState.getBlock()).toString();
                 for (Map.Entry<String, ExpGain> entry : SkillRegistry.MINING_SKILL.getExpGainMap().entrySet()) {
                     if(blockID.equalsIgnoreCase(entry.getKey())) {

                     }
                 }
             }
             return true;
        });
    }

}
