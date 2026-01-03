package dev.elrol.osmc.libs;

import com.google.gson.Gson;
import dev.elrol.osmc.Osmc;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtils {

    public static void saveToJson(File dir, String name, Object obj) {
        Gson GSON = OSMCConstants.makeGSON();
        File file = new File(dir, name);

        if(dir.mkdirs()) {
            Osmc.LOGGER.warn("{} directory for OSMC created. If this happens more than once, there is an issue.", dir);
        }

        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    Osmc.LOGGER.warn("New file {} created.", name);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try(FileWriter writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
            if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
                Osmc.LOGGER.info("Saved File {}", name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T loadFromJson(File dir, String name, T defaultObject) {
        File file = new File(dir, name);

        if(file.exists()) {
            try(FileReader reader = new FileReader(file)) {
                Gson GSON = OSMCConstants.makeGSON();
                T obj = GSON.fromJson(reader, (Class<T>) defaultObject.getClass());

                if(obj != null) {
                    if(Osmc.CONFIG.isDebug) {
                        Osmc.LOGGER.info("Loaded File {}", name);
                    }
                    return obj;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveToJson(dir, name, defaultObject);
        return defaultObject;

    }
}
