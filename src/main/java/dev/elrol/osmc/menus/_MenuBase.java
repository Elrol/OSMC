package dev.elrol.osmc.menus;

import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.libs.ModTranslations;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCItems;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class _MenuBase {
    public ServerPlayerEntity player;
    public PlayerSkillData data;
    public _ModMenu menu;

    public <T extends ScreenHandler> _MenuBase(ServerPlayerEntity player, ScreenHandlerType<T> type) {
        this.player = player;
        updateData();
        this.menu = new _ModMenu(getMenuName(), type, player, false);

        Style style = Style.EMPTY.withFont(getMenuFont());

        this.menu.setTitle(Text.literal("aaaaaaaa" + getMenuChar()).setStyle(style).formatted(Formatting.WHITE));
        this.menu.setTickCallback(this::onTick);
    }

    public void open() {
        drawMenu();
        menu.open();
    }

    public void close() {
        menu.close();
    }

    protected void drawMenu() {
        setSlot(8, backButton());
    }

    protected GuiElementBuilder backButton() {
        return MenuUtils.item(OSMCItems.BACK_BUTTON, 1, ModTranslations.translate("osmc.menu.item.name.close").formatted(Formatting.RED, Formatting.BOLD)).setCallback((index, clickType, slotActionType, slotGuiInterface) -> {
            click();
            back();
        });
    }

    protected void back() {
        close();
    }

    public void setSlot(int index, GuiElementBuilder element, int dataOffset) {
        setSlot(index, element.build());
    }

    public void setSlot(int index, GuiElementBuilder element) {
        if(element == null) return;
        setSlot(index, element.build());
    }

    public void setSlot(int index, GuiElement element) {
        if(element == null) return;
        menu.setSlot(index, element);
    }

    protected void click() {
        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1.0f, 1.0f);
    }

    protected void updateData() {
        this.data = OSMCPlayerDataRegistry.get(player.getUuid());
    }

    protected void onTick() {}

    public abstract void openNewMenu();

    @NotNull
    public abstract String getMenuName();

    public abstract char getMenuChar();

    @NotNull
    public Identifier getMenuFont() {
        return OSMCConstants.osmcID("menu_font");
    }
}
