package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PotionBrewExpSource extends ExpSource {

    public static final MapCodec<PotionBrewExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(ItemStack.CODEC.listOf().fieldOf("ingredients").forGetter(PotionBrewExpSource::getIngredients)
    ).apply(instance, (expGain, ingredients) -> {
        PotionBrewExpSource data = new PotionBrewExpSource(expGain);
        data.ingredients.addAll(ingredients);
        return data;
    }));

    private final List<ItemStack> ingredients = new ArrayList<>();

    public PotionBrewExpSource(int expGain) {
        super(expGain);
    }

    public void addIngredient(ItemStack ingredient) {
        ingredients.add(ingredient);
    }

    public List<ItemStack> getIngredients() { return ingredients; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.POTION_BREW_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
