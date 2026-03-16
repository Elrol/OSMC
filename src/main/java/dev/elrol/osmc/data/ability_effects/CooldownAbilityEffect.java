package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.util.Identifier;

public class CooldownAbilityEffect extends AbilityEffect {

    public static final MapCodec<CooldownAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(Codec.INT.fieldOf("reduceSeconds").forGetter(CooldownAbilityEffect::getReduceSeconds))
    .apply(instance, (abilityEffectID, reqLevel, togglable, displayName, desc, extraSeconds) -> {
        CooldownAbilityEffect data = new CooldownAbilityEffect(abilityEffectID, reqLevel, togglable, extraSeconds);
        data.displayName = displayName;
        data.setDescription(desc);
        return data;
    }));

    final int reduceSeconds;

    public CooldownAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, int reduceSeconds) {
        super(abilityEffectID, reqLevel, togglable);
        this.reduceSeconds = reduceSeconds;
    }

    public CooldownAbilityEffect(Identifier abilityEffectID, int reqLevel, int reduceSeconds) {
        super(abilityEffectID, reqLevel);
        this.reduceSeconds = reduceSeconds;
    }

    public int getReduceSeconds() { return reduceSeconds; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.COOLDOWN_ABILITY_EFFECT;
    }
}
