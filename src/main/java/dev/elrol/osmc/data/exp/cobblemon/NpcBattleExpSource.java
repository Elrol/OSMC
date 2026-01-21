package dev.elrol.osmc.data.exp.cobblemon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.village.TradeOffer;

import java.util.Map;

public class NpcBattleExpSource extends ExpSource {

    public static final MapCodec<NpcBattleExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(NpcBattleExpSource::getExpFormula)
    ).apply(instance, (expGain, expFormula) -> {
        NpcBattleExpSource data = new NpcBattleExpSource(expGain);
        data.expFormula = expFormula;
        return data;
    }));

    private String expFormula = "xp * 2";

    public NpcBattleExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public int calculate() {
        return (int) MathUtils.calculate(getExpFormula(), Map.of("xp", (double) getExpGain()));
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.COBBLEMON_NPC_BATTLE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
