package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.CobblemonTier;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OSMCCobblemonTierRegistry {

    private static final List<CobblemonTier> tiers = new ArrayList<>();

    public static void init() {
        load();
    }

    @NotNull
    public static List<CobblemonTier> get() { return tiers; }

    @Nullable
    public static CobblemonTier get(int tierNumber) {
        return tiers.get(tierNumber);
    }

    /**
     * Register the tier at the index - 1
     * @param tier The new tier to register
     * @param index The numerical value for the tier
     */
    public static void register(CobblemonTier tier, int index) {
        if(tiers.size() >= index)
            tiers.set(index - 1, tier);
        else
            tiers.add(tier);
    }

    public static void load() {
        tiers.clear();
        File[] files = OSMCConstants.TIER_CONFIG_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files != null) {
            for (File file : files) {
                int index = Integer.parseInt(file.getName().replace(".json", ""));
                JsonElement json = JsonUtils.loadFromJson(OSMCConstants.TIER_CONFIG_DIR, file.getName(), null);

                if (json != null) {
                    CobblemonTier.CODEC.parse(JsonOps.INSTANCE, json)
                            .ifSuccess(tier -> OSMCCobblemonTierRegistry.register(tier, index))
                            .ifError(error -> OSMC.LOGGER.error(error.message()));
                }
            }
        }

        if(tiers.isEmpty()) {
            CobblemonTier tier = new CobblemonTier();
            tier.setMinSpawnedLevel(5);
            tier.setMaxSpawnedLevel(15);
            tier.setReqLevel(1);
            tier.setName(Text.literal("1").formatted(Formatting.GREEN));
            register(tier, 1);
            save();
        }
    }

    public static void save() {
        for (int index = 0; index < tiers.size(); index++) {
            int finalIndex = index + 1;
            CobblemonTier.CODEC.encodeStart(JsonOps.INSTANCE, tiers.get(index))
                    .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.TIER_CONFIG_DIR, finalIndex + ".json", json))
                    .ifError(error -> OSMC.LOGGER.error(error.message()));
        }
    }
}
