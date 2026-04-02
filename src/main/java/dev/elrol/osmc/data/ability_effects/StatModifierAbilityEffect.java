package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class StatModifierAbilityEffect extends AbilityEffect {

    public static final MapCodec<StatModifierAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("formula").forGetter(StatModifierAbilityEffect::getFormula))
            .and(Identifier.CODEC.fieldOf("attribute").forGetter(StatModifierAbilityEffect::getAttribute))
            .and(EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(StatModifierAbilityEffect::getOperation)
    ).apply(instance, StatModifierAbilityEffect::new));

    private final String formula;
    private final Identifier attribute;
    private final EntityAttributeModifier.Operation operation;

    public StatModifierAbilityEffect(Identifier abilityEffectID, int reqLevel, String formula, Identifier attribute, EntityAttributeModifier.Operation operation) {
        super(abilityEffectID, reqLevel);
        this.formula = formula;
        this.attribute = attribute;
        this.operation = operation;
    }

    public StatModifierAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, Text displayName, Text description, String formula, Identifier attribute, EntityAttributeModifier.Operation operation) {
        super(abilityEffectID, reqLevel, togglable, displayName, description);
        this.formula = formula;
        this.attribute = attribute;
        this.operation = operation;
    }

    public void apply(ServerPlayerEntity player, int level) {
        EntityAttributeInstance instance = player.getAttributeInstance(getAttributeEntry());

        if(instance != null) {
            double value = MathUtils.calculate(getFormula(), Map.of("level", (double) level));
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    getAbilityEffectID(),
                    value,
                    operation
            );
            instance.addTemporaryModifier(modifier);
            OSMC.LOGGER.debug("Stat Modifier Ability Effect Applied: {}", getAbilityEffectID());
        }
    }

    public void remove(ServerPlayerEntity player) {
        EntityAttributeInstance instance = player.getAttributeInstance(getAttributeEntry());
        if(instance != null)
            if(instance.removeModifier(getAbilityEffectID())) {
                OSMC.LOGGER.debug("Stat Modifier Ability Effect Removed: {}", getAbilityEffectID());
            } else {
                OSMC.LOGGER.error("Stat Modifier Ability Effect Not Found: {}", getAbilityEffectID());
            }
    }

    public String getFormula() { return formula; }
    public Identifier getAttribute() { return attribute; }
    public RegistryEntry<EntityAttribute> getAttributeEntry() { return Registries.ATTRIBUTE.getEntry(getAttribute()).orElseThrow(); }
    public EntityAttributeModifier.Operation getOperation() { return operation; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.STAT_MODIFIER_ABILITY_EFFECT;
    }
}
