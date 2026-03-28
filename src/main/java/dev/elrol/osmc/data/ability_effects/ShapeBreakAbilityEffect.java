package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.util.Identifier;

public class ShapeBreakAbilityEffect extends AbilityEffect {

    public static final MapCodec<ShapeBreakAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(Codec.INT.fieldOf("extraBlocks").forGetter(ShapeBreakAbilityEffect::getExtraBlocks))
            .apply(instance, (abilityEffectID, reqLevel, togglable, displayName, desc, extraBlocks) -> {
                ShapeBreakAbilityEffect data = new ShapeBreakAbilityEffect(abilityEffectID, reqLevel, togglable, extraBlocks);
                data.displayName = displayName;
                data.setDescription(desc);
                return data;
            }));

    final int extraBlocks;

    public ShapeBreakAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, int extraBlocks) {
        super(abilityEffectID, reqLevel, togglable);
        this.extraBlocks = extraBlocks;
    }

    public ShapeBreakAbilityEffect(Identifier abilityEffectID, int reqLevel, int extraBlocks) {
        super(abilityEffectID, reqLevel);
        this.extraBlocks = extraBlocks;
    }

    public int getExtraBlocks() { return extraBlocks; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.SHAPE_BREAK_ABILITY_EFFECT;
    }
}
