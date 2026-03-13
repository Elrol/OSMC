package dev.elrol.osmc.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSMCConfig {
    private static final String FILENAME = "config.json";

    public static final Codec<OSMCConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isDebug").forGetter(OSMCConfig::getDebug),
            Codec.BOOL.fieldOf("sendLevelUpToGlobal").forGetter(OSMCConfig::getSendLevelUpToGlobal),
            Codec.INT.fieldOf("maxLevel").forGetter(OSMCConfig::getMaxLevel),
            Codec.INT.fieldOf("autosave").forGetter(OSMCConfig::getAutoSave),
            Codec.INT.fieldOf("expPayout").forGetter(OSMCConfig::getExpPayout),
            Codec.INT.fieldOf("leaderboardCount").forGetter(OSMCConfig::getLeaderboardCount),
            DamageMitigationConfig.CODEC.fieldOf("damageMitigation").forGetter(OSMCConfig::getDamageMitigation),
            TrainerLevelConfig.CODEC.fieldOf("trainerLevel").forGetter(OSMCConfig::getTrainerLevel),
            CobblemonTiers.CODEC.fieldOf("cobblemonTiers").forGetter(OSMCConfig::getCobblemonTiers),
            Codec.INT.optionalFieldOf("millisecondsToActivateAbility", 500).forGetter(OSMCConfig::getMillisecondsToActivateAbility)
    ).apply(instance, (isDebug, sendLevelUpToGlobal, maxLevel, autosave, expPayout, leaderboardCount, damageMitigation, trainerLevel, cobblemonTiers, millisecondsToActivateAbility) -> {
        OSMCConfig data = new OSMCConfig();

        data.isDebug = isDebug;
        data.sendLevelUpToGlobal = sendLevelUpToGlobal;

        data.maxLevel = maxLevel;
        data.autoSave = autosave;
        data.expPayout = expPayout;
        data.leaderboardCount = leaderboardCount;

        data.damageMitigation = damageMitigation;
        data.trainerLevel = trainerLevel;
        data.cobblemonTiers = cobblemonTiers;

        data.millisecondsToActivateAbility = millisecondsToActivateAbility;

        return data;
    }));

    private boolean isDebug = false;
    private boolean sendLevelUpToGlobal = true;

    private int maxLevel = 99;
    private int autoSave = 5;
    private int expPayout = 20;
    private int leaderboardCount = 5;
    private int millisecondsToActivateAbility = 500;

    private DamageMitigationConfig damageMitigation = new DamageMitigationConfig();
    private TrainerLevelConfig trainerLevel = new TrainerLevelConfig();
    private CobblemonTiers cobblemonTiers = new CobblemonTiers();

    public boolean getDebug() { return isDebug; }

    public boolean getSendLevelUpToGlobal() { return sendLevelUpToGlobal; }

    public int getMaxLevel() { return maxLevel; }

    public int getAutoSave() { return autoSave; }

    public int getExpPayout() { return expPayout; }

    public int getLeaderboardCount() { return leaderboardCount; }

    public DamageMitigationConfig getDamageMitigation() { return damageMitigation; }

    public TrainerLevelConfig getTrainerLevel() { return trainerLevel; }

    public CobblemonTiers getCobblemonTiers() { return cobblemonTiers; }

    public int getMillisecondsToActivateAbility() { return millisecondsToActivateAbility; }

    public void save() {

        trainerLevel.check();

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

    public static class TrainerLevelConfig {

        public static final Codec<TrainerLevelConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.listOf().fieldOf("subskills").forGetter(TrainerLevelConfig::getSubskills),
                Codec.STRING.fieldOf("formula").forGetter(TrainerLevelConfig::getFormula)
        ).apply(instance, (subskills, formula) -> {
            TrainerLevelConfig data = new TrainerLevelConfig();
            data.formula = formula;
            data.subskills.addAll(subskills);
            return data;
        }));

        private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{(.+?)\\}");
        private final List<Identifier> subskills = new ArrayList<>();
        private String formula = "{osmc:example_skill} + (total / skills)";

        public int calculate(UUID uuid) {
            PlayerSkillData data = OSMCPlayerDataRegistry.get(uuid);

            double totalLevels = 0;
            Map<String, Double> skillValues = new HashMap<>();

            for(Identifier id : subskills) {
                double level = data.getSkillLevel(id);
                skillValues.put(id.toString(), level);
                totalLevels += level;
            }

            StringBuilder sb = new StringBuilder();
            Matcher matcher = VARIABLE_PATTERN.matcher(formula);

            while (matcher.find()) {
                String key = matcher.group(1);
                double val = skillValues.getOrDefault(key, 0.0d);
                matcher.appendReplacement(sb, String.valueOf(val));
            }
            matcher.appendTail(sb);

            Map<String, Double> globals = new HashMap<>();
            globals.put("skills", (double) subskills.size());
            globals.put("total", totalLevels);
            return (int) MathUtils.calculate(sb.toString(), globals);
        }

        public String getFormula() { return formula; }

        public List<Identifier> getSubskills() { return subskills; }
        public void check() {
            if(subskills.isEmpty()) {
                subskills.add(OSMCConstants.osmcID("example_skill"));
            }
        }
    }

    public static class CobblemonTiers {

        public static final Codec<CobblemonTiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("enabled").forGetter(CobblemonTiers::getEnabled)
        ).apply(instance, (enabled) -> {
            CobblemonTiers data = new CobblemonTiers();

            data.enabled = enabled;

            return data;
        }));

        boolean enabled = false;

        public boolean getEnabled() { return enabled; }
    }
}
