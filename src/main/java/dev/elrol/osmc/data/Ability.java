package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Ability {

    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("enabled").forGetter(Ability::isEnabled),
            Identifier.CODEC.fieldOf("id").forGetter(Ability::getID),
            AbilityEffect.CODEC.listOf().fieldOf("effects").forGetter(Ability::getEffects)
    ).apply(instance, (enabled, id, effects) -> {
        Ability data = new Ability(id);
        data.enabled = enabled;
        data.effects.addAll(effects);
        return data;
    }));

    protected boolean enabled = true;
    protected final Identifier id;
    protected List<AbilityEffect> effects = new ArrayList<>();

    public Ability(Identifier id) {
        this.id = id;
    }

    public void addAbilityEffect(AbilityEffect effect) {
        effects.add(effect);
    }

    public boolean isEnabled() { return enabled; }
    public Identifier getID() { return id; }
    public List<AbilityEffect> getEffects() { return effects; }
}
