package dev.elrol.osmc.menus;

import dev.elrol.osmc.data.Ability;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.libs.ModTranslations;
import dev.elrol.osmc.registries.OSMCAbilityRegistry;
import dev.elrol.osmc.registries.OSMCItems;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OSMCSkillMenu extends _PageMenuBase5 {

    private final Skill skill;
    private Ability ability;

    private final Map<String, AbilityEffect> abilityEffects = new HashMap<>();

    public OSMCSkillMenu(ServerPlayerEntity player, Identifier skillID) {
        this(player, skillID, 0);
    }

    public OSMCSkillMenu(ServerPlayerEntity player, Identifier skillID, int page) {
        super(player);
        this.page = page;
        skill = OSMCSkillRegistry.get(skillID);
        if(skill != null) {
            ability = OSMCAbilityRegistry.get(skill.getAbilityID());
            if(ability != null) {
                abilityEffects.putAll(
                        ability.getEffects()
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                ae -> ae.getAbilityEffectID().toString(),
                                                ae -> ae
                                        )));
            }
        }
    }

    @Override
    protected void back() {
        new OSMCMenu(this.player).open();
    }

    @Override
    protected void drawMenu() {
        super.drawMenu();

        drawItems(abilityEffects);
    }


    @Override
    protected <T> GuiElementBuilder createElement(String key, Map<String, T> map) {
        AbilityEffect abilityEffect = (AbilityEffect) map.get(key);
        boolean aeSetting = data.getAbilityEffectSetting(skill.getID(), abilityEffect.getAbilityEffectID());
        Item item = aeSetting ? OSMCItems.LIME_BUTTON : OSMCItems.RED_BUTTON;

        GuiElementBuilder element = MenuUtils.item(item, 1, abilityEffect.getDisplayName());
        element.addLoreLine(ModTranslations.removeItalic(abilityEffect.getDescription()));

        return element.setCallback(() -> {
            click();
            data.toggleAbilityEffectSettings(skill.getID(),
                    abilityEffect.getAbilityEffectID());
            drawMenu();
        });
    }

    @Override
    public int getLastPage() {
        return Math.floorDiv(abilityEffects.size(), 15);
    }

    @Override
    public void openNewMenu() {
        new OSMCSkillMenu(player, skill.getID(), page).open();
    }

    @Override
    public @NotNull String getMenuName() {
        return "skill_menu";
    }

    @Override
    public char getMenuUnicode() {
        return '≁';
    }
}
