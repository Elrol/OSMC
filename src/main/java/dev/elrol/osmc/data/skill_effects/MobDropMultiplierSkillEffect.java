package dev.elrol.osmc.data.skill_effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.SkillEffectType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCSkillEffectTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MobDropMultiplierSkillEffect extends SkillEffect {

    public static final MapCodec<MobDropMultiplierSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(MobDropMultiplierSkillEffect::getChanceFormula))
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("targets").forGetter(MobDropMultiplierSkillEffect::getTargets))
            .and(OSMCConstants.TARGET_ENTITY_TYPE_CODEC.listOf().fieldOf("entities").forGetter(MobDropMultiplierSkillEffect::getEntities)
    ).apply(instance, (reqLevel, expGainFormula, chanceFormula, targets, entities) -> {
        MobDropMultiplierSkillEffect data = new MobDropMultiplierSkillEffect(reqLevel, expGainFormula, chanceFormula);
        data.targets.addAll(targets);
        data.entities.addAll(entities);
        return data;
    }));

    private final String chanceFormula;
    private final List<Either<RegistryKey<Item>, TagKey<Item>>> targets = new ArrayList<>();
    private final List<Either<RegistryKey<EntityType<?>>, TagKey<EntityType<?>>>> entities = new ArrayList<>();

    public MobDropMultiplierSkillEffect(int reqLevel, String expGainFormula, String chanceFormula) {
        super(reqLevel, expGainFormula);
        this.chanceFormula = chanceFormula;
    }

    public void addTarget(Item target) {
        Registries.ITEM.getKey(target).ifPresent(key -> targets.add(Either.left(key)));
    }

    public void addTarget(TagKey<Item> target) {
        targets.add(Either.right(target));
    }

    public void addEntity(EntityType<?> type) {
        Registries.ENTITY_TYPE.getKey(type).ifPresent(key -> entities.add(Either.left(key)));
    }

    public void addEntity(Identifier id) {
        entities.add(Either.right(TagKey.of(RegistryKeys.ENTITY_TYPE, id)));
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<Either<RegistryKey<Item>, TagKey<Item>>> getTargets() { return targets; }
    public List<Either<RegistryKey<EntityType<?>>, TagKey<EntityType<?>>>> getEntities() { return entities; }

    public float calculateChanceDrop(int skillLevel, int originalCount) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
                "level", (double) skillLevel,
                "count", (double) originalCount));
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.MOB_DROP_MULTIPLIER_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.ENTITY_DROP);
    }
}
