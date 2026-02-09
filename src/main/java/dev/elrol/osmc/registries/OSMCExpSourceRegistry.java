package dev.elrol.osmc.registries;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.BoundSource;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.data.exp.*;
import dev.elrol.osmc.data.exp.cobblemon.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OSMCExpSourceRegistry {

    private static final Map<SkillTrigger, Map<Object, List<BoundSource<?>>>> TRIGGER_CACHE = new EnumMap<>(SkillTrigger.class);

    public static List<BoundSource<?>> getSources(SkillTrigger trigger, Object target) {
        Map<Object, List<BoundSource<?>>> cache = TRIGGER_CACHE.get(trigger);
        if(cache == null) return new ArrayList<>();
        List<BoundSource<?>> sources = new ArrayList<>(cache.getOrDefault(target, List.of()));
        sources.addAll(cache.getOrDefault("GLOBAL", List.of()));
        return sources;
    }

    public static void rebuild(Map<Identifier, Skill> skills, RegistryWrapper.WrapperLookup registryManager) {
        TRIGGER_CACHE.clear();

        if(registryManager == null) return;

        skills.forEach((id, skill) -> {
                for(ExpSource source : skill.getExpSources()) {
                    for(SkillTrigger trigger : source.getTriggers()) {
                        indexEffect(trigger, source, id, registryManager);
                    }
                }
        });
    }

    private static <T extends ExpSource> void indexEffect(SkillTrigger trigger,T source, Identifier id, RegistryWrapper.WrapperLookup registryManager) {
        Map<Object, List<BoundSource<?>>> cache = TRIGGER_CACHE.computeIfAbsent(trigger, a -> new Object2ObjectOpenHashMap<>());

        switch (source) {
            case BlockBreakExpSource breakSource ->
                    indexOrGlobal(cache, breakSource.getTargets(), breakSource, id, registryManager, RegistryKeys.BLOCK);

            case BlockInteractionExpSource interactSource ->
                    indexOrGlobal(cache, interactSource.getTargets(), interactSource, id, registryManager, RegistryKeys.BLOCK);

            case BlockBrushExpSource brushSource ->
                    indexOrGlobal(cache, brushSource.getTargets(), brushSource, id, registryManager, RegistryKeys.BLOCK);

            case ConsumeFoodExpSource consumeFood ->
                    indexOrGlobal(cache, consumeFood.getItems(), consumeFood, id, registryManager, RegistryKeys.ITEM);

            case ConsumePotionExpSource consumePotion ->
                    indexOrGlobal(cache, consumePotion.getEffects(), consumePotion, id, registryManager, RegistryKeys.STATUS_EFFECT);

            case CraftExpSource craft ->
                    indexOrGlobal(cache, craft.getItems(), craft, id, registryManager, RegistryKeys.ITEM);

            case EnchantExpSource enchant ->
                    indexOrGlobal(cache, enchant.getTargets(), enchant, id, registryManager, RegistryKeys.ENCHANTMENT);

            case EntityInteractionExpSource entityInteract ->
                    indexOrGlobal(cache, entityInteract.getEntities(), entityInteract, id, registryManager, RegistryKeys.ENTITY_TYPE);

            case EntityKillExpSource entityKill ->
                    indexOrGlobal(cache, entityKill.getEntities(), entityKill, id, registryManager, RegistryKeys.ENTITY_TYPE);

            case ItemUseExpSource itemUse ->
                    indexOrGlobal(cache, itemUse.getItems(), itemUse, id, registryManager, RegistryKeys.ITEM);

            case PotionBrewExpSource potionBrew ->
                    indexOrGlobal(cache, potionBrew.getIngredients(), potionBrew, id, registryManager, RegistryKeys.ITEM);

            case VillagerTradeExpSource trade -> {
                List<Either<RegistryKey<Item>, TagKey<Item>>> combined = new ArrayList<>(trade.getInputItemStacks());
                combined.addAll(trade.getOutputItemStacks());
                indexOrGlobal(cache, combined, trade, id, registryManager, RegistryKeys.ITEM);
            }

            case CaptureExpSource capture ->
                    indexSpeciesOrGlobal(cache, capture.getTargets(), capture, id);

            case NpcBattleExpSource npcBattle ->
                    indexSpeciesOrGlobal(cache, npcBattle.getTargets(), npcBattle, id);

            case PlayerBattleExpSource playerBattle ->
                    indexSpeciesOrGlobal(cache, playerBattle.getTargets(), playerBattle, id);

            case WildBattleExpSource wildBattle ->
                    indexSpeciesOrGlobal(cache, wildBattle.getTargets(), wildBattle, id);

            case EvolutionExpSource evolution ->
                    addBoundSource(cache, evolution, id);

            case EggHatchExpSource eggHatch ->
                    addBoundSource(cache, eggHatch, id);

            case LevelUpExpSource levelUp ->
                    addBoundSource(cache, levelUp, id);

            case FossilReviveExpSource fossilRevive ->
                    addBoundSource(cache, fossilRevive, id);
            default -> {}
        }
    }

    private static void addBoundSource(Map<Object, List<BoundSource<?>>> cache, Object key, ExpSource source, Identifier id) {
        if(OSMC.CONFIG.getDebug()) OSMC.LOGGER.warn("Adding bound source: {} [{}]", key, source.getType());
        cache.computeIfAbsent(key, obj -> new ArrayList<>())
                .add(new BoundSource<>(source, id));
    }

    private static void addBoundSource(Map<Object, List<BoundSource<?>>> cache, ExpSource source, Identifier id) {
        addBoundSource(cache, "GLOBAL", source, id);
    }

    private static <S, T extends ExpSource> void indexGenericEither(Map<Object, List<BoundSource<?>>> cache, Either<RegistryKey<S>, TagKey<S>> either, T source, Identifier id, RegistryWrapper<S> wrapper) {
        either.ifLeft(left ->
                        wrapper.getOptional(left)
                                .ifPresent(entry -> addBoundSource(cache, entry.value(), source, id)))
                .ifRight(right ->
                        wrapper.getOptional(right)
                                .ifPresent(entryList ->
                                        entryList.forEach(entry -> addBoundSource(cache, entry.value(), source, id))));
    }

    private static <T extends ExpSource> void indexSpeciesEither(Map<Object, List<BoundSource<?>>> cache, Either<Species, String> either, T source, Identifier id) {
        either.ifLeft(species -> addBoundSource(cache, species, source, id))
                .ifRight(label -> {
                    String cleaned = label.startsWith("#") ? label.substring(1) : label;
                    PokemonSpecies.getSpecies().forEach(species -> {
                        if(species.getLabels().contains(cleaned)) {
                            addBoundSource(cache, species, source, id);
                        }
                    });
        });
    }

    private static <T extends ExpSource> void indexSpeciesOrGlobal(Map<Object, List<BoundSource<?>>> cache, List<Either<Species, String>> targets, T source, Identifier id) {
        if(targets == null || targets.isEmpty()) {
            addBoundSource(cache, source, id);
        } else {
            targets.forEach(either -> indexSpeciesEither(cache, either, source, id));
        }
    }

    private static <S, T extends ExpSource> void indexOrGlobal(Map<Object, List<BoundSource<?>>> cache, List<Either<RegistryKey<S>, TagKey<S>>> targets, T source, Identifier id, RegistryWrapper.WrapperLookup registryManager, RegistryKey<Registry<S>> registryKey) {
        if(targets == null || targets.isEmpty()) {
            addBoundSource(cache, source, id);
        } else {
            RegistryWrapper<S> wrapper = registryManager.getWrapperOrThrow(registryKey);
            targets.forEach(either -> indexGenericEither(cache, either, source, id, wrapper));
        }
    }
}
