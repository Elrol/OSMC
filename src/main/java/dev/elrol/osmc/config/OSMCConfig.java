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
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class OSMCConfig {
    private static final String FILENAME = "config.json";

    public static final Codec<OSMCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isDebug").forGetter(OSMCConfig::getDebug),
            Codec.BOOL.fieldOf("sendLevelUpToGlobal").forGetter(OSMCConfig::getSendLevelUpToGlobal),
            Codec.INT.fieldOf("maxLevel").forGetter(OSMCConfig::getMaxLevel),
            Codec.INT.fieldOf("autosave").forGetter(OSMCConfig::getAutoSave),
            Codec.INT.fieldOf("expPayout").forGetter(OSMCConfig::getExpPayout),
            Codec.INT.fieldOf("leaderboardCount").forGetter(OSMCConfig::getLeaderboardCount),
            DamageMitigationConfig.CODEC.fieldOf("damageMitigation").forGetter(OSMCConfig::getDamageMitigation)
    ).apply(instance, (isDebug, sendLevelUpToGlobal, maxLevel, autosave, expPayout, leaderboardCount, damageMitigation) -> {
        OSMCConfig data = new OSMCConfig();

        data.isDebug = isDebug;
        data.sendLevelUpToGlobal = sendLevelUpToGlobal;

        data.maxLevel = maxLevel;
        data.autoSave = autosave;
        data.expPayout = expPayout;
        data.leaderboardCount = leaderboardCount;

        data.damageMitigation = damageMitigation;

        return data;
    }));

    private boolean isDebug = false;
    private boolean sendLevelUpToGlobal = true;

    private int maxLevel = 99;
    private int autoSave = 5;
    private int expPayout = 20;
    private int leaderboardCount = 5;

    private DamageMitigationConfig damageMitigation = new DamageMitigationConfig();

    public boolean getDebug() { return isDebug; }

    public boolean getSendLevelUpToGlobal() { return sendLevelUpToGlobal; }

    public int getMaxLevel() { return maxLevel; }

    public int getAutoSave() { return autoSave; }

    public int getExpPayout() { return expPayout; }

    public int getLeaderboardCount() { return leaderboardCount; }

    public DamageMitigationConfig getDamageMitigation() { return damageMitigation; }

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

    public static class DamageMitigationConfig {
        public static final Codec<DamageMitigationConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("sourceLimits").forGetter(DamageMitigationConfig::getSourceLimits)
        ).apply(instance, (sourceLimits) -> {
            DamageMitigationConfig data = new DamageMitigationConfig();
            data.sourceLimits.putAll(sourceLimits);
            return data;
        }));

        Map<Identifier, Float> sourceLimits = new HashMap<>();

        public void addSourceLimit(Identifier id, float limit) {
            sourceLimits.put(id, limit);
        }

        public float getSourceLimit(Identifier id) {
            return sourceLimits.getOrDefault(id, 0f);
        }

        public Map<Identifier, Float> getSourceLimits() { return sourceLimits; }
    }
}
