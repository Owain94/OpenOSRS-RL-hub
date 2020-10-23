package com.essencerunning;

import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseAdapter;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class ShiftClickInputListener extends MouseAdapter implements KeyListener {

    @Inject
    private EssenceRunningPlugin plugin;

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            plugin.setShiftModifier(true);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            plugin.setShiftModifier(false);
        }
    }

    @Override
    public MouseEvent mousePressed(final MouseEvent mouseEvent) {
        plugin.setShiftModifier(mouseEvent.isShiftDown());
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseEntered(final MouseEvent mouseEvent) {
        plugin.setShiftModifier(mouseEvent.isShiftDown());
        return mouseEvent;
    }
}
