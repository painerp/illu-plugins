package net.runelite.client.plugins.iutils.game;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.iutils.api.Interactable;
import net.unethicalite.api.items.Inventory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class iWidget implements Interactable, Useable {

    private final Game game;
    private final Widget widget;
    Map<Integer, iWidget> children = new HashMap<>();

    public iWidget(Game game, Widget widget) {
        this.game = game;
        this.widget = widget;
    }

    public Game game() {
        return game;
    }

    public Client client() {
        return game.client();
    }

    public int id() {
        return widget.getId();
    }

    public int itemId() {
        return widget.getItemId();
    }

    public int index() {
//        return game.getFromClientThread(() -> widget.getIndex());
        return widget.getIndex();
    }

    public int x() {
        return widget.getOriginalX();
    }

    public int y() {
        return widget.getOriginalY();
    }

    public String text() {
        return widget.getText();
    }

    public String name() {
        if (widget.getName().contains(">")) {
            String result = StringUtils.substringBetween(widget.getName()
                    , ">"
                    , "<");
            return result;
        }
        return widget.getName();
    }

    public int quantity() {
        return widget.getItemQuantity();
    }

    public boolean hidden() {
        if (widget == null) {
            return true;
        }
        return game.getFromClientThread(widget::isHidden);
    }

    public List<WidgetItem> getWidgetItems() {
        ArrayList<WidgetItem> items = new ArrayList<>();

        for (WidgetItem slot : widget.getWidgetItems()) {
            if (slot != null) {
                items.add(slot);
            }
        }
        return items;
    }

    public List<iWidget> items() {
        ArrayList<iWidget> items = new ArrayList<>();

        for (Widget slot : widget.getDynamicChildren()) {
            if (slot != null) {
                items.add(new iWidget(game(), slot));
            }
        }
        return items;
    }

    public int nestedInterface() {
        Widget[] nested = game.getFromClientThread(widget::getNestedChildren);

        if (nested.length == 0) {
            return -1;
        }

        return nested[0].getId() >> 16;
    }

    @Override
    public List<String> actions() {
        return Arrays.stream(widget.getActions())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void interact(String action) {
        widget.interact(action);
        game.sleepDelay();
//        String[] actions = widget.getActions();
//        for (int i = 0; i < actions.length; i++) {
//            if (action.equalsIgnoreCase(actions[i])) {
//                interact(i);
//                return;
//            }
//        }
//
//        throw new IllegalArgumentException("no action " + action + " on widget " + widget.getParentId() + "." + widget.getId());
    }

    public void interact(int action) {
        widget.interact(action);
        game.sleepDelay();
//        game().clientThread.invoke(() ->
//                client().invokeMenuAction("", "",
//                        action + 1,
//                        MenuAction.CC_OP.getId(),
//                        index(),
//                        id()
//                )
//        );
    }

    public void select() {
//        game.interactionManager().interact(
//                0,
//                MenuAction.WIDGET_TYPE_6.getId(),
//                index(),
//                id()
//        );
        game().clientThread.invoke(() ->
                client().invokeMenuAction("", "",
                        0,
                        MenuAction.WIDGET_CONTINUE.getId(),
                        index(),
                        id()
                )
        );
    }

    public Widget child(int child) { //TODO untested
        if (widget.getDynamicChildren().length == 0) {
            return null;
        }

        var c = widget.getDynamicChildren()[child];

        if (c == null) {
            this.children.put(child, this);
        }

        return c;
    }

    @Override
    public void useOn(InventoryItem item) {
        Inventory.getFirst(itemId()).useOn(item.widgetItem.getWidget());
        game.sleepDelay();
//        game.interactionManager().submit(() -> game.clientThread.invoke(() -> {
//            game.client.setSelectedSpellWidget(id());
//            game.client.setSelectedSpellChildIndex(-1);
//            game.client.invokeMenuAction("", "", item.id(),
//                    MenuAction.WIDGET_USE_ON_ITEM.getId(), item.slot(), WidgetInfo.INVENTORY.getId());
//        }));
    }

    @Override
    public void useOn(iNPC npc) {
        Inventory.getFirst(itemId()).useOn(npc.npc);
        game.sleepDelay();
//        game.interactionManager().submit(() -> game.clientThread.invoke(() -> {
//            game.client.setSelectedSpellWidget(id());
//            game.client.setSelectedSpellChildIndex(-1);
//            game.client.invokeMenuAction("", "", npc.index(),
//                    MenuAction.WIDGET_TARGET_ON_NPC.getId(), 0, 0);
//        }));
    }

    @Override
    public void useOn(iObject object) {
        Inventory.getFirst(itemId()).useOn(object.tileObject);
        game.sleepDelay();
//        game.interactionManager().submit(() -> game.clientThread.invoke(() -> {
//            game.client.setSelectedSpellWidget(id());
//            game.client.setSelectedSpellChildIndex(-1);
//            game.client.invokeMenuAction("", "", object.id(),
//                    MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.getId(), object.menuPoint().getX(), object.menuPoint().getY());
//        }));
    }
}
