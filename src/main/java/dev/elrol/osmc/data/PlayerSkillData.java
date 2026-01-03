package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkillData {

    public static Codec<PlayerSkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("skillExp").forGetter(PlayerSkillData::getSKILL_EXP)
    ).apply(instance, (skillExp) -> {
        PlayerSkillData data = new PlayerSkillData();

        data.SKILL_EXP.putAll(skillExp);

        return data;
    }));

    private final Map<String, Long> SKILL_EXP = new HashMap<>();

    public Map<String, Long> getSKILL_EXP() { return SKILL_EXP; }
}
