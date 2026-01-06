package dev.elrol.osmc.data.skills;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.SkillDataTypeRegistry;

public class WoodcuttingSkill extends BlockBreakSkill {
    public static final MapCodec<WoodcuttingSkill> CODEC = RecordCodecBuilder.mapCodec(instance ->
            BlockBreakSkill.blockBreakCodec(instance).apply(instance, (enabled, id, levelFormula, expGainMap, chanceDropMap) -> {
                WoodcuttingSkill data = new WoodcuttingSkill();
                data.setEnabled(enabled);
                data.setLevelFormula(levelFormula);
                data.expGainMap.putAll(expGainMap);
                data.chanceDropMap.putAll(chanceDropMap);
                return data;
            }));

    public WoodcuttingSkill() {
        super(OSMCConstants.WOODCUTTING_ID);
    }

    @Override
    public MapCodec<? extends AbstractSkill> getCodec() {
        return CODEC;
    }

    @Override
    public void update() {

    }

    @Override
    public SkillDataType<?> getType() {
        return SkillDataTypeRegistry.WOODCUTTING_SKILL;
    }
}
