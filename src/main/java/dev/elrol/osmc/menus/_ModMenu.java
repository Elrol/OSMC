package dev.elrol.osmc.menus;

import dev.elrol.osmc.events.MenuCloseCallback;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class _ModMenu extends SimpleGui {

    private Runnable tickCallback;
    public final String menuName;
    boolean canClose = true;

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param type                  the screen handler that the client should display
     * @param player                the player to server this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public _ModMenu(String menuName, ScreenHandlerType<?> type, ServerPlayerEntity player, boolean manipulatePlayerSlots) {
        super(type, player, manipulatePlayerSlots);
        this.menuName = menuName;
    }

    @Override
    public void onClose() {
        MenuCloseCallback.EVENT.invoker().onClose(this);
        super.onClose();
    }

    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }

    @Override
    public boolean canPlayerClose() {
        return canClose;
    }

    @Override
    public void onTick() {
        super.onTick();
        if(tickCallback != null) {
            tickCallback.run();
        }
    }

    public void setTickCallback(Runnable callback) {
        tickCallback = callback;
    }
}
