package dev.elrol.osmc.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public interface BrewingStandExtended {
    void osmc$setBaseItems(DefaultedList<ItemStack> potions);
    List<ItemStack> osmc$getBaseItems();

}
