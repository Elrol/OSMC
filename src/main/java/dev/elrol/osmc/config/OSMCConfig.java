package dev.elrol.osmc.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;

public class OSMCConfig {
    private static final String FILENAME = "config.json";

    public static final Codec<OSMCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isDebug").forGetter(data -> data.isDebug),
            Codec.INT.fieldOf("maxLevel").forGetter(data -> data.maxLevel)
    ).apply(instance, (isDebug, maxLevel) -> {
        OSMCConfig data = new OSMCConfig();

        data.isDebug = isDebug;
        data.maxLevel = maxLevel;

        return data;
    }));

    public boolean isDebug = false;
    public int maxLevel = 99;

    public void save() {
        DataResult<JsonElement> jsonResult = CODEC.encodeStart(JsonOps.INSTANCE, this);
        jsonResult.ifError(err -> OSMC.LOGGER.error(err.message()))
                .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.CONFIG_DIR, FILENAME, json));
    }

    public OSMCConfig load() {
        JsonElement json = JsonUtils.loadFromJson(OSMCConstants.CONFIG_DIR, FILENAME, JsonParser.parseString("{}"));
        DataResult<Pair<OSMCConfig, JsonElement>> configPair = CODEC.decode(JsonOps.INSTANCE, json);
        if(configPair.isSuccess()) {
            return configPair.getOrThrow().getFirst();
        } else {
            save();
            return this;
        }
    }
}
