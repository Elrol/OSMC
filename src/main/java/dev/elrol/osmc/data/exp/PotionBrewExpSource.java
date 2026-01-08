package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class PotionBrewExpSource extends ExpSource {

    public static final MapCodec<PotionBrewExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            Registries.POTION.getEntryCodec().listOf().fieldOf("potions").forGetter(PotionBrewExpSource::getPotions)
    ).apply(instance, (expGain, potions) -> {
        PotionBrewExpSource data = new PotionBrewExpSource(expGain);
        data.potions.addAll(potions);
        return data;
    }));

    private final List<RegistryEntry<Potion>> potions = new ArrayList<>();

    protected PotionBrewExpSource(int expGain) {
        super(expGain);
    }

    public List<RegistryEntry<Potion>> getPotions() { return potions; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.POTION_BREW_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
