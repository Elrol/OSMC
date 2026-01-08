package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ConsumeFoodExpSource extends ExpSource {

    public static final MapCodec<ConsumeFoodExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(ConsumeFoodExpSource::getItems)
    ).apply(instance, (expGain, item) -> {
        ConsumeFoodExpSource data = new ConsumeFoodExpSource(expGain);
        data.item = item;
        return data;
    }));

    private List<ItemStack> item = new ArrayList<>();

    protected ConsumeFoodExpSource(int expGain) {
        super(expGain);
    }

    public boolean isValid(ItemStack stack) {
        return getItems().contains(stack);
    }

    public List<ItemStack> getItems() { return item; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.CONSUME_FOOD_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
