package dev.elrol.osmc.data.exp.cobblemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaptureExpSource extends ExpSource {

    public static final MapCodec<CaptureExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(CaptureExpSource::getExpFormula))
            .and(Species.getBY_IDENTIFIER_CODEC().listOf().fieldOf("species").forGetter(CaptureExpSource::getSpecies)
    ).apply(instance, (expGain, expFormula, species) -> {
        CaptureExpSource data = new CaptureExpSource(expGain);
        data.expFormula = expFormula;
        data.species.addAll(species);
        return data;
    }));

    private String expFormula = "(level + ball_modifier * (1 + shiny)) * (1 + legendary)";
    List<Species> species = new ArrayList<>();

    public CaptureExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public List<Species> getSpecies() { return species; }

    // ((level + ball_modifier + exp) * (1 + shiny)) * (1 + legendary)
    public double calculate(Pokemon pokemon, float ballModifier) {
        Map<String, Double> variables = Map.of(
                "xp", (double) getExpGain(),
                "level", (double) pokemon.getLevel(),
                "ball_modifier", (double) ballModifier,
                "shiny", (double) (pokemon.getShiny() ? 1 : 0),
                "legendary", (double) (pokemon.isLegendary() ? 1 : 0));
        return MathUtils.calculate(getExpFormula(), variables);
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.COBBLEMON_CAPTURE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    public void addSpecies(Species newSpecies) { species.add(newSpecies); }
}
