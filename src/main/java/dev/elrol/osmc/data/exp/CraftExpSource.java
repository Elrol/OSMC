package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftExpSource extends ExpSource {

    public static final MapCodec<CraftExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(ItemStack.CODEC.listOf().fieldOf("items").forGetter(CraftExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        CraftExpSource data = new CraftExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    protected CraftExpSource(int expGain) {
        super(expGain);
    }

    List<ItemStack> items = new ArrayList<>();

    public List<ItemStack> getItems() { return items; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.CRAFT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
