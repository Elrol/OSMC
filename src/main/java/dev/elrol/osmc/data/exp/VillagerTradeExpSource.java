package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VillagerTradeExpSource extends ExpSource {

    public static final MapCodec<VillagerTradeExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(VillagerTradeExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        VillagerTradeExpSource data = new VillagerTradeExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    private final List<ItemStack> items = new ArrayList<>();

    protected VillagerTradeExpSource(int expGain) {
        super(expGain);
    }

    public List<ItemStack> getItems() { return items; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.VILLAGER_TRADE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
