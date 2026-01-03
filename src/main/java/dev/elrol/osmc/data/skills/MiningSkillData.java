package dev.elrol.osmc.data.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpGain;
import dev.elrol.osmc.registries.SkillDataTypeRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

public class MiningSkillData extends AbstractSkillData {

    public static final MapCodec<MiningSkillData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            AbstractSkillData.baseCodec(instance).and(
                    instance.group(
                            Codec.unboundedMap(Codec.STRING, ExpGain.CODEC).fieldOf("expGainMap").forGetter(MiningSkillData::getExpGainMap),
                            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("chanceDropMap").forGetter(MiningSkillData::getChanceDropMap)
                    )
            ).apply(instance, (enabled, id, expGainMap, chanceDropMap) -> {
                MiningSkillData data = new MiningSkillData();

                data.expGainMap.putAll(expGainMap);
                data.chanceDropMap.putAll(chanceDropMap);

                return data;
            }));


    Map<String, ExpGain> expGainMap = new LinkedHashMap<>();
    Map<String, Float> chanceDropMap = new LinkedHashMap<>();

    public MiningSkillData() {
        super("mining");
    }

    public Map<String, ExpGain> getExpGainMap() { return expGainMap; }
    public Map<String, Float> getChanceDropMap() { return chanceDropMap; }

    @Override
    public MapCodec<? extends AbstractSkillData> getCodec() {
        return CODEC;
    }

    @Override
    public SkillDataType<?> getType() {
        return SkillDataTypeRegistry.MINING_SKILL;
    }
}
