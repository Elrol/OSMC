package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum TriState implements StringIdentifiable {
    TRUE("true"),
    NEUTRAL("neutral"),
    FALSE("false");

    private final String name;
    TriState(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }

    public static final Codec<TriState> CODEC = StringIdentifiable.createCodec(TriState::values);
}
