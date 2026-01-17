package dev.elrol.osmc.data.exp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerTradeExpSource extends ExpSource {

    public static final MapCodec<VillagerTradeExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(VillagerTradeExpSource::getExpFormula))
            .and(ItemStack.CODEC.listOf().fieldOf("inputItems").forGetter(VillagerTradeExpSource::getInputItemStacks))
            .and(ItemStack.CODEC.listOf().fieldOf("outputItems").forGetter(VillagerTradeExpSource::getOutputItemStacks)
    ).apply(instance, (expGain, expFormula, inputItems, outputItems) -> {
        VillagerTradeExpSource data = new VillagerTradeExpSource(expGain);
        data.expFormula = expFormula;
        data.inputItems.addAll(inputItems);
        data.outputItems.addAll(outputItems);
        return data;
    }));

    private String expFormula = "2*one + 1*two + 3*three";
    private final List<ItemStack> inputItems = new ArrayList<>();
    private final List<ItemStack> outputItems = new ArrayList<>();

    public VillagerTradeExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }
    public List<ItemStack> getInputItemStacks() { return inputItems; }
    public List<ItemStack> getOutputItemStacks() { return outputItems; }

    public List<Item> getInputItems() { return inputItems.stream().map(ItemStack::getItem).toList(); }
    public List<Item> getOutputItems() { return outputItems.stream().map(ItemStack::getItem).toList(); }

    public void addInputItem(ItemStack item) {
        inputItems.add(item);
    }

    public void addOutputItem(ItemStack item) {
        outputItems.add(item);
    }

    public double calculate(TradeOffer trade) {
        int one = trade.getDisplayedFirstBuyItem().getCount();
        int two = trade.getDisplayedSecondBuyItem().getCount();
        int three = trade.getSellItem().getCount();

        return MathUtils.calculate(getExpFormula(), Map.of("one", (double) one, "two", (double) two, "three", (double) three));
    }

    public boolean isValid(TradeOffer trade) {
        Item firstItem = trade.getDisplayedFirstBuyItem().getItem();
        Item secondItem = trade.getDisplayedSecondBuyItem().getItem();
        Item sellItem = trade.getSellItem().getItem();

        List<Item> input = getInputItems();
        return (input.contains(firstItem) || input.contains(secondItem)) || getOutputItems().contains(sellItem);
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.VILLAGER_TRADE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
