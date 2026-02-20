package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkillData {

    public static Codec<PlayerSkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("uuid").forGetter(PlayerSkillData::getUuidString),
            Codec.unboundedMap(Identifier.CODEC, Codec.LONG).fieldOf("skillExp").forGetter(PlayerSkillData::getSkillExpMap),
            TextCodecs.CODEC.optionalFieldOf("username", Text.empty()).forGetter(PlayerSkillData::getUsername)
    ).apply(instance, (uuid, skillExp, username) -> {
        PlayerSkillData data = new PlayerSkillData(UUID.fromString(uuid));

        data.SKILL_EXP.putAll(skillExp);
        data.username = username;

        return data;
    }));

    private final UUID uuid;
    private final Map<Identifier, Long> SKILL_EXP = new LinkedHashMap<>();
    private final Map<Identifier, Integer> SKILL_LEVEL_CACHE = new LinkedHashMap<>();
    private Text username = Text.empty();

    public PlayerSkillData(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUsername(Text text) { username = text; }
    public Text getUsername() { return username; }

    public void setSkillExp(Identifier skillID, long newExp) {
        SKILL_EXP.put(skillID, newExp);
        SKILL_LEVEL_CACHE.remove(skillID);
    }

    public void addSkillExp(Identifier skillID, long expGained) {
        long oldExp = SKILL_EXP.getOrDefault(skillID, 0L);
        long newExp = oldExp + expGained;

        SKILL_EXP.put(skillID, newExp);
        SKILL_LEVEL_CACHE.remove(skillID);
    }

    public long getTargetXP(Identifier skillID) {
        Skill skill = OSMCSkillRegistry.get(skillID);
        assert skill != null;
        return Math.round(MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), getSkillLevel(skillID) + 1));
    }

    public int getSkillLevel(Identifier skillID) {
        if(SKILL_LEVEL_CACHE.containsKey(skillID)) return SKILL_LEVEL_CACHE.get(skillID);

        Skill skill = OSMCSkillRegistry.get(skillID);
        int level = 1;
        long xp = getSkillExp(skillID);

        if(skill == null) {
            OSMC.LOGGER.error("Invalid skill ID " + skillID);
            return 0;
        }

        while (level < OSMC.CONFIG.getMaxLevel()) {
            if(xp < MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), level + 1)) {
                break;
            }
            level++;
        }
        SKILL_LEVEL_CACHE.put(skillID, level);
        return level;
    }

    public long getSkillExp(Identifier skillID) {
        return SKILL_EXP.getOrDefault(skillID, 0L);
    }

    public SkillExpInfo getSkillInfo(Identifier skillID) {
        return new SkillExpInfo(
                getSkillLevel(skillID),
                getSkillExp(skillID),
                getTargetXP(skillID)
        );
    }

    public Map<Identifier, Long> getSkillExpMap() { return SKILL_EXP; }
    public UUID getUuid() { return uuid; }
    public String getUuidString() { return uuid.toString(); }

    public void rebuildLevelCache() {
        SKILL_EXP.keySet().forEach(this::getSkillLevel);
    }

    public record SkillExpInfo(int level, long currentExp, long targetExp) {}
}
