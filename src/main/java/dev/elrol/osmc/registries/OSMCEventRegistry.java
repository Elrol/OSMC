package dev.elrol.osmc.registries;

import com.cobblemon.mod.common.platform.events.ServerPlayerEvent;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.ExpGain;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.skills.BlockBreakSkill;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class OSMCEventRegistry {

    public static void init() {
        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, pos, blockState, blockEntity) -> {
             if(playerEntity instanceof ServerPlayerEntity player) {
                 String blockID = Registries.BLOCK.getId(blockState.getBlock()).toString();
                 SkillRegistry.getSkillsOfType(BlockBreakSkill.class).forEach((id, skill) -> {
                     for (Map.Entry<String, ExpGain> entry : skill.getExpGainMap().entrySet()) {
                         if(blockID.equalsIgnoreCase(entry.getKey())) {
                             PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
                             int xpGain = entry.getValue().getExpGained();
                             long totalXP = data.addSkillXp(OSMCConstants.MINING_ID, xpGain);
                             long targetXP = data.getTargetXP(OSMCConstants.MINING_ID);
                             int level = data.getSkillLevel(OSMCConstants.MINING_ID);
                             PlayerDataRegistry.updatePlayerData(data);
                             player.sendMessage(Text.literal("Mined " + blockID + " and got " + xpGain + "xp. " + level + " [" + totalXP + "/" + targetXP + "]"));
                         }
                     }
                 });

             }
             return true;
        });

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            OSMC.LOGGER.info("Loading all player skill data");
            PlayerDataRegistry.init();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            OSMC.LOGGER.info("Saving all player skill data");
            PlayerDataRegistry.save();
        });

        ServerPlayerEvents.LEAVE.register(PlayerDataRegistry::save);
    }

}
