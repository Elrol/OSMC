package dev.elrol.osmc.registries;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.*;
import dev.elrol.osmc.data.skill_effects.StatModifierSkillEffect;
import dev.elrol.osmc.data.exp.*;
import dev.elrol.osmc.data.exp.cobblemon.*;
import dev.elrol.osmc.data.exp.quickbattle.QuickBattleExpSource;
import dev.elrol.osmc.data.functions.BlockDropLootFunction;
import dev.elrol.osmc.data.functions.MobDropLootFunction;
import dev.elrol.osmc.events.*;
import dev.elrol.osmc.interfaces.IPlacedTracker;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.SkillUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.*;

public class OSMCEventRegistry {

    private static int ticksSinceSave = 0;
    private static int ticksSinceBufferPayout = 0;
    private static final Random random = new Random();

    private static final Map<UUID, Long> LAST_CLICK_CACHE = new HashMap<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.register(OSMCCommandRegistry::init);

        HarvestEvent.EVENT.register(((player, state, pos) -> {
            List<BoundSource<?>> boundSources = OSMCExpSourceRegistry.getSources(SkillTrigger.HARVEST, state.getBlock());

            if(boundSources.isEmpty()) return;

            for (BoundSource<?> boundSource : boundSources) {
                if(boundSource.source() instanceof BlockHarvestExpSource source) {
                    if(source.hasProperties(state))
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                }
            }
        }));

        QuickBattleEvent.EVENT.register((attacker, defender, result) -> {
            if(attacker.getOwnerPlayer() instanceof ServerPlayerEntity player) {
                if(result.isVictory()) {
                    List<BoundSource<?>> boundSources = OSMCExpSourceRegistry.getSources(SkillTrigger.BATTLE_END, defender.getSpecies());
                    boundSources.forEach(boundSource -> {
                        if(boundSource.source() instanceof QuickBattleExpSource source) {
                            OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.calculate(defender));
                        }
                    });
                }
            }
        });

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(event -> {
            if(!OSMC.CONFIG.getCobblemonTiers().getEnabled()) return;
            PokemonEntity pokemon = event.getEntity();
            if(pokemon.getWorld() instanceof ServerWorld world) {
                if(world.getClosestPlayer(pokemon, 64.0) instanceof ServerPlayerEntity player) {
                    CobblemonTier tier = SkillUtils.getPlayerTier(player);
                    if(tier == null) return;

                    Pokemon pokeData = pokemon.getPokemon();
                    if(tier.isValid(pokeData)) {
                        pokeData.setLevel(random.nextInt(tier.getMinSpawnedLevel(), tier.getMaxSpawnedLevel() + 1));
                        pokeData.heal();
                    } else {
                        pokemon.discard();
                    }
                }
            }
        });

        BlockBrushEvent.EVENT.register((player, state) -> {
            Registries.BLOCK.getKey(state.getBlock()).ifPresent(key -> {
                List<BoundSource<?>> sources = OSMCExpSourceRegistry.getSources(SkillTrigger.BLOCK_BRUSH, key);

                sources.forEach(boundSource -> {
                    if(boundSource.source() instanceof BlockBrushExpSource source) {
                        if(source.hasProperties(state)) {
                            OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                        }
                    }
                });
            });
        });

        SkillLevelUpEvent.EVENT.register((player, skillID, level) -> {
            List<BoundEffect<?>> effects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.LEVEL_UP);

            effects.forEach(boundEffect -> {
                if(boundEffect.effect() instanceof StatModifierSkillEffect effect) {
                    effect.updateAttribute(player, skillID, level);
                }
            });
        });

        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(event -> {
            Pokemon pokemon = event.getPokemon();
            if(pokemon.isPlayerOwned() && pokemon.getOwnerPlayer() != null) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.EVOLVE, pokemon.getSpecies()).forEach(boundSource -> {
                    if(boundSource.source() instanceof EvolutionExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(pokemon.getOwnerPlayer(), boundSource.skillID(), source.calculate(pokemon));
                });
            }
        });

        CobblemonEvents.FOSSIL_REVIVED.subscribe(event -> {
            ServerPlayerEntity player = event.getPlayer();
            if(player == null) return;

            Pokemon pokemon = event.getPokemon();
            OSMCExpSourceRegistry.getSources(SkillTrigger.REVIVE_FOSSIL, pokemon).forEach(boundSource -> {
                if(boundSource.source() instanceof FossilReviveExpSource source)
                    OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.calculate(pokemon));
            });
        });

        CobblemonEvents.HATCH_EGG_POST.subscribe(event -> {
           ServerPlayerEntity player = event.getPlayer();

           Pokemon pokemon = event.getPokemon();
           OSMCExpSourceRegistry.getSources(SkillTrigger.EGG_HATCH, pokemon).forEach(boundSource -> {
                if(boundSource.source() instanceof EggHatchExpSource source)
                   OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.calculate(pokemon));
           });
        });

        CobblemonEvents.LEVEL_UP_EVENT.subscribe(event -> {
            Pokemon pokemon = event.getPokemon();
            if(pokemon.isPlayerOwned() && pokemon.getOwnerPlayer() != null) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.LEVEL_UP, pokemon).forEach(boundSource -> {
                    if(boundSource.source() instanceof LevelUpExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(pokemon.getOwnerPlayer(), boundSource.skillID(), source.calculate(pokemon));
                });
            }
        });

        CobblemonEvents.POKEMON_CAPTURED.subscribe(event -> {
            ServerPlayerEntity player = event.getPlayer();
            Pokemon pokemon = event.getPokemon();
            float ball_modifier = event.getPokeBallEntity().getPokeBall().getCatchRateModifier().value(player, pokemon);
            OSMCExpSourceRegistry.getSources(SkillTrigger.CAPTURE, pokemon.getSpecies()).forEach(boundSource -> {
                if(boundSource.source() instanceof CaptureExpSource source) {
                    double exp = source.calculate(pokemon, ball_modifier);
                    OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), (int) exp);
                }
            });
        });

        CobblemonEvents.BATTLE_VICTORY.subscribe(event -> {
            if(!event.getWasWildCapture()) {
                for (BattleActor winner : event.getWinners()) {
                    if(winner instanceof PlayerBattleActor playerActor) {
                        ServerPlayerEntity player = playerActor.getEntity();
                        if(player == null) continue;

                        List<Pokemon> pokemonList = event.getLosers().stream().filter(actor -> actor instanceof PokemonBattleActor).map(actor -> ((PokemonBattleActor)actor).getPokemon().getOriginalPokemon()).toList();

                        pokemonList.forEach(pokemon -> {
                            List<BoundSource<?>> sources = OSMCExpSourceRegistry.getSources(SkillTrigger.BATTLE_END, pokemon.getSpecies());

                            sources.forEach(boundSource -> {
                                if (event.getBattle().isPvW() && boundSource.source() instanceof WildBattleExpSource source) {
                                    OSMCPlayerDataRegistry.bufferExp(player.getUuid(), boundSource.skillID(), source.calculate(pokemon));
                                } else if (event.getBattle().isPvP() && boundSource.source() instanceof PlayerBattleExpSource source) {
                                    OSMCPlayerDataRegistry.bufferExp(player.getUuid(), boundSource.skillID(), source.calculate(pokemon));
                                } else if (event.getBattle().isPvN() && boundSource.source() instanceof NpcBattleExpSource source) {
                                    OSMCPlayerDataRegistry.bufferExp(player.getUuid(), boundSource.skillID(), source.calculate(pokemon));
                                }
                            });
                        });


                    }
                }
            }
        });

        VillagerTradeEvent.EVENT.register((playerEntity, merchant, trade) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                List<Item> items = new ArrayList<>();
                items.add(trade.getDisplayedFirstBuyItem().getItem());
                items.add(trade.getDisplayedSecondBuyItem().getItem());
                items.add(trade.getSellItem().getItem());

                items.forEach(item -> {
                    OSMCExpSourceRegistry.getSources(SkillTrigger.TRADE, item).forEach(boundSource -> {
                        if(boundSource.source() instanceof VillagerTradeExpSource source) {
                            if (source.isValid(trade)) {
                                OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), (int) source.calculate(trade));
                            }
                        }
                    });
                });
            }
        });

        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, hitResult) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.ENTITY_INTERACT, entity.getType()).forEach(boundSource -> {
                    if(boundSource.source() instanceof EntityInteractionExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                });
            }
            return ActionResult.PASS;
        });

        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(((serverWorld, entity, killedEntity) -> {
            if(entity instanceof ServerPlayerEntity player) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.ENTITY_KILL, killedEntity.getType()).forEach(boundSource -> {
                    if (boundSource.source() instanceof EntityKillExpSource source) {
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), boundSource.source().getExpGain());
                    }
                });
            }
        }));

        UseItemCallback.EVENT.register(((playerEntity, world, hand) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.ITEM_USE, player.getStackInHand(hand).getItem()).forEach(boundSource -> {
                    if(boundSource.source() instanceof ItemUseExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                });

                long curTime = System.currentTimeMillis();
                long lastTime = LAST_CLICK_CACHE.getOrDefault(player.getUuid(), 0L);

                if(curTime - lastTime < OSMC.CONFIG.getMillisecondsToActivateAbility()) {
                    // TODO double click activated, use ability
                    Skill targetSkill = null;
                    ItemStack heldItem = player.getMainHandStack();

                    for (Skill skill : OSMCSkillRegistry.getAll().values()) {
                        if(skill.isValidTool(heldItem)) {
                            targetSkill = skill;
                            break;
                        }
                    }
                    if(targetSkill != null)
                        player.sendMessage(Text.of("Activate ability " + targetSkill.getAbilityID()));
                    else
                        player.sendMessage(Text.of("No active ability found for current tool"));

                    LAST_CLICK_CACHE.put(player.getUuid(), 0L);
                } else {
                    LAST_CLICK_CACHE.put(player.getUuid(), curTime);
                }
            }
            return TypedActionResult.pass(playerEntity.getStackInHand(hand));
        }));

        PlayerBrewingEvent.EVENT.register((uuid, ingredient, beforePotion, afterPotion) -> {
                OSMCExpSourceRegistry.getSources(SkillTrigger.BREWED, ingredient.getItem()).forEach(boundSource -> {
                    if(boundSource.source() instanceof PotionBrewExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(uuid, boundSource.skillID(), source.getExpGain());
                });
        });

        LivingConsumeEvent.POTION.register((living, stack, potion) -> {
            if(living instanceof ServerPlayerEntity player) {
                PotionContentsComponent potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
                potionContentsComponent.forEachEffect((effect) -> {
                    Map<String, Double> variables = new HashMap<>();
                    variables.put("duration", (double) effect.getDuration());
                    variables.put("amplifier", (double) effect.getAmplifier());

                    OSMCExpSourceRegistry.getSources(SkillTrigger.CONSUME, effect.getEffectType()).forEach(boundSource -> {
                        if(boundSource.source() instanceof ConsumePotionExpSource source) {
                            Skill skill = OSMCSkillRegistry.get(boundSource.skillID());
                            if (skill == null) return;
                            variables.put("xp", (double) source.getExpGain());
                            double expGained = MathUtils.calculate(source.getFormula(), variables);
                            OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), (int) expGained);
                        }
                    });
                });
            }
        });

        LivingConsumeEvent.FOOD.register((living, stack) -> {
            if(living instanceof ServerPlayerEntity player) {
                OSMCExpSourceRegistry.getSources(SkillTrigger.CONSUME, stack.getItem()).forEach(boundSource -> {
                    if(boundSource.source() instanceof ConsumeFoodExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                });
            }
        });

        PlayerCraftingEvent.EVENT.register((player, stack, amount) ->
                OSMCExpSourceRegistry.getSources(SkillTrigger.CRAFTED, stack.getItem()).forEach(boundSource -> {
                    if(boundSource.source() instanceof ItemUseExpSource source)
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain() * amount);}));

        EnchantingEvent.EVENT.register(((player, enchantedItem, enchantPower, xpSpent) -> {
            enchantedItem.getEnchantments().getEnchantmentEntries().forEach((entry) ->
                OSMCExpSourceRegistry.getSources(SkillTrigger.ENCHANT, entry.getKey()).forEach(boundSource -> {
                        if(boundSource.source() instanceof EnchantExpSource source)
                            OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), (int) source.calculate(entry.getIntValue(), enchantPower, xpSpent));
                })
            );
        }));

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if(playerEntity instanceof ServerPlayerEntity player) {
                if(blockHitResult.getType().equals(HitResult.Type.MISS) || hand.equals(Hand.OFF_HAND)) return ActionResult.PASS;

                BlockState state = world.getBlockState(blockHitResult.getBlockPos());
                Block block = state.getBlock();

                List<BoundSource<?>> list = OSMCExpSourceRegistry.getSources(SkillTrigger.BLOCK_INTERACT, block);
                if(list.isEmpty()) return ActionResult.PASS;
                list.forEach(boundSource -> {
                    if(boundSource.source() instanceof BlockInteractionExpSource source) {
                        if (!source.hasProperties(state)) return;
                        OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                    }
                });
            }
            return ActionResult.PASS;
        });

        BlockPlaceEvent.POST.register(((pos, world, player, stack, state) -> {
            if(world instanceof ServerWorld serverWorld && player instanceof ServerPlayerEntity serverPlayer) {
                Chunk chunk = serverWorld.getChunk(pos);
                ChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
                if(section == null) return;

                ((IPlacedTracker)section).osmc$setPlaced(
                        pos.getX() & 15,
                        pos.getY() & 15,
                        pos.getZ() & 15
                );
            }
        }));

        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, pos, state, blockEntity) -> {
             if(playerEntity instanceof ServerPlayerEntity player) {
                 List<BoundSource<?>> blockBreakList = OSMCExpSourceRegistry.getSources(SkillTrigger.BLOCK_BREAK, state.getBlock());
                 boolean isSilkTouch = SkillUtils.hasEnchantment(world.getRegistryManager(), player.getMainHandStack(), Enchantments.SILK_TOUCH);

                 if(blockBreakList.isEmpty()) return true;

                 blockBreakList.forEach(boundSource -> {
                     if(boundSource.source() instanceof BlockBreakExpSource source) {
                         if (!source.hasProperties(state)) return;
                         if(source.getExpGain() > 1 && isSilkTouch) return;
                         OSMCPlayerDataRegistry.bufferExp(player, boundSource.skillID(), source.getExpGain());
                     }
                 });

                 Chunk chunk = world.getChunk(pos);
                 ChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
                 if(section != null) {
                     ((IPlacedTracker)section).osmc$break(
                             pos.getX() & 15,
                             pos.getY() & 15,
                             pos.getZ() & 15
                     );
                 }
             }
             return true;
        });

        LootTableEvents.MODIFY.register(((key, builder, source, registries) -> {
            if(source.isBuiltin()) {
                builder.modifyPools(poolBuilder -> {
                    String path = key.getValue().getPath();
                    if(path.startsWith("blocks/"))
                        poolBuilder.apply(BlockDropLootFunction.builder().build());
                    else if(path.startsWith("entities/"))
                        poolBuilder.apply(MobDropLootFunction.builder().build());
                });
            }
        }));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ticksSinceSave++;
            ticksSinceBufferPayout++;

            int autoSaveDelay = OSMC.CONFIG.getAutoSave() * 1200;

            if(ticksSinceSave >= autoSaveDelay) {
                ticksSinceSave = 0;

                OSMC.LOGGER.info("Autosaving Data");
                OSMCPlayerDataRegistry.save();
            }

            if(ticksSinceBufferPayout >= OSMC.CONFIG.getExpPayout()) {
                ticksSinceBufferPayout = 0;
                OSMCPlayerDataRegistry.payBuffer(server);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, player, b) -> {
            refreshStatModifiers(player);
        }));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            OSMCItems.register();

            OSMCAbilityRegistry.init(server);
            OSMCSkillRegistry.init(server);

            OSMCExpSourceRegistry.rebuild(OSMCSkillRegistry.getAll(), server.getRegistryManager());
            OSMCSkillEffectRegistry.rebuild(OSMCSkillRegistry.getAll(), server.getRegistryManager());



            OSMC.LOGGER.info("Loading all player skill data");
            OSMCPlayerDataRegistry.init();
            OSMCLeaderboard.populate();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            OSMC.LOGGER.info("Saving all player skill data");
            OSMCPlayerDataRegistry.save();
        });

        ServerPlayerEvents.JOIN.register((player) -> {
            OSMCPlayerDataRegistry.load(player);
            refreshStatModifiers(player);
        });

        ServerPlayerEvents.LEAVE.register(OSMCPlayerDataRegistry::save);
    }

    private static void refreshStatModifiers(ServerPlayerEntity player) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());

        List<BoundEffect<?>> effects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.LOGIN);

        data.getSkillExpMap().keySet().forEach(skillID -> {
            int level = data.getSkillLevel(skillID);
            effects.forEach(boundEffect -> {
                if(boundEffect.skillID().equals(skillID) && boundEffect.effect() instanceof StatModifierSkillEffect effect) {
                    effect.updateAttribute(player, skillID, level);
                }
            });
        });
    }
}
