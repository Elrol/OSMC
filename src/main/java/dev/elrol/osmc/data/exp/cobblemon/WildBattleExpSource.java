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

public class WildBattleExpSource extends ExpSource {

    public static final MapCodec<WildBattleExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(WildBattleExpSource::getExpFormula))
            .and(OSMCConstants.TARGET_SPECIES_CODEC.listOf().fieldOf("targets").forGetter(WildBattleExpSource::getTargets)
    ).apply(instance, (expGain, expFormula, targets) -> {
        WildBattleExpSource data = new WildBattleExpSource(expGain);
        data.expFormula = expFormula;
                data.targets.addAll(targets);
        return data;
    }));

    private String expFormula = "xp";
    List<Either<Species, String>> targets = new ArrayList<>();

    public WildBattleExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public List<Either<Species, String>> getTargets() { return targets; }

    public int calculate(Pokemon pokemon) {
        return (int) MathUtils.calculate(getExpFormula(), getVariables(), pokemon);
    }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.COBBLEMON_WILD_BATTLE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.BATTLE_END);
    }
}
