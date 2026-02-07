package dev.elrol.osmc.data.effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.SkillEffectType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.libs.SkillUtils;
import dev.elrol.osmc.registries.OSMCSkillEffectTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatModifierSkillEffect extends SkillEffect {

    public static final MapCodec<StatModifierSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(StatModifierSkillEffect::getChanceFormula))
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("targets").forGetter(StatModifierSkillEffect::getTargets))
            .and(OSMCConstants.TARGET_BLOCK_CODEC.listOf().fieldOf("blocks").forGetter(StatModifierSkillEffect::getBlocks)
    ).apply(instance, (reqLevel, expGainFormula, chanceFormula, targets, blocks) -> {
        StatModifierSkillEffect data = new StatModifierSkillEffect(reqLevel, expGainFormula, chanceFormula);
        data.targets.addAll(targets);
        data.blocks.addAll(blocks);
        return data;
    }));

    private final String chanceFormula;
    private final List<Either<RegistryKey<Item>, TagKey<Item>>> targets = new ArrayList<>();
    private final List<Either<RegistryKey<Block>, TagKey<Block>>> blocks = new ArrayList<>();

    public StatModifierSkillEffect(int reqLevel, String expGainFormula, String chanceFormula) {
        super(reqLevel, expGainFormula);
        this.chanceFormula = chanceFormula;
    }

    public void addTarget(RegistryKey<Item> target) {
        targets.add(Either.left(target));
    }

    public void addTarget(TagKey<Item> target) {
        targets.add(Either.right(target));
    }

    public void addBlock(RegistryKey<Block> block) {
        blocks.add(Either.left(block));
    }

    public void addBlock(TagKey<Block> block) {
        blocks.add(Either.right(block));
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<Either<RegistryKey<Item>, TagKey<Item>>> getTargets() { return targets; }
    public List<Either<RegistryKey<Block>, TagKey<Block>>> getBlocks() { return blocks; }

    public float calculateChanceDrop(int skillLevel, int originalCount) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
                "level", (double) skillLevel,
                "count", (double) originalCount));
    }

    public boolean isValid(Block block) {
        return SkillUtils.isValid(block, getBlocks());
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.STAT_MODIFIER_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.PLAYER_TICK);
    }
}
