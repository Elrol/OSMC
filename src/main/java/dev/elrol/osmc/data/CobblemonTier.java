package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class CobblemonTier {
    public static final Codec<CobblemonTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextCodecs.CODEC.fieldOf("name").forGetter(CobblemonTier::getName),
            Codec.INT.fieldOf("reqLevel").forGetter(CobblemonTier::getReqLevel),
            Codec.INT.fieldOf("minSpawnedLevel").forGetter(CobblemonTier::getMinSpawnedLevel),
            Codec.INT.fieldOf("maxSpawnedLevel").forGetter(CobblemonTier::getMaxSpawnedLevel)
    ).apply(instance, (name, reqLevel, minSpawnedLevel, maxSpawnedLevel) -> {
        CobblemonTier data = new CobblemonTier();
        data.setReqLevel(reqLevel);
        data.setMinSpawnedLevel(minSpawnedLevel);
        data.setMaxSpawnedLevel(maxSpawnedLevel);
        return data;
    }));

    Text name = Text.empty();
    int reqLevel;
    int minSpawnedLevel;
    int maxSpawnedLevel;

    public void setName(Text name) { this.name = name; }
    public void setReqLevel(int reqLevel) { this.reqLevel = reqLevel; }
    public void setMinSpawnedLevel(int minSpawnedLevel) { this.minSpawnedLevel = minSpawnedLevel; }
    public void setMaxSpawnedLevel(int maxSpawnedLevel) { this.maxSpawnedLevel = maxSpawnedLevel; }

    public Text getName() { return name; }
    public int getReqLevel() { return reqLevel; }
    public int getMinSpawnedLevel() { return minSpawnedLevel; }
    public int getMaxSpawnedLevel() { return maxSpawnedLevel; }
}
