package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.PlayerSkillData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataRegistry {

    private static final Map<UUID, PlayerSkillData> PLAYER_SKILL_DATA_MAP = new HashMap<>();

    public static void init(){

    }

    public static void load(){}
    public static void save(){}

}
