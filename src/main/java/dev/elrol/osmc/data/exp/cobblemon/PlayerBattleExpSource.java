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

public class PlayerBattleExpSource extends ExpSource {

    public static final MapCodec<PlayerBattleExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(PlayerBattleExpSource::getExpFormula)
    ).apply(instance, (expGain, expFormula) -> {
        PlayerBattleExpSource data = new PlayerBattleExpSource(expGain);
        data.expFormula = expFormula;
        return data;
    }));

    private String expFormula = "xp * 3";

    public PlayerBattleExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }

    public int calculate() {
        return (int) MathUtils.calculate(getExpFormula(), Map.of("xp", (double) getExpGain()));
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.COBBLEMON_PLAYER_BATTLE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
