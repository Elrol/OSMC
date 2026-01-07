package dev.elrol.osmc.registries;

import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.BoundSource;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.data.exp.BlockInteractionExpSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;import net.minecraft.util.hit.HitResult;

import java.util.List;

public class OSMCEventRegistry {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(OSMCCommandRegistry::init);

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                if(blockHitResult.getType().equals(HitResult.Type.MISS) || hand.equals(Hand.OFF_HAND)) return ActionResult.PASS;

                BlockState state = world.getBlockState(blockHitResult.getBlockPos());
                Block block = state.getBlock();

                List<BoundSource<BlockInteractionExpSource>> list = ExpSourceRegistry.getInteract(block);
                if(list.isEmpty()) return ActionResult.PASS;
                list.forEach(bound -> {
                    PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
                    Skill skill = SkillRegistry.get(bound.skillID());
                    BlockInteractionExpSource source = bound.source();

                    if(!source.hasProperties(state)) return;

                    int xpGain = source.getExpGain();
                    long totalXP = data.addSkillXp(bound.skillID(), xpGain);
                    long targetXP = data.getTargetXP(bound.skillID());
                    int level = data.getSkillLevel(bound.skillID());

                    PlayerDataRegistry.updatePlayerData(data);
                    player.sendMessage(Text.empty()
                            .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                            .append(skill.getDisplayName())
                            .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(" Interacted with " + block.getName().getString() + " and got " + xpGain + "xp. " + level + " [" + totalXP + "/" + targetXP + "]")));

                });
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, pos, state, blockEntity) -> {
             if(playerEntity instanceof ServerPlayerEntity player) {
                 List<BoundSource<BlockBreakExpSource>> list = ExpSourceRegistry.getBreak(state.getBlock());
                 if(list.isEmpty()) return true;
                 list.forEach(bound -> {
                     PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
                     Skill skill = SkillRegistry.get(bound.skillID());
                     BlockBreakExpSource source = bound.source();

                     if(!source.hasProperties(state)) return;

                     int xpGain = source.getExpGain();
                     long totalXP = data.addSkillXp(bound.skillID(), xpGain);
                     long targetXP = data.getTargetXP(bound.skillID());
                     int level = data.getSkillLevel(bound.skillID());
                     PlayerDataRegistry.updatePlayerData(data);
                     player.sendMessage(Text.empty()
                             .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                             .append(skill.getDisplayName())
                             .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                             .append(Text.literal(" Mined " + state.getBlock().getName().getString() + " and got " + xpGain + "xp. " + level + " [" + totalXP + "/" + targetXP + "]")));

                 });
             }
             return true;
        });

        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            ExpSourceRegistry.rebuild(SkillRegistry.getAll());
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
