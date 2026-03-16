package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SkillSettingsData {
    public static final Codec<SkillSettingsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, Codec.BOOL).fieldOf("booleanMap").forGetter(SkillSettingsData::getBooleanMap)
    ).apply(instance, (booleanMap) -> {
        SkillSettingsData data = new SkillSettingsData();
        data.booleanMap.putAll(booleanMap);
        return data;
    }));

    Map<Identifier, Boolean> booleanMap = new HashMap<>();

    public void toggleAbilityEffectSettings(Identifier abilityEffectID) {
        setAbilityEffectSetting(abilityEffectID, !getAbilityEffectSetting(abilityEffectID));
    }

    public boolean getAbilityEffectSetting(Identifier abilityEffectID) {
        return booleanMap.computeIfAbsent(abilityEffectID, id -> true);
    }

    public void setAbilityEffectSetting(Identifier abilityEffectID, boolean value) {
        booleanMap.put(abilityEffectID, value);
    }

    public Map<Identifier, Boolean> getBooleanMap() { return booleanMap; }
}
