package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConsumePotionExpSource extends ExpSource {

    public static final MapCodec<ConsumePotionExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(ConsumePotionExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        ConsumePotionExpSource data = new ConsumePotionExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    private final List<ItemStack> items = new ArrayList<>();

    protected ConsumePotionExpSource(int expGain) {
        super(expGain);
    }

    public boolean isValid(ItemStack stack) {
        return getItems().contains(stack);
    }

    public List<ItemStack> getItems() { return items; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.CONSUME_POTION_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
