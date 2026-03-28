package dev.elrol.osmc.menus;

import dev.elrol.osmc.libs.MenuUtils;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class DevMenu extends _MenuBase {

    public <T extends ScreenHandler> DevMenu(ServerPlayerEntity player) {
        super(player, ScreenHandlerType.GENERIC_9X5);
    }

    @Override
    protected void drawMenu() {
        super.drawMenu();

        setSlot(22, MenuUtils.item(Items.DIAMOND, 1, Text.literal("Test Item").formatted(Formatting.GREEN)));

    }

    @Override
    public void openNewMenu() {
        new DevMenu(player).open();
    }

    @Override
    public @NotNull String getMenuName() {
        return "dev";
    }

    @Override
    public char getMenuChar() {
        return 'b';
    }
}
