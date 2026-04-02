package dev.elrol.osmc.menus;

import dev.elrol.osmc.data.Ability;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.libs.ModTranslations;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCAbilityRegistry;
import dev.elrol.osmc.registries.OSMCItems;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OSMCSkillMenu extends _PageMenuBase5 {

    private final Skill skill;
    private Ability ability;
    private final int level;
    private boolean hasShapeSettings = false;

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
                                .collect(Collectors.toMap(ae -> ae.getAbilityEffectID().toString(), ae -> ae )));
                if(ability.doesHaveShapeSettings()) hasShapeSettings = true;
            }
            level = data.getSkillLevel(skill.getID());
        } else {
            level = 0;
        }
    }

    @Override
    protected void back() {
        new OSMCMenu(this.player).open();
    }

    @Override
    protected void drawMenu() {
        super.drawMenu();

        if(ability != null) {
            List<Text> lore = new ArrayList<>();
            lore.add(Text.literal("Configure what blocks are broken when using this ability"));

            if(hasShapeSettings) {
                setSlot(0, MenuUtils.itemWithLore(OSMCItems.LIME_BUTTON, 1, Text.literal("Break Shape Menu").formatted(Formatting.GREEN), lore)
                        .setCallback(() -> {
                            if(!hasShapeSettings) return;
                            click();
                            ShapeCanvasMenu newMenu = new ShapeCanvasMenu(player, skill.getID());
                            newMenu.open();
                        })
                );

                boolean shapeEnabled = data.getAbilityEffectSetting(skill.getID(), OSMCConstants.osmcID("shape_break_enabled"));
                setSlot(9, MenuUtils.itemWithLore(shapeEnabled ? OSMCItems.LIME_BUTTON : OSMCItems.RED_BUTTON, 1, Text.literal(shapeEnabled ? "Break Shape Enabled" : "Break Shape Disabled").formatted(shapeEnabled ? Formatting.GREEN : Formatting.RED), lore)
                        .setCallback(() -> {
                            if (!hasShapeSettings) return;
                            click();
                            data.setAbilityEffectSetting(skill.getID(), OSMCConstants.osmcID("shape_break_enabled"), !shapeEnabled);
                            drawMenu();
                        })
                );
            }
        }

        drawItems(abilityEffects);
    }


    @Override
    protected <T> GuiElementBuilder createElement(String key, Map<String, T> map) {
        AbilityEffect abilityEffect = (AbilityEffect) map.get(key);
        boolean isUnlocked = abilityEffect.getReqLevel() <= level;
        boolean togglable = abilityEffect.isTogglable();
        boolean aeSetting = data.getAbilityEffectSetting(skill.getID(), abilityEffect.getAbilityEffectID());
        Item item = isUnlocked ? (togglable ? (aeSetting ? OSMCItems.LIME_BUTTON : OSMCItems.RED_BUTTON) : OSMCItems.LIME_BUTTON) : OSMCItems.GRAY_BUTTON;

        GuiElementBuilder element = MenuUtils.item(item, 1, abilityEffect.getDisplayName());
        element.addLoreLine(ModTranslations.removeItalic(abilityEffect.getDescription()));

        if(togglable && isUnlocked)
            element.setCallback(() -> {
                click();
                data.toggleAbilityEffectSettings(skill.getID(),
                        abilityEffect.getAbilityEffectID());
                drawMenu();
            });

        return element;
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
    public char getMenuChar() {
        return 'd';
    }
}
