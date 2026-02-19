package dev.elrol.osmc.data;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CobblemonTier {
    public static final Codec<CobblemonTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TextCodecs.CODEC.fieldOf("name").forGetter(CobblemonTier::getName),
            Codec.INT.fieldOf("reqLevel").forGetter(CobblemonTier::getReqLevel),
            Codec.INT.fieldOf("minSpawnedLevel").forGetter(CobblemonTier::getMinSpawnedLevel),
            Codec.INT.fieldOf("maxSpawnedLevel").forGetter(CobblemonTier::getMaxSpawnedLevel),
            Codec.STRING.listOf().fieldOf("labels").forGetter(CobblemonTier::getLabels),
            Identifier.CODEC.listOf().fieldOf("species").forGetter(CobblemonTier::getSpecies)
    ).apply(instance, (name, reqLevel, minSpawnedLevel, maxSpawnedLevel, labels, species) -> {
        CobblemonTier data = new CobblemonTier();
        data.setReqLevel(reqLevel);
        data.setMinSpawnedLevel(minSpawnedLevel);
        data.setMaxSpawnedLevel(maxSpawnedLevel);
        data.labels.addAll(labels);
        data.species.addAll(species);
        return data;
    }));

    Text name = Text.empty();
    int reqLevel;
    int minSpawnedLevel;
    int maxSpawnedLevel;
    List<String> labels = new ArrayList<>();
    List<Identifier> species = new ArrayList<>();

    public void setName(Text name) { this.name = name; }
    public void setReqLevel(int reqLevel) { this.reqLevel = reqLevel; }
    public void setMinSpawnedLevel(int minSpawnedLevel) { this.minSpawnedLevel = minSpawnedLevel; }
    public void setMaxSpawnedLevel(int maxSpawnedLevel) { this.maxSpawnedLevel = maxSpawnedLevel; }

    public Text getName() { return name; }
    public int getReqLevel() { return reqLevel; }
    public int getMinSpawnedLevel() { return minSpawnedLevel; }
    public int getMaxSpawnedLevel() { return maxSpawnedLevel; }

    public List<String> getLabels() {
        return labels;
    }

    public List<Identifier> getSpecies() {
        return species;
    }

    public boolean isValid(Pokemon pokemon) {
        return (labels.isEmpty() || labels.stream().anyMatch(pokemon::hasLabels)) && (species.isEmpty() || species.stream().anyMatch(s -> pokemon.getSpecies().getResourceIdentifier().equals(s)));
    }
}
