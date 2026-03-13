package dev.elrol.osmc.libs;

import dev.elrol.osmc.registries.OSMCItems;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class MenuUtils {

    public static GuiElementBuilder item(Item item, int count, String name){
        return item(item, count, ModTranslations.translate("arrow.menu.item.name." + name));
    }

    public static GuiElementBuilder itemWithLore(Item item, int count, String name, List<Text> lore){
        return item(item, count, name).setLore(lore);
    }

    public static GuiElementBuilder item(Item item, int count, Text name){
        return new GuiElementBuilder(item).setCount(count).setName(name);
    }

    public static GuiElementBuilder itemStack(ItemStack stack, String name) {
        return itemStack(stack, Text.literal(name));
    }

    public static GuiElementBuilder itemStack(ItemStack stack, Text name) {
        return new GuiElementBuilder(stack).setName(name);
    }

    public static GuiElementBuilder itemGlow(Item item, int count, Text name, boolean glow){
        return new GuiElementBuilder(item).setCount(count).setName(name).glow(glow);
    }

    public static GuiElementBuilder itemWithLore(Item item, int count, Text name, List<Text> lore){
        return item(item, count, name).setLore(lore);
    }

    public static GuiElementBuilder getCurrentItemState(Text name, Item unselected, Item selected, int current, int target) {
        return MenuUtils.item(target == current ? selected : unselected, 1, name);
    }

    public static Item leftArrow(boolean disabled) {
        return disabled ? OSMCItems.LEFT_BUTTON_DISABLED : OSMCItems.LEFT_BUTTON;
    }

    public static Item rightArrow(boolean disabled) {
        return disabled ? OSMCItems.RIGHT_BUTTON_DISABLED : OSMCItems.RIGHT_BUTTON;
    }
}