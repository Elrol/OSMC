package dev.elrol.osmc.registries;

import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.BoundSource;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.data.exp.BlockInteractionExpSource;
import dev.elrol.osmc.data.exp.ConsumePotionExpSource;
import dev.elrol.osmc.events.EnchantingEvent;
import dev.elrol.osmc.events.LivingConsumeEvent;
import dev.elrol.osmc.events.PlayerCraftingEvent;
import dev.elrol.osmc.libs.MathUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSMCEventRegistry {

    private static int ticksSinceSave = 0;
    private static int ticksSinceBufferPayout = 0;

    public static void init() {
        CommandRegistrationCallback.EVENT.register(OSMCCommandRegistry::init);

        LivingConsumeEvent.POTION.register((living, stack, potion) -> {
            if(living instanceof ServerPlayerEntity player) {
                PotionContentsComponent potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
                potionContentsComponent.forEachEffect((effect) -> {
                    Map<String, Double> variables = new HashMap<>();
                    variables.put("duration", (double) effect.getDuration());
                    variables.put("amplifier", (double) effect.getAmplifier());

                    ExpSourceRegistry.getConsumePotion(effect.getEffectType()).forEach(source -> {
                        Skill skill = SkillRegistry.get(source.skillID());
                        if(skill == null) return;
                        ConsumePotionExpSource expSource = source.source();
                        variables.put("xp", (double) expSource.getExpGain());
                        double expGained = MathUtils.calculate(expSource.getFormula(), variables);
                        PlayerDataRegistry.bufferExp(player, source.skillID(), (int) expGained);
                    });
                });
            }
        });

        LivingConsumeEvent.FOOD.register((living, stack) -> {
            if(living instanceof ServerPlayerEntity player) {
                ExpSourceRegistry.getConsumeFood(stack.getItem()).forEach(boundSource ->
                        PlayerDataRegistry.bufferExp(player, boundSource.skillID(), boundSource.source().getExpGain()));
            }
        });

        PlayerCraftingEvent.EVENT.register((player, stack, amount) ->
                ExpSourceRegistry.getCraft(stack.getItem()).forEach(boundSource ->
                        PlayerDataRegistry.bufferExp(player, boundSource.skillID(), boundSource.source().getExpGain() * amount)));

        EnchantingEvent.EVENT.register(((player, enchantedItem, enchantPower, xpSpent) -> {
            enchantedItem.getEnchantments().getEnchantmentEntries().forEach((entry) ->
                ExpSourceRegistry.getEnchant(entry.getKey()).forEach(source ->
                        PlayerDataRegistry.bufferExp(player, source.skillID(), (int) source.source().calculate(entry.getIntValue(), enchantPower, xpSpent)))
            );
        }));

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                if(blockHitResult.getType().equals(HitResult.Type.MISS) || hand.equals(Hand.OFF_HAND)) return ActionResult.PASS;

                BlockState state = world.getBlockState(blockHitResult.getBlockPos());
                Block block = state.getBlock();

                List<BoundSource<BlockInteractionExpSource>> list = ExpSourceRegistry.getBlockInteract(block);
                if(list.isEmpty()) return ActionResult.PASS;
                list.forEach(bound -> {
                    BlockInteractionExpSource source = bound.source();
                    if(!source.hasProperties(state)) return;

                    PlayerDataRegistry.bufferExp(player, bound.skillID(), source.getExpGain());
                });
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, pos, state, blockEntity) -> {
             if(playerEntity instanceof ServerPlayerEntity player) {
                 List<BoundSource<BlockBreakExpSource>> list = ExpSourceRegistry.getBlockBreak(state.getBlock());
                 if(list.isEmpty()) return true;
                 list.forEach(bound -> {
                     BlockBreakExpSource source = bound.source();

                     if(!source.hasProperties(state)) return;
                     PlayerDataRegistry.bufferExp(player, bound.skillID(), source.getExpGain());
                 });
             }
             return true;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticksSinceSave++;
            ticksSinceBufferPayout++;

            int autoSaveDelay = OSMC.CONFIG.autoSave * 1200;

            if(ticksSinceSave >= autoSaveDelay) {
                ticksSinceSave = 0;

                OSMC.LOGGER.info("Autosaving Data");
                PlayerDataRegistry.save();
            }

            if(ticksSinceBufferPayout >= OSMC.CONFIG.expPayout) {
                ticksSinceBufferPayout = 0;
                PlayerDataRegistry.payBuffer(server);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SkillRegistry.init(server);

            ExpSourceRegistry.rebuild(SkillRegistry.getAll(), server.getRegistryManager());
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
