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
import dev.elrol.osmc.registries.OSMCSkillEffectTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockDropExtraSkillEffect extends SkillEffect {

    public static final MapCodec<BlockDropExtraSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(BlockDropExtraSkillEffect::getChanceFormula))
            .and(RegistryKey.createCodec(RegistryKeys.ITEM).listOf().fieldOf("items").forGetter(BlockDropExtraSkillEffect::getItems))
            .and(OSMCConstants.TARGET_BLOCK_CODEC.listOf().fieldOf("targets").forGetter(BlockDropExtraSkillEffect::getTargets)
    ).apply(instance, (reqLevel, expGainFormula, chanceFormula, items, targets) -> {
        BlockDropExtraSkillEffect data = new BlockDropExtraSkillEffect(reqLevel, expGainFormula, chanceFormula);
        data.items.addAll(items);
        data.targets.addAll(targets);
        return data;
    }));

    private final String chanceFormula;
    private final List<RegistryKey<Item>> items = new ArrayList<>();
    private final List<Either<RegistryKey<Block>, TagKey<Block>>> targets = new ArrayList<>();

    public BlockDropExtraSkillEffect(int reqLevel, String expGainFormula, String chanceFormula) {
        super(reqLevel, expGainFormula);
        this.chanceFormula = chanceFormula;
    }

    public void addItem(RegistryKey<Item> target) {
        items.add(target);
    }

    public void addTarget(Block target) {
        Registries.BLOCK.getKey(target).ifPresent(key -> targets.add(Either.left(key)));
    }

    public void addTarget(TagKey<Block> block) {
        targets.add(Either.right(block));
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<RegistryKey<Item>> getItems() { return items; }
    public List<Either<RegistryKey<Block>, TagKey<Block>>> getTargets() { return targets; }

    public float calculateChanceDrop(int skillLevel, int originalCount) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
                "level", (double) skillLevel,
                "count", (double) originalCount));
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.BLOCK_DROP_EXTRA_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.BLOCK_DROP);
    }
}
