package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataRegistry {

    private static final Map<UUID, PlayerSkillData> PLAYER_SKILL_DATA_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<Identifier, Integer>> EXP_BUFFER = new ConcurrentHashMap<>();

    public static void init(){
        PLAYER_SKILL_DATA_MAP.clear();
        load();
    }

    public static void bufferExp(ServerPlayerEntity player, Identifier skillID, int expGain) {
        Map<Identifier, Integer> buffer = EXP_BUFFER.getOrDefault(player.getUuid(), new ConcurrentHashMap<>());
        buffer.compute(skillID, (key, value) -> value == null ? expGain : value + expGain);
        EXP_BUFFER.put(player.getUuid(), buffer);
    }

    public static void payBuffer(MinecraftServer server) {
        EXP_BUFFER.forEach((uuid, buffer) -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if(player != null) {
                PlayerSkillData data = PlayerDataRegistry.get(uuid);
                buffer.forEach((id, expGain) -> {
                    Skill skill = SkillRegistry.get(id);
                    if(skill != null) {
                        int oldLevel = data.getSkillLevel(id);
                        data.addSkillExp(id, (long) expGain);

                        int newLevel = data.getSkillLevel(id);
                        PlayerSkillData.SkillExpInfo info = data.getSkillInfo(id);
                        String message = "Gained " + expGain + " exp. Level: " + info.level() + " [" + info.currentExp() + "/" + info.targetExp() + "]";
                        OSMCConstants.sendExpMessage(player, skill, message);
                        if(oldLevel < newLevel) OSMCConstants.levelUpNotification(player, id);
                    }
                });
                PlayerDataRegistry.updatePlayerData(data);
            }
        });
        EXP_BUFFER.clear();
    }

    public static void updatePlayerData(PlayerSkillData data) {
        PLAYER_SKILL_DATA_MAP.put(data.getUuid(), data);
    }
    
    @NotNull
    public static PlayerSkillData get(UUID uuid) {
        return PLAYER_SKILL_DATA_MAP.getOrDefault(uuid, new PlayerSkillData(uuid));
    }
    
    public static void load() {
        File[] files = OSMCConstants.PLAYER_DATA_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files != null) {
            for (File file : files) {
                JsonElement json = JsonUtils.loadFromJson(OSMCConstants.PLAYER_DATA_DIR, file.getName(), null);
                if(json != null) {
                    PlayerSkillData.CODEC.decode(JsonOps.INSTANCE, json)
                            .ifError(error -> OSMC.LOGGER.error(error.message()))
                            .ifSuccess(pair -> updatePlayerData(pair.getFirst()));
                }
            }
        }
    }

    public static void save(PlayerEntity player) { save(player.getUuid()); }

    public static void save(UUID uuid) {
        PlayerSkillData.CODEC.encodeStart(JsonOps.INSTANCE, get(uuid))
                .ifError(error -> OSMC.LOGGER.error(error.message()))
                .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.PLAYER_DATA_DIR, uuid.toString() + ".json", json));
    }

    public static void save() {
        PLAYER_SKILL_DATA_MAP.keySet().forEach(PlayerDataRegistry::save);
    }

}
