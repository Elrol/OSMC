package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SkillSettingsData {
    public static final Codec<SkillSettingsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, Codec.BOOL).fieldOf("booleanMap").forGetter(SkillSettingsData::getBooleanMap),
            Grid.makeCodec(TriState.CODEC, () -> TriState.FALSE).fieldOf("shapeSettings").forGetter(SkillSettingsData::getShapeSettings),
            Codec.INT.fieldOf("shapePoints").forGetter(SkillSettingsData::getShapePoints)
    ).apply(instance, (booleanMap, shapeSettings, shapePoints) -> {
        SkillSettingsData data = new SkillSettingsData();
        data.booleanMap.putAll(booleanMap);
        data.shapeSettings = shapeSettings;
        data.shapePoints = shapePoints;
        return data;
    }));

    Map<Identifier, Boolean> booleanMap = new HashMap<>();
    Grid<TriState> shapeSettings = new Grid<>(() -> TriState.FALSE);
    int shapePoints = 0;

    public void toggleAbilityEffectSettings(Identifier abilityEffectID) {
        setAbilityEffectSetting(abilityEffectID, !getAbilityEffectSetting(abilityEffectID));
    }

    public boolean getAbilityEffectSetting(Identifier abilityEffectID) {
        return booleanMap.computeIfAbsent(abilityEffectID, id -> true);
    }

    public void setAbilityEffectSetting(Identifier abilityEffectID, boolean value) {
        booleanMap.put(abilityEffectID, value);
    }

    public void settShapePoints(int points) { shapePoints = points; }

    public Map<Identifier, Boolean> getBooleanMap() { return booleanMap; }
    public Grid<TriState> getShapeSettings() { return shapeSettings; }
    public void setShapeSettings(Grid<TriState> shapeSettings) { this.shapeSettings = shapeSettings; }
    public int getShapePoints() { return shapePoints; }
}
