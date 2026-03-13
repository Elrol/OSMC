package dev.elrol.osmc.libs;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModTranslations {

    public static MutableText removeItalic(Text text) {
        return MutableText.of(text.getContent()).setStyle(text.getStyle().withItalic(false));
    }

    public static MutableText literal(String string) {
        return removeItalic(Text.literal(string));
    }

    public static MutableText translate(String node, Object... args) {
        return removeItalic(Text.translatable(node, args));
    }

    public static MutableText translate(String node) {
        return removeItalic(Text.translatable(node));
    }

    public static MutableText translateFallback(String node, String fallback) {
        return removeItalic(Text.translatableWithFallback(node, fallback));
    }

    public static MutableText translateFallback(String node, String fallback, Object... args) {
        return removeItalic(Text.translatableWithFallback(node, fallback, args));
    }

    public static MutableText msg(String node) {
        return translate("arrow.message." + node).formatted(Formatting.GREEN);
    }

    public static MutableText msg(String node, Object... args) {
        return translate("arrow.message." + node, args).formatted(Formatting.GREEN);
    }

    public static MutableText msgFallback(String node, String fallback) {
        return translateFallback("arrow.message." + node, fallback).formatted(Formatting.GREEN);
    }

    public static MutableText msgFallback(String node, String fallback, Object... args) {
        return translateFallback("arrow.message." + node, fallback, args).formatted(Formatting.GREEN);
    }

    public static MutableText info(String node) {
        return translate("arrow.info." + node);
    }

    public static MutableText info(String node, Object... args) {
        return translate("arrow.info." + node, args);
    }

    public static MutableText infoFallback(String node, String fallback) {
        return translateFallback("arrow.info." + node, fallback);
    }

    public static MutableText infoFallback(String node, String fallback, Object... args) {
        return translateFallback("arrow.info." + node, fallback, args);
    }

    public static MutableText warn(String node) {
        return translate("arrow.warn." + node);
    }

    public static MutableText warn(String node, Object... args) {
        return translate("arrow.warn." + node, args);
    }

    public static MutableText warnFallback(String node, String fallback) {
        return translateFallback("arrow.warn." + node, fallback);
    }

    public static MutableText warnFallback(String node, String fallback, Object... args) {
        return translateFallback("arrow.warn." + node, fallback, args);
    }

    public static MutableText err(String node) {
        return translate("arrow.error." + node).formatted(Formatting.RED);
    }

    public static MutableText err(String node, Object... args) {
        return translate("arrow.error." + node, args).formatted(Formatting.RED);
    }

    public static MutableText errFallback(String node, String fallback) {
        return translateFallback("arrow.error." + node, fallback).formatted(Formatting.RED);
    }

    public static MutableText errFallback(String node, String fallback, Object... args) {
        return translateFallback("arrow.error." + node, fallback, args).formatted(Formatting.RED);
    }

}
