package dev.elrol.osmc.menus;

import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.libs.ModTranslations;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OSMCMenu extends _PageMenuBase4 {

    private final Map<String, Skill> SKILLS = new HashMap<>();

    public OSMCMenu(ServerPlayerEntity player) {
        super(player);
        updateMap();
    }

    public OSMCMenu(ServerPlayerEntity player, int page) {
        super(player, page);
        updateMap();
    }

    private void updateMap() {
        Map<Identifier, Skill> SKILL_MAP = OSMCSkillRegistry.getAll();
        SKILLS.putAll(SKILL_MAP.keySet().stream().collect(Collectors.toMap(Identifier::toString, SKILL_MAP::get)));
    }

    @Override
    protected void drawMenu() {
        super.drawMenu();
        drawItems(SKILLS);
    }

    @Override
    protected <T> GuiElementBuilder createElement(String key, Map<String, T> map) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
        Skill skill = (Skill) map.get(key);

        GuiElementBuilder element = MenuUtils.item(skill.getIconItem(), 1, skill.getDisplayName());
        element.addLoreLine(ModTranslations.literal("Level: " + data.getSkillLevel(Identifier.tryParse(key))).formatted(Formatting.GRAY));
        element.addLoreLine(ModTranslations.literal("Click to customize your Ability Effects").formatted(Formatting.GREEN));

        return element.setCallback((i, clickType, slotActionType) -> {
            if(clickType.equals(ClickType.MOUSE_LEFT)) {
                click();
                new OSMCSkillMenu(player, skill.getID()).open();
            }
        });
    }

    @Override
    public int getLastPage() {
        return Math.floorDiv(OSMCSkillRegistry.getAll().size(), 10);
    }

    @Override
    public void openNewMenu() {
        new OSMCMenu(player, page).open();
    }

    @Override
    public @NotNull String getMenuName() {
        return "skills_menu";
    }

    @Override
    public char getMenuChar() {
        return 'c';
    }
}
