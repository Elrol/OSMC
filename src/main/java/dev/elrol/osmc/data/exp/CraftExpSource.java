package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class CraftExpSource extends ExpSource {

    public static final MapCodec<CraftExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(OSMCConstants.TARGET_ITEM_CODEC.listOf().fieldOf("items").forGetter(CraftExpSource::getItems)
    ).apply(instance, (expGain, items) -> {
        CraftExpSource data = new CraftExpSource(expGain);
        data.items.addAll(items);
        return data;
    }));

    public CraftExpSource(int expGain) {
        super(expGain);
    }

    List<Either<RegistryKey<Item>, TagKey<Item>>> items = new ArrayList<>();

    public List<Either<RegistryKey<Item>, TagKey<Item>>> getItems() { return items; }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.CRAFT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    public void addItem(Item item) {
        Registries.ITEM.getKey(item).ifPresent(key -> items.add(Either.left(key)));
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.CRAFTED);
    }
}
