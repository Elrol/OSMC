package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.util.Identifier;

public class DurationAbilityEffect extends AbilityEffect {

    public static final MapCodec<DurationAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(Codec.INT.fieldOf("extraSeconds").forGetter(DurationAbilityEffect::getExtraSeconds))
    .apply(instance, (abilityEffectID, reqLevel, togglable, displayName, desc, extraSeconds) -> {
        DurationAbilityEffect data = new DurationAbilityEffect(abilityEffectID, reqLevel, togglable, extraSeconds);
        data.displayName = displayName;
        data.setDescription(desc);
        return data;
    }));

    final int extraSeconds;

    public DurationAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, int extraSeconds) {
        super(abilityEffectID, reqLevel, togglable);
        this.extraSeconds = extraSeconds;
    }

    public DurationAbilityEffect(Identifier abilityEffectID, int reqLevel, int extraSeconds) {
        super(abilityEffectID, reqLevel);
        this.extraSeconds = extraSeconds;
    }

    public int getExtraSeconds() { return extraSeconds; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.DURATION_ABILITY_EFFECT;
    }
}
