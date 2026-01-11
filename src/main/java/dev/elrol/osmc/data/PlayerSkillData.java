package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.PlayerDataRegistry;import dev.elrol.osmc.registries.SkillRegistry;import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSkillData {

    public static Codec<PlayerSkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("uuid").forGetter(PlayerSkillData::getUuidString),
        Codec.unboundedMap(Identifier.CODEC, Codec.LONG).fieldOf("skillExp").forGetter(PlayerSkillData::getSkillExpMap)
    ).apply(instance, (uuid, skillExp) -> {
        PlayerSkillData data = new PlayerSkillData(UUID.fromString(uuid));

        data.SKILL_EXP.putAll(skillExp);

        return data;
    }));

    private final UUID uuid;
    private final Map<Identifier, Long> SKILL_EXP = new HashMap<>();

    public PlayerSkillData(UUID uuid) {
        this.uuid = uuid;
    }

    public long addSkillXp(Identifier skillID, int expGained) {
        long newXp = SKILL_EXP.getOrDefault(skillID, 0L) + expGained;
        SKILL_EXP.put(skillID, newXp);
        PlayerDataRegistry.updatePlayerData(this);
        return newXp;
    }

    public long getTargetXP(Identifier skillID) {
        Skill skill = SkillRegistry.get(skillID);
        return Math.round(MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), getSkillLevel(skillID) + 1));
    }

    public int getSkillLevel(Identifier skillID) {
        Skill skill = SkillRegistry.get(skillID);
        int level = 1;
        long xp = getSkillXp(skillID);

        while (level < OSMC.CONFIG.maxLevel) {
            if(xp < MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), level + 1)) {
                break;
            }
            level++;
        }
        return level;
    }

    public long getSkillXp(Identifier skillID) {
        return SKILL_EXP.getOrDefault(skillID, 0L);
    }

    public SkillExpInfo getSkillInfo(Identifier skillID) {
        return new SkillExpInfo(
                getSkillLevel(skillID),
                getSkillXp(skillID),
                getTargetXP(skillID)
        );
    }

    public Map<Identifier, Long> getSkillExpMap() { return SKILL_EXP; }
    public UUID getUuid() { return uuid; }
    public String getUuidString() { return uuid.toString(); }

    public record SkillExpInfo(int level, long currentExp, long targetExp) {
    }
}
