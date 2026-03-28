package dev.elrol.osmc.data.ability_effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChainBreakAbilityEffect extends AbilityEffect {

    public static MapCodec<ChainBreakAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(OSMCConstants.TARGET_BLOCK_CODEC.listOf().fieldOf("targets").forGetter(ChainBreakAbilityEffect::getTargets))
            .and(Codec.STRING.fieldOf("blockCountFormula").forGetter(ChainBreakAbilityEffect::getFormula)
            ).apply(instance, (abilityEffectID, reqLevel, togglable, displayName, desc, targets, blockCountFormula) -> {
                ChainBreakAbilityEffect data = new ChainBreakAbilityEffect(abilityEffectID, reqLevel, togglable, blockCountFormula);

                data.displayName = displayName;
                data.setDescription(desc);
                data.targets.addAll(targets);

                return data;
            }));

    private final List<Either<RegistryKey<Block>, TagKey<Block>>> targets = new ArrayList<>();
    private final String blockCountFormula;

    public ChainBreakAbilityEffect(Identifier abilityEffectID, int reqLevel, String blockCountFormula) {
        this(abilityEffectID, reqLevel, false, blockCountFormula);
    }

    public ChainBreakAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, String blockCountFormula) {
        super(abilityEffectID, reqLevel, togglable);
        this.blockCountFormula = blockCountFormula;
    }

    public void addTarget(RegistryKey<Block> target) {
        this.targets.add(Either.left(target));
    }

    public void addTarget(Identifier tag) {
        targets.add(Either.right(TagKey.of(RegistryKeys.BLOCK, tag)));
    }

    public double calculate(double level) {
        Map<String, Double> variables = new HashMap<>();
        variables.put("level", level);

        return MathUtils.calculate(getFormula(), variables);
    }

    public boolean isValid(BlockState state) {
        return targets.stream().anyMatch(either -> either.map((key) -> state.isOf(Registries.BLOCK.entryOf(key)), state::isIn));
    }


    public List<Either<RegistryKey<Block>, TagKey<Block>>> getTargets() { return targets; }

    public String getFormula() { return blockCountFormula; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.CHAIN_BREAK_ABILITY_EFFECT;
    }
}
