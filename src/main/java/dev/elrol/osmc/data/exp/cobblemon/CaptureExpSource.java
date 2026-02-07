package dev.elrol.osmc.data.exp.cobblemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CaptureExpSource extends ExpSource {

    public static final MapCodec<CaptureExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(CaptureExpSource::getExpFormula))
            .and(OSMCConstants.TARGET_SPECIES_CODEC.listOf().fieldOf("targets").forGetter(CaptureExpSource::getTargets)
    ).apply(instance, (expGain, expFormula, targets) -> {
        CaptureExpSource data = new CaptureExpSource(expGain);
        data.expFormula = expFormula;
        data.targets.addAll(targets);
        return data;
    }));

    private String expFormula = "(p_level + ball_modifier * (1 + p_shiny)) * (1 + p_legendary)";
    List<Either<Species, String>> targets = new ArrayList<>();

    public CaptureExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public List<Either<Species, String>> getTargets() { return targets; }

    // (p_level + ball_modifier * (1 + p_shiny)) * (1 + p_legendary)
    public double calculate(Pokemon pokemon, float ballModifier) {
        Map<String, Double> variables = Map.of(
                "xp", (double) getExpGain(),
                "ball_modifier", (double) ballModifier);
        return MathUtils.calculate(getExpFormula(), variables, pokemon);
    }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.COBBLEMON_CAPTURE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.CAPTURE);
    }

    public void addSpecies(Species newSpecies) { targets.add(Either.left(newSpecies)); }
}
