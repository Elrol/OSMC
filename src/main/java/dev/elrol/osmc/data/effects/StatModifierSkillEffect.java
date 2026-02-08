package dev.elrol.osmc.data.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.SkillEffectType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCSkillEffectTypeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class StatModifierSkillEffect extends SkillEffect {

    public static final MapCodec<StatModifierSkillEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> SkillEffect.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("formula").forGetter(StatModifierSkillEffect::getFormula))
            .and(Identifier.CODEC.fieldOf("attribute").forGetter(StatModifierSkillEffect::getAttribute))
            .and(EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(StatModifierSkillEffect::getOperation)
    ).apply(instance, StatModifierSkillEffect::new));

    private final String formula;
    private final Identifier attribute;
    private final EntityAttributeModifier.Operation operation;

    public StatModifierSkillEffect(int reqLevel, String expGainFormula, String formula, Identifier attribute, EntityAttributeModifier.Operation operation) {
        super(reqLevel, expGainFormula);
        this.formula = formula;
        this.attribute = attribute;
        this.operation = operation;
    }

    public String getFormula() { return formula; }
    public Identifier getAttribute() { return attribute; }
    public EntityAttributeModifier.Operation getOperation() { return operation; }
    public RegistryEntry<EntityAttribute> getAttributeEntry() {
        return Registries.ATTRIBUTE.getEntry(getAttribute()).orElseThrow();
    }

    public void updateAttribute(ServerPlayerEntity player, Identifier skillID, int level) {
        EntityAttributeInstance instance = player.getAttributeInstance(getAttributeEntry());
        if(instance != null) {
            Identifier modifierID = OSMCConstants.osmcID("stat_boost_" + skillID.getPath());
            instance.removeModifier(modifierID);

            if(level >= getReqLevel()) {
                double value = MathUtils.calculate(getFormula(), Map.of("level", (double) level));
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        modifierID,
                        value,
                        operation
                );
                instance.addTemporaryModifier(modifier);
            }
        }
    }

    @Override
    public SkillEffectType<?> getType() {
        return OSMCSkillEffectTypeRegistry.STAT_MODIFIER_SKILL_EFFECT;
    }

    @Override
    public MapCodec<? extends SkillEffect> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.LEVEL_UP, SkillTrigger.LOGIN);
    }
}
