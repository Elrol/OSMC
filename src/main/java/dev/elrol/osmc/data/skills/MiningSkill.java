package dev.elrol.osmc.data.skills;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.SkillDataTypeRegistry;

public class MiningSkill extends BlockBreakSkill {
    public static final MapCodec<MiningSkill> CODEC = RecordCodecBuilder.mapCodec(instance ->
            BlockBreakSkill.blockBreakCodec(instance).apply(instance, (enabled, id, levelFormula, expGainMap, chanceDropMap) -> {
                MiningSkill data = new MiningSkill();
                data.setEnabled(enabled);
                data.expGainMap.putAll(expGainMap);
                data.chanceDropMap.putAll(chanceDropMap);
                return data;
            }));

    public MiningSkill() {
        super(OSMCConstants.MINING_ID);
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
        return SkillDataTypeRegistry.MINING_SKILL;
    }
}
