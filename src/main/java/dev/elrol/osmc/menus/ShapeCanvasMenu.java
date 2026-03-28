package dev.elrol.osmc.menus;

import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Grid;
import dev.elrol.osmc.data.SkillSettingsData;
import dev.elrol.osmc.data.TriState;
import dev.elrol.osmc.libs.MenuUtils;
import dev.elrol.osmc.registries.OSMCItems;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShapeCanvasMenu extends _MenuBase {

    final Identifier skillID;
    final int maxRadius;
    final Grid<TriState> grid;
    final int maxBlocks;
    int x = -2;
    int y = -2;

    GuiElementBuilder[][] slots = new GuiElementBuilder[5][5];
    //shapePoints = SkillUtils.getPlayerAbilityBlockConfigPoint(player, skillID);

    public ShapeCanvasMenu(ServerPlayerEntity player, Identifier skillID) {
        super(player, ScreenHandlerType.GENERIC_9X5);
        maxRadius = OSMC.CONFIG.getMaxShapeCanvasRange();
        this.skillID = skillID;

        SkillSettingsData skillData = data.getSkillSettings(skillID);
        if(skillData == null) throw new IllegalArgumentException("Skill settings data is invalid");
        maxBlocks = skillData.getShapePoints() + 1;

        grid = skillData.getShapeSettings();
        if(grid.get(0,0) != TriState.TRUE) grid.set(0,0, TriState.TRUE);

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                int finalR = r;
                int finalC = c;
                slots[r][c] = new GuiElementBuilder(getItemForCell(r, c))
                        .hideTooltip()
                        .setCallback(() -> toggleCell(finalR, finalC));
            }
        }

        setSlot(16, MenuUtils.item(OSMCItems.UP_BUTTON,         1, "pan_up").setCallback(() -> pan(1, 0)));
        setSlot(24, MenuUtils.item(OSMCItems.LEFT_BUTTON,       1, "pan_left").setCallback(() -> pan(0, -1)));
        setSlot(26, MenuUtils.item(OSMCItems.RIGHT_BUTTON,      1, "pan_right").setCallback(() -> pan(0, 1)));
        setSlot(34, MenuUtils.item(OSMCItems.DOWN_BUTTON,       1, "pan_down").setCallback(() -> pan(-1, 0)));

        setSlot(25, MenuUtils.item(OSMCItems.LIGHT_GRAY_BUTTON, 1, "center").setCallback(() -> {
            x = -2;
            y = -2;
            click();
            drawMenu();
        }));

        menu.setOnClose(() -> {
            data.setSkillShapeData(skillID, grid);
            OSMCPlayerDataRegistry.updatePlayerData(data);
            OSMCPlayerDataRegistry.save(player);
        });
    }

    private void pan(int r, int c) {
        if(Math.abs(x + r) > maxRadius || Math.abs(y + c) > maxRadius) return;
        x += r;
        y += c;

        click();
        drawMenu();
    }

    private TriState getStateForCell(int r, int c) {
         return grid.get(x + r, y + c);
    }

    @Override
    protected void drawMenu() {
        super.drawMenu();

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                setSlot( (9 * r) + c, slots[r][c].setItem(getItemForCell(r, c)));
            }
        }
    }

    private Item getItemForCell(int r, int c) {
        if(x + r == 0 && y + c == 0) return OSMCItems.GRAY_BUTTON;

        TriState state = getEffectiveState(r, c);

        if(state == TriState.TRUE) return OSMCItems.LIME_BUTTON;
        if(hasTrueNeighbor(r, c)) return OSMCItems.YELLOW_BUTTON;
        return OSMCItems.RED_BUTTON;
    }

    private void toggleCell(int r, int c) {
        if(x + r == 0 && y + c == 0) return;
        TriState tristate = getStateForCell(r, c);
        boolean hasNeighbor = hasTrueNeighbor(r, c);

        if(tristate == TriState.FALSE && !hasNeighbor) return;

        click();

        if(tristate == TriState.TRUE) {
            remove(r, c);
        } else {
            setCell(r, c, TriState.TRUE);
        }

        revalidate();
        drawMenu();
    }

    private void revalidate() {
        Set<Long> connected = new HashSet<>();
        Queue<Long> toCheck = new LinkedList<>();

        long orign = Grid.getPackedKey(0, 0);
        toCheck.add(orign);
        connected.add(orign);

        while(!toCheck.isEmpty()) {
            long current = toCheck.poll();
            int x = (int) (current >> 32);
            int y = (int) (current & 0xffffffffL);

            int[][] neighbors = {{x+1, y}, {x-1, y}, {x, y+1}, {x, y-1}};
            for(int[] n : neighbors) {
                long key = Grid.getPackedKey(n[0], n[1]);
                if(grid.get(n[0], n[1]) == TriState.TRUE && !connected.contains(key)) {
                    connected.add(key);
                    toCheck.add(key);
                }
            }
        }

        List<Long> toRemove = new ArrayList<>();
        grid.get().forEach((key, state) -> {
            if(state == TriState.TRUE && !connected.contains(key)) toRemove.add(key);
        });

        for(Long key : toRemove) {
            grid.remove(key);
        }
    }

    private void remove(int r, int c) {
        grid.remove(r + x, c + y);
    }

    private void setCell(int r, int c, TriState state) {
        grid.set(r + x, c + y, state);
    }

    private TriState getEffectiveState(int r, int c) {
        return grid.get(x + r, y + c) == TriState.TRUE ? TriState.TRUE : (hasTrueNeighbor(r, c) ? TriState.NEUTRAL : TriState.FALSE);
    }

    private boolean hasTrueNeighbor(int r, int c) {
        if(maxBlocks <= grid.count(TriState.TRUE)) return false;

        int finalX = x + r;
        int finalY = y + c;
        return grid.get(finalX + 1, finalY) == TriState.TRUE ||
                grid.get(finalX - 1, finalY) == TriState.TRUE ||
                grid.get(finalX, finalY + 1) == TriState.TRUE ||
                grid.get(finalX, finalY - 1) == TriState.TRUE;
    }

    @Override
    public void openNewMenu() {
        new ShapeCanvasMenu(player, skillID).open();
    }

    @Override
    public @NotNull String getMenuName() {
        return "shape_canvas";
    }

    @Override
    public char getMenuChar() {
        return 'e';
    }
}
