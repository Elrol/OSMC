package dev.elrol.osmc.menus;

import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.libs.ModTranslations;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class _PageMenuBase5 extends _MenuBase {

    public int startingIndex;
    public int page;

    public _PageMenuBase5(ServerPlayerEntity player) {
        super(player, ScreenHandlerType.GENERIC_9X5);
        page = 0;
    }

    public _PageMenuBase5(ServerPlayerEntity player, int page) {
        super(player, ScreenHandlerType.GENERIC_9X5);
        this.page = page;
    }

    @Override
    protected void drawMenu() {
        startingIndex = this.page * 15;

        super.drawMenu();

        setSlot(36, GuiElementBuilder.from(new ItemStack(MenuUtils.leftArrow(page <= 0))).setCallback(this::previousPage).setName(ModTranslations.translate("osmc.menu.item.page.next").formatted(page < getLastPage() ? Formatting.RED : Formatting.DARK_GRAY)));
        setSlot(44, GuiElementBuilder.from(new ItemStack(MenuUtils.rightArrow(page >= getLastPage()))).setCallback(this::nextPage).setName(ModTranslations.translate("osmc.menu.item.page.next").formatted(page < getLastPage() ? Formatting.GREEN : Formatting.DARK_GRAY)));
    }

    private void nextPage() {
        if (page < getLastPage()) {
            click();
            page++;
            openNewMenu();
        }
    }

    private void previousPage() {
        if (page > 0) {
            click();
            page--;
            openNewMenu();
        }
    }

    protected <T> void drawItems(Map<String, T> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        for(int i = 0; i < 15; i++) {
            int itemIndex = i + startingIndex;
            if(itemIndex >= keys.size()) break;
            String key = keys.get(itemIndex);

            int x = i % 5;
            int y = (i - x) / 5;

            if(map.containsKey(key)) {
                int index = (y * 9) + x + 11;
                GuiElementBuilder element = createElement(key, map);
                menu.setSlot(index, element);
            }
        }
    }

    protected abstract <T> GuiElementBuilder createElement(String key, Map<String, T> map);

    public abstract int getLastPage();
}
