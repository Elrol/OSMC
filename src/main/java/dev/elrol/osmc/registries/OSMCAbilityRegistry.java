package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Ability;
import dev.elrol.osmc.data.ability_effects.DurationAbilityEffect;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OSMCAbilityRegistry {

    private static final Map<Identifier, Ability> ABILITY_MAP = new HashMap<>();

    public static void init(MinecraftServer server){
        load(server);
    }

    public static void register(Ability ability) {
        if(ability.isEnabled())
            ABILITY_MAP.put(ability.getID(), ability);
    }

    @Nullable
    public static Ability get(Identifier id) {
        return ABILITY_MAP.get(id);
    }

    public static void save(Ability ability, MinecraftServer server) {
        Codec<Ability> codec = Ability.CODEC;
        RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
        codec.encodeStart(registryOps, ability)
                .ifError(error -> OSMC.LOGGER.error(error.message()))
                .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.ABILITY_CONFIG_DIR, ability.getID().getPath() + ".json", json));
    }

    public static void save(MinecraftServer server) {
        ABILITY_MAP.forEach((id, ability) -> save(ability, server));
    }

    public static void load(MinecraftServer server) {
        ABILITY_MAP.clear();
        File[] files = OSMCConstants.ABILITY_CONFIG_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files == null || files.length == 0) {
            registerExampleAbility(server);
            return;
        }

        for(File file : files) {
            JsonElement json = JsonUtils.loadFromJson(OSMCConstants.ABILITY_CONFIG_DIR, file.getName(), null);

            if(json != null) {
                RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
                Ability.CODEC.parse(registryOps, json)
                        .resultOrPartial(OSMC.LOGGER::error)
                        .ifPresent(OSMCAbilityRegistry::register);
            } else {
                OSMC.LOGGER.error("Ability failed to load from: {}", file);
            }
        }
    }

    private static void registerExampleAbility(MinecraftServer server) {
        Ability ability = new Ability(OSMCConstants.osmcID("example_ability"));
        DurationAbilityEffect daEffect = new DurationAbilityEffect(OSMCConstants.osmcID("example_ability_effect"), 0, 5);
        daEffect.setDescription(Text.literal("This increases your ability by 5 seconds"));
        ability.addAbilityEffect(daEffect);
        save(ability, server);
    }

}
