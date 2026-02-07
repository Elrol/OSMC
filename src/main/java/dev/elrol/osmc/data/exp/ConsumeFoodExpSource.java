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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class ConsumeFoodExpSource extends ExpSource {

    public static final MapCodec<ConsumeFoodExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("items").forGetter(ConsumeFoodExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        ConsumeFoodExpSource data = new ConsumeFoodExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    private final List<Either<RegistryKey<Item>, TagKey<Item>>> items = new ArrayList<>();

    public ConsumeFoodExpSource(int expGain) {
        super(expGain);
    }

    public List<Either<RegistryKey<Item>, TagKey<Item>>> getItems() { return items; }

    public void addItem(RegistryKey<Item> food) {
        items.add(Either.left(food));
    }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.CONSUME_FOOD_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.CONSUME);
    }
}
