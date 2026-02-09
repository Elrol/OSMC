package dev.elrol.osmc.libs;

import net.minecraft.util.Identifier;

public class OSMCLootTracker {

    private static final ThreadLocal<Identifier> CURRENT_TABLE = new ThreadLocal<>();

    public static void set(Identifier id) {
        CURRENT_TABLE.set(id);
    }

    public static Identifier get() {
        return CURRENT_TABLE.get();
    }

    public static void clear() {
        CURRENT_TABLE.remove();
    }
}
