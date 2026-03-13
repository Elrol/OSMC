package dev.elrol.osmc.events;

import dev.elrol.osmc.menus._ModMenu;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MenuCloseCallback {
    Event<MenuCloseCallback> EVENT = EventFactory.createArrayBacked(MenuCloseCallback.class, listeners -> (menu) -> {
        for (MenuCloseCallback listener : listeners) {
            listener.onClose(menu);
        }
    });

    void onClose(_ModMenu menu);

}
