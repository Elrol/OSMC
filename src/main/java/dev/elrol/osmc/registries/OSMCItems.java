package dev.elrol.osmc.registries;

import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class OSMCItems {

    public static Item RIGHT_BUTTON_DISABLED;
    public static Item LEFT_BUTTON_DISABLED;
    public static Item RIGHT_BUTTON;
    public static Item LEFT_BUTTON;
    public static Item UP_BUTTON_DISABLED;
    public static Item DOWN_BUTTON_DISABLED;
    public static Item UP_BUTTON;
    public static Item DOWN_BUTTON;
    public static Item BACK_BUTTON;

    public static Item GRAY_BUTTON;
    public static Item GRAY_BUTTON_LEFT;
    public static Item GRAY_BUTTON_MIDDLE;
    public static Item GRAY_BUTTON_RIGHT;

    public static Item LIGHT_GRAY_BUTTON;
    public static Item LIGHT_GRAY_BUTTON_LEFT;
    public static Item LIGHT_GRAY_BUTTON_MIDDLE;
    public static Item LIGHT_GRAY_BUTTON_RIGHT;

    public static Item LIME_BUTTON;
    public static Item LIME_BUTTON_1;
    public static Item LIME_BUTTON_2;
    public static Item LIME_BUTTON_3;
    public static Item LIME_BUTTON_4;
    public static Item LIME_BUTTON_LEFT;
    public static Item LIME_BUTTON_MIDDLE;
    public static Item LIME_BUTTON_RIGHT;

    public static Item RED_BUTTON;
    public static Item RED_BUTTON_1;
    public static Item RED_BUTTON_2;
    public static Item RED_BUTTON_3;
    public static Item RED_BUTTON_4;
    public static Item RED_BUTTON_LEFT;
    public static Item RED_BUTTON_MIDDLE;
    public static Item RED_BUTTON_RIGHT;

    public static Item YELLOW_BUTTON;
    public static Item YELLOW_BUTTON_1;
    public static Item YELLOW_BUTTON_2;
    public static Item YELLOW_BUTTON_3;
    public static Item YELLOW_BUTTON_4;
    public static Item YELLOW_BUTTON_LEFT;
    public static Item YELLOW_BUTTON_MIDDLE;
    public static Item YELLOW_BUTTON_RIGHT;

    public static void register() {
        RIGHT_BUTTON_DISABLED   = get("gui_right_button_disabled");
        LEFT_BUTTON_DISABLED    = get("gui_left_button_disabled");
        RIGHT_BUTTON            = get("gui_right_button");
        LEFT_BUTTON             = get("gui_left_button");
        UP_BUTTON_DISABLED      = get("gui_up_button_disabled");
        DOWN_BUTTON_DISABLED    = get("gui_down_button_disabled");
        UP_BUTTON               = get("gui_up_button");
        DOWN_BUTTON             = get("gui_down_button");
        BACK_BUTTON             = get("gui_back_button");

        GRAY_BUTTON                 = get("gray_button");
        GRAY_BUTTON_LEFT            = get("gray_button_left");
        GRAY_BUTTON_MIDDLE          = get("gray_button_middle");
        GRAY_BUTTON_RIGHT           = get("gray_button_right");

        LIGHT_GRAY_BUTTON           = get("light_gray_button");
        LIGHT_GRAY_BUTTON_LEFT      = get("light_gray_button_left");
        LIGHT_GRAY_BUTTON_MIDDLE    = get("light_gray_button_middle");
        LIGHT_GRAY_BUTTON_RIGHT     = get("light_gray_button_right");

        LIME_BUTTON                 = get("lime_button");
        LIME_BUTTON_1               = get("lime_button_1");
        LIME_BUTTON_2               = get("lime_button_2");
        LIME_BUTTON_3               = get("lime_button_3");
        LIME_BUTTON_4               = get("lime_button_4");
        LIME_BUTTON_LEFT            = get("lime_button_left");
        LIME_BUTTON_MIDDLE          = get("lime_button_middle");
        LIME_BUTTON_RIGHT           = get("lime_button_right");

        RED_BUTTON                  = get("red_button");
        RED_BUTTON_1                = get("red_button_1");
        RED_BUTTON_2                = get("red_button_2");
        RED_BUTTON_3                = get("red_button_3");
        RED_BUTTON_4                = get("red_button_4");
        RED_BUTTON_LEFT             = get("red_button_left");
        RED_BUTTON_MIDDLE           = get("red_button_middle");
        RED_BUTTON_RIGHT            = get("red_button_right");

        YELLOW_BUTTON               = get("yellow_button");
        YELLOW_BUTTON_1             = get("yellow_button_1");
        YELLOW_BUTTON_2             = get("yellow_button_2");
        YELLOW_BUTTON_3             = get("yellow_button_3");
        YELLOW_BUTTON_4             = get("yellow_button_4");
        YELLOW_BUTTON_LEFT          = get("yellow_button_left");
        YELLOW_BUTTON_MIDDLE        = get("yellow_button_middle");
        YELLOW_BUTTON_RIGHT         = get("yellow_button_right");
    }

    private static Item get(String id) {
        return Registries.ITEM.get(OSMCConstants.osmcID(id));
    }
}
