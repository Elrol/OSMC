package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class PotionBrewExpSource extends ExpSource {

    public static final MapCodec<PotionBrewExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("ingredients").forGetter(PotionBrewExpSource::getIngredients)
    ).apply(instance, (expGain, ingredients) -> {
        PotionBrewExpSource data = new PotionBrewExpSource(expGain);
        data.ingredients.addAll(ingredients);
        return data;
    }));

    private final List<Either<RegistryKey<Item>, TagKey<Item>>> ingredients = new ArrayList<>();

    public PotionBrewExpSource(int expGain) {
        super(expGain);
    }

    public void addIngredient(Item item) {
        Registries.ITEM.getKey(item).ifPresent(key -> ingredients.add(Either.left(key)));
    }

    public List<Either<RegistryKey<Item>, TagKey<Item>>> getIngredients() { return ingredients; }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.POTION_BREW_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.BREWED);
    }
}
