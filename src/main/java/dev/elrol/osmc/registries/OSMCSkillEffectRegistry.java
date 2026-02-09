package dev.elrol.osmc.registries;

import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.*;
import dev.elrol.osmc.data.effects.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

public class OSMCSkillEffectRegistry {

    private static final Map<SkillTrigger, Map<Object, List<BoundEffect<?>>>> TRIGGER_CACHE = new EnumMap<>(SkillTrigger.class);

    public static List<BoundEffect<?>> getEffects(SkillTrigger trigger) {
        return getEffects(trigger, "GLOBAL");
    }

    public static List<BoundEffect<?>> getEffects(SkillTrigger trigger, Object target) {
        Map<Object, List<BoundEffect<?>>> cache = TRIGGER_CACHE.get(trigger);
        if(cache == null) return new ArrayList<>();
        if(target == null) return cache.getOrDefault("GLOBAL", List.of());

        List<BoundEffect<?>> effects = new ArrayList<>(cache.getOrDefault(target, List.of()));
        effects.addAll(cache.getOrDefault("GLOBAL", List.of()));

        return effects;
    }

    public static void rebuild(Map<Identifier, Skill> skills, RegistryWrapper.WrapperLookup registryManager) {
        TRIGGER_CACHE.clear();

        skills.forEach((id,skill) -> {
            for(SkillEffect effect : skill.getSkillEffects()) {
                for(SkillTrigger trigger : effect.getTriggers()) {
                    indexEffect(trigger, effect, id, registryManager);
                }
            }
        });
    }

    private static <T extends SkillEffect> void indexEffect(SkillTrigger trigger, T effect, Identifier id, RegistryWrapper.WrapperLookup registryManager) {
        Map<Object, List<BoundEffect<?>>> cache = TRIGGER_CACHE.computeIfAbsent(trigger, a -> new Object2ObjectOpenHashMap<>());

        switch(effect) {
            case BlockDropMultiplierSkillEffect blockDropMultEffect ->
                    indexOrGlobal(cache, blockDropMultEffect.getTargets(), blockDropMultEffect, id, registryManager, RegistryKeys.ITEM);
            case MobDropMultiplierSkillEffect mobDropMultEffect ->
                    indexOrGlobal(cache, mobDropMultEffect.getTargets(), mobDropMultEffect, id, registryManager, RegistryKeys.ITEM);
            case LootRollSkillEffect lootRollEffect ->
                    indexOrGlobal(cache, lootRollEffect.getLootTables(), lootRollEffect, id);
            case DamageMitigationSkillEffect damageMitigationEffect ->
                    indexOrGlobal(cache, damageMitigationEffect.getDamageTypes(), damageMitigationEffect, id, registryManager, RegistryKeys.DAMAGE_TYPE);
            case StatModifierSkillEffect statModifierEffect ->
                    addBoundEffect(cache, statModifierEffect, id);
            default -> {}
        }
    }

    private static void addBoundEffect(Map<Object, List<BoundEffect<?>>> cache, Object key, SkillEffect effect, Identifier id) {
        if(OSMC.CONFIG.getDebug()) OSMC.LOGGER.warn("Adding bound effect: {} [{}]", key, effect.getType());
        cache.computeIfAbsent(key, obj -> new ArrayList<>())
                .add(new BoundEffect<>(effect, id));
    }

    private static void addBoundEffect(Map<Object, List<BoundEffect<?>>> cache, SkillEffect effect, Identifier id) {
        addBoundEffect(cache, "GLOBAL", effect, id);
    }

    private static <S, T extends SkillEffect> void indexOrGlobal(Map<Object, List<BoundEffect<?>>> cache, List<S> keys, T effect, Identifier id) {
        if(keys.isEmpty()) {
            addBoundEffect(cache, effect, id);
        } else {
            keys.forEach(key -> addBoundEffect(cache, key, effect, id));
        }
    }

    private static <S, T extends SkillEffect> void indexOrGlobal(Map<Object, List<BoundEffect<?>>> cache, List<Either<RegistryKey<S>, TagKey<S>>> targets, T effect, Identifier id, RegistryWrapper.WrapperLookup registryManager, RegistryKey<Registry<S>> registryKey) {
        if(targets == null || targets.isEmpty()) {
            addBoundEffect(cache, effect, id);
        } else {
            RegistryWrapper<S> wrapper = registryManager.getWrapperOrThrow(registryKey);
            targets.forEach(either -> indexGenericEither(cache, either, effect, id, wrapper));
        }
    }

    private static <S, T extends SkillEffect> void indexGenericEither(Map<Object, List<BoundEffect<?>>> cache, Either<RegistryKey<S>, TagKey<S>> either, T effect, Identifier id, RegistryWrapper<S> wrapper) {
        either.ifLeft(left ->
                wrapper.getOptional(left)
                        .ifPresent(entry -> addBoundEffect(cache, entry.value(), effect, id)))
                .ifRight(right ->
                        wrapper.getOptional(right)
                                .ifPresent(entryList ->
                                        entryList.forEach(entry -> addBoundEffect(cache, entry.value(), effect, id))));
    }
}
