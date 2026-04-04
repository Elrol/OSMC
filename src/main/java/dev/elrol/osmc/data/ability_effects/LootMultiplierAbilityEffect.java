package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootMultiplierAbilityEffect extends AbilityEffect {

    public static final MapCodec<LootMultiplierAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("chanceFormula").forGetter(LootMultiplierAbilityEffect::getChanceFormula))
            .and(Identifier.CODEC.listOf().fieldOf("lootTables").forGetter(LootMultiplierAbilityEffect::getLootTables))
            .apply(instance, (abilityEffectID, reqLevel, togglable, displayName, description, chanceFormula, lootTables) -> {
                LootMultiplierAbilityEffect data = new LootMultiplierAbilityEffect(abilityEffectID, reqLevel, togglable, displayName, description, chanceFormula);
                data.lootTables.addAll(lootTables);
                return data;
            }));

    private final String chanceFormula;
    private final List<Identifier> lootTables = new ArrayList<>();

    public LootMultiplierAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, Text displayName, Text description, String chanceFormula) {
        super(abilityEffectID, reqLevel, togglable, displayName, description);
        this.chanceFormula = chanceFormula;
    }

    public String getChanceFormula() { return chanceFormula; }
    public List<Identifier> getLootTables() { return lootTables; }

    public void addTable(Identifier tableID) {
        lootTables.add(tableID);
    }

    public float calculateChanceDrop(int skillLevel) {
        return (float) MathUtils.calculate(getChanceFormula(), Map.of(
                "level", (double) skillLevel));
    }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.LOOT_MULTIPLIER_ABILITY_EFFECT;
    }
}
