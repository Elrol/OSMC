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

public class LootRollSkillEffect extends SkillEffect {

    public static final MapCodec<LootRollSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(LootRollSkillEffect::getChanceFormula))
            .and(Identifier.CODEC.listOf().fieldOf("lootTables").forGetter(LootRollSkillEffect::getLootTables)
            ).apply(instance, (reqLevel, expGainFormula, chanceFormula, lootTables) -> {
                LootRollSkillEffect data = new LootRollSkillEffect(reqLevel, expGainFormula, chanceFormula);
                data.lootTables.addAll(lootTables);
                return data;
            }));

    private final String chanceFormula;
    private final List<Identifier> lootTables = new ArrayList<>();

    public LootRollSkillEffect(int reqLevel, String expGainFormula, String chanceFormula) {
        super(reqLevel, expGainFormula);
        this.chanceFormula = chanceFormula;
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<Identifier> getLootTables() { return lootTables; }

    public void addTable(Identifier tableID) {
        lootTables.add(tableID);
    }

    public float calculateChanceDrop(int skillLevel) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
                "level", (double) skillLevel));
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.LOOT_ROLL_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.LOOT_ROLL);
    }
}
