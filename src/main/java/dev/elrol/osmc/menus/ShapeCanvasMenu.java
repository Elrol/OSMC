package dev.elrol.osmc.menus;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class ShapeCanvasMenu extends _MenuBase {

    public <T extends ScreenHandler> ShapeCanvasMenu(ServerPlayerEntity player) {
        super(player, ScreenHandlerType.GENERIC_9X6);
    }

    @Override
    public void openNewMenu() {

    }

    @Override
    public @NotNull String getMenuName() {
        return "";
    }

    @Override
    public char getMenuUnicode() {
        return 0;
    }
}
