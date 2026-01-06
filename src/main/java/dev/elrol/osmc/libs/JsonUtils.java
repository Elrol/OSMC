package dev.elrol.osmc.libs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.elrol.osmc.OSMC;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtils {
    private static final Gson GSON = OSMCConstants.makeGSON();

    public static void saveToJson(File dir, String name, JsonElement obj) {
        File file = new File(dir, name);

        if(dir.mkdirs()) {
            OSMC.LOGGER.warn("{} directory for OSMC created. If this happens more than once, there is an issue.", dir);
        }

        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    OSMC.LOGGER.warn("New file {} created.", name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try(FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
            if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
                OSMC.LOGGER.info("Saved File {}", name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonElement loadFromJson(File dir, String name, @Nullable JsonElement defaultJson) {
        File file = new File(dir, name);

        if(file.exists() && file.length() > 0) {
            try(FileReader reader = new FileReader(file)) {
                JsonElement obj = GSON.fromJson(reader, JsonElement.class);

                if(obj != null && !obj.isJsonNull()) {
                    if(OSMC.CONFIG.isDebug) {
                        OSMC.LOGGER.info("Loaded File {}", name);
                    }
                    return obj;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(defaultJson != null) saveToJson(dir, name, defaultJson);
        return defaultJson;

    }
}
