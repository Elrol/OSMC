package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.village.TradeOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class VillagerTradeExpSource extends ExpSource {

    public static final MapCodec<VillagerTradeExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Codec.STRING.fieldOf("expFormula").forGetter(VillagerTradeExpSource::getExpFormula))
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("inputItems").forGetter(VillagerTradeExpSource::getInputItemStacks))
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("outputItems").forGetter(VillagerTradeExpSource::getOutputItemStacks)
    ).apply(instance, (expGain, expFormula, inputItems, outputItems) -> {
        VillagerTradeExpSource data = new VillagerTradeExpSource(expGain);
        data.expFormula = expFormula;
        data.inputItems.addAll(inputItems);
        data.outputItems.addAll(outputItems);
        return data;
    }));

    private String expFormula = "xp * (2*one + 1*two + 3*three)";
    private final List<Either<RegistryKey<Item>, TagKey<Item>>> inputItems = new ArrayList<>();
    private final List<Either<RegistryKey<Item>, TagKey<Item>>> outputItems = new ArrayList<>();

    public VillagerTradeExpSource(int expGain) {
        super(expGain);
    }

    public String getExpFormula() { return expFormula; }
    public List<Either<RegistryKey<Item>, TagKey<Item>>> getInputItemStacks() { return inputItems; }
    public List<Either<RegistryKey<Item>, TagKey<Item>>> getOutputItemStacks() { return outputItems; }

    public List<Item> getInputItems() {
        return inputItems.stream().flatMap(
            either -> either.map(
                    key -> Stream.of(Registries.ITEM.get(key)),
                    tagKey -> Registries.ITEM.getOrCreateEntryList(tagKey)
                            .stream().map(RegistryEntry::value)
            )).toList();
    }
    public List<Item> getOutputItems() {
        return outputItems.stream().flatMap(
                either -> either.map(
                        key -> Stream.of(Registries.ITEM.get(key)),
                        tagKey -> Registries.ITEM.getOrCreateEntryList(tagKey)
                                .stream().map(RegistryEntry::value)
                )).toList();
    }

    public void addInputItem(Item item) {
        Registries.ITEM.getKey(item).ifPresent(key -> inputItems.add(Either.left(key)));
    }

    public void addOutputItem(Item item) {
        Registries.ITEM.getKey(item).ifPresent(key -> outputItems.add(Either.left(key)));
    }

    public double calculate(TradeOffer trade) {
        int one = trade.getDisplayedFirstBuyItem().getCount();
        int two = trade.getDisplayedSecondBuyItem().getCount();
        int three = trade.getSellItem().getCount();

        return MathUtils.calculate(getExpFormula(), Map.of(
                "one", (double) one,
                "two", (double) two,
                "three", (double) three,
                "xp", (double) getExpGain()));
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
        return OSMCExpSourceTypeRegistry.VILLAGER_TRADE_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.TRADE);
    }
}
