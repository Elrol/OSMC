package dev.elrol.osmc.libs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.io.File;

public class OSMCConstants {

    public static final String MODID = "osmc";

    // Identifiers
    public static final Identifier EXP_TYPE_ID = Identifier.of(MODID, "exp_data_type");

    // Registry Keys
    public static final RegistryKey<Registry<ExpSourceType<?>>> EXP_TYPE_KEY = RegistryKey.ofRegistry(EXP_TYPE_ID);

    // Files
    public static final File ROOT_DIR = FabricLoader.getInstance().getGameDir().toFile();
    public static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "OSMC");
    public static final File SKILL_CONFIG_DIR = new File(CONFIG_DIR, "Skills");

    public static final File OSMC_DATA_DIR = new File(ROOT_DIR, "OSMC");
    public static final File PLAYER_DATA_DIR = new File(OSMC_DATA_DIR, "player_data");

    // Exp Source Type IDs
    public static final String BLOCK_BREAK_EXP_ID = "block_break";
    public static final String BLOCK_INTERACT_EXP_ID = "block_interact";

    public static Gson makeGSON() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
    public static Identifier osmcID(String id) { return Identifier.of(MODID, id); }

}
