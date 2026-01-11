package dev.elrol.osmc.registries;

import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.data.BoundSource;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.*;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpSourceRegistry {

    private static final Map<Block,                             List<BoundSource<BlockBreakExpSource>>>                 BLOCK_BREAK_CACHE               = new Reference2ObjectOpenHashMap<>();
    private static final Map<Block,                             List<BoundSource<BlockInteractionExpSource>>>           BLOCK_INTERACT_CACHE            = new Reference2ObjectOpenHashMap<>();
    private static final Map<Item,                              List<BoundSource<ConsumeFoodExpSource>>>                CONSUME_FOOD_CACHE              = new Reference2ObjectOpenHashMap<>();
    private static final Map<RegistryEntry<StatusEffect>,       List<BoundSource<ConsumePotionExpSource>>>              CONSUME_POTION_CACHE            = new Reference2ObjectOpenHashMap<>();
    private static final Map<Item,                              List<BoundSource<CraftExpSource>>>                      CRAFT_CACHE                     = new Reference2ObjectOpenHashMap<>();
    private static final Map<RegistryEntry<Enchantment>,        List<BoundSource<EnchantExpSource>>>                    ENCHANT_CACHE                   = new Reference2ObjectOpenHashMap<>();
    private static final Map<EntityType<?>,                     List<BoundSource<EntityInteractionExpSource>>>          ENTITY_INTERACT_CACHE           = new Reference2ObjectOpenHashMap<>();
    private static final Map<EntityType<?>,                     List<BoundSource<EntityKillExpSource>>>                 ENTITY_KILL_CACHE               = new Reference2ObjectOpenHashMap<>();
    private static final Map<Item,                              List<BoundSource<ItemUseExpSource>>>                    ITEM_USE_CACHE                  = new Reference2ObjectOpenHashMap<>();
    private static final Map<RegistryEntry<Potion>,             List<BoundSource<PotionBrewExpSource>>>                 POTION_BREW_CACHE               = new Reference2ObjectOpenHashMap<>();
    private static final Map<Item,                              List<BoundSource<VillagerTradeExpSource>>>              VILLAGER_TRADE_CACHE            = new Reference2ObjectOpenHashMap<>();

    private static void clear() {
        BLOCK_BREAK_CACHE.clear();
        BLOCK_INTERACT_CACHE.clear();
        CONSUME_FOOD_CACHE.clear();
        CONSUME_POTION_CACHE.clear();
        CRAFT_CACHE.clear();
        ENCHANT_CACHE.clear();
        ENTITY_INTERACT_CACHE.clear();
        ENTITY_KILL_CACHE.clear();
        ITEM_USE_CACHE.clear();
        POTION_BREW_CACHE.clear();
        VILLAGER_TRADE_CACHE.clear();
    }

    @NotNull
    public static List<BoundSource<BlockBreakExpSource>> getBlockBreak(Block block) {
        return BLOCK_BREAK_CACHE.getOrDefault(block, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<BlockInteractionExpSource>> getBlockInteract(Block block) {
        return BLOCK_INTERACT_CACHE.getOrDefault(block, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<ConsumeFoodExpSource>> getConsumeFood(Item item) {
        return CONSUME_FOOD_CACHE.getOrDefault(item, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<ConsumePotionExpSource>> getConsumePotion(RegistryEntry<StatusEffect> effect) {
        return CONSUME_POTION_CACHE.getOrDefault(effect, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<CraftExpSource>> getCraft(Item item) {
        return CRAFT_CACHE.getOrDefault(item, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<EnchantExpSource>> getEnchant(RegistryEntry<Enchantment> enchant) {
        return ENCHANT_CACHE.getOrDefault(enchant, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<EntityInteractionExpSource>> getEntityInteract(EntityType<?> entity) {
        return ENTITY_INTERACT_CACHE.getOrDefault(entity, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<EntityKillExpSource>> getEntityKill(EntityType<?> entity) {
        return ENTITY_KILL_CACHE.getOrDefault(entity, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<ItemUseExpSource>> getItemUse(Item item) {
        return ITEM_USE_CACHE.getOrDefault(item, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<PotionBrewExpSource>> getPotionBrew(RegistryEntry<Potion> potion) {
        return POTION_BREW_CACHE.getOrDefault(potion, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<VillagerTradeExpSource>> getVillagerTrade(Item item) {
        return VILLAGER_TRADE_CACHE.getOrDefault(item, new ArrayList<>());
    }

    public static void rebuild(Map<Identifier, Skill> skills, RegistryWrapper.WrapperLookup registryManager) {
        clear();

        skills.forEach((id, skill) -> {
            for(ExpSource source : skill.getExpSources()) {

                switch(source) {
                    case BlockBreakExpSource breakSource -> breakSource.getTargets().forEach(either ->
                            indexEither(either, Registries.BLOCK, breakSource, id, BLOCK_BREAK_CACHE));

                    case BlockInteractionExpSource interactSource -> interactSource.getTargets().forEach(either ->
                            indexEither(either, Registries.BLOCK, interactSource, id, BLOCK_INTERACT_CACHE));

                    case ConsumeFoodExpSource consumeFood -> consumeFood.getItems().forEach(item ->
                            addBoundSource(CONSUME_FOOD_CACHE, item.getItem(), consumeFood, id));

                    case ConsumePotionExpSource consumePotion -> consumePotion.getEffects().forEach(effect ->
                            addBoundSource(CONSUME_POTION_CACHE, effect, consumePotion, id));

                    case CraftExpSource craft -> craft.getItems().forEach(item ->
                            addBoundSource(CRAFT_CACHE, item.getItem(), craft, id));

                    case EnchantExpSource enchant -> enchant.getTargets().forEach(enchantment ->
                            indexEither(enchantment, registryManager.getWrapperOrThrow(RegistryKeys.ENCHANTMENT), enchant, id, ENCHANT_CACHE));

                    case EntityInteractionExpSource entityInteract -> entityInteract.getEntities().forEach(entity ->
                            addBoundSource(ENTITY_INTERACT_CACHE, entity, entityInteract, id));

                    case EntityKillExpSource entityKill -> entityKill.getEntities().forEach(entity ->
                            addBoundSource(ENTITY_KILL_CACHE, entity, entityKill, id));

                    case ItemUseExpSource itemUse -> itemUse.getItems().forEach(item ->
                            addBoundSource(ITEM_USE_CACHE, item.getItem(), itemUse, id));

                    case PotionBrewExpSource potionBrew -> potionBrew.getPotions().forEach(potion ->
                            addBoundSource(POTION_BREW_CACHE, potion, potionBrew, id));

                    case VillagerTradeExpSource trade -> trade.getItems().forEach(item ->
                            addBoundSource(VILLAGER_TRADE_CACHE, item.getItem(), trade, id));

                    default -> {}
                }
            }
        });
    }

    private static <T, S extends ExpSource> void indexEither(Either<RegistryEntry<T>, TagKey<T>> target, RegistryWrapper.Impl<T> registry, S source, Identifier id, Map<RegistryEntry<T>, List<BoundSource<S>>> cache) {
        target.ifLeft(obj -> addBoundSource(cache, obj, source, id))
                .ifRight(tagKey -> registry.getOptional(tagKey).ifPresent(entryList -> {
                    for(RegistryEntry<T> entry : entryList) {
                        addBoundSource(cache, entry, source, id);
                    }
                }));
    }

    private static <U, T extends ExpSource> void addBoundSource(Map<U, List<BoundSource<T>>> cache, U key, T source, Identifier id) {
        cache.computeIfAbsent(key, obj -> new ArrayList<>())
                .add(new BoundSource<>(source, id));
    }

    private static <T, S extends ExpSource> void indexEither(Either<T, TagKey<T>> target, Registry<T> registry, S source, Identifier id, Map<T, List<BoundSource<S>>> cache) {
        target.ifLeft(obj -> addBoundSource(cache, obj, source, id))
                .ifRight(tagKey -> {
                    for(RegistryEntry<T> entry : registry.getOrCreateEntryList(tagKey)) {
                        addBoundSource(cache, entry.value(), source, id);
                    }
                });
    }
}
