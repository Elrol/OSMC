package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConsumeFoodExpSource extends ExpSource {

    public static final MapCodec<ConsumeFoodExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(ItemStack.CODEC.listOf().fieldOf("items").forGetter(ConsumeFoodExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        ConsumeFoodExpSource data = new ConsumeFoodExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    private final List<ItemStack> items = new ArrayList<>();

    public ConsumeFoodExpSource(int expGain) {
        super(expGain);
    }

    public boolean isValid(ItemStack stack) {
        return getItems().contains(stack);
    }

    public List<ItemStack> getItems() { return items; }

    public void addItem(ItemStack food) {
        items.add(food);
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.CONSUME_FOOD_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
