package ru.spbau.mit.game.client;

import ru.spbau.mit.game.client.Game.Role;
import ru.spbau.mit.game.common.api.units.Field;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class ImagePack {
    private final ImageIcon emptyCell;
    private final ImageIcon cellFocusedX;
    private final ImageIcon cellFocusedO;
    private final ImageIcon cellPressedX;
    private final ImageIcon cellPressedO;
    private final ImageIcon xCell;
    private final ImageIcon oCell;

    ImagePack(ImageIcon emptyCell,
                     ImageIcon cellFocusedX, ImageIcon cellFocusedO,
                     ImageIcon cellPressedX, ImageIcon cellPressedO,
                     ImageIcon xCell, ImageIcon oCell) {
        this.emptyCell = emptyCell;
        this.cellFocusedX = cellFocusedX;
        this.cellFocusedO = cellFocusedO;
        this.cellPressedX = cellPressedX;
        this.cellPressedO = cellPressedO;
        this.xCell = xCell;
        this.oCell = oCell;
    }

    public static ImagePack loadDefaultPack(int size) throws IOException {
        return new ImagePack(
                loadIcon("/empty_no_focus.png", size),
                loadIcon("/empty_focus_X.png", size),
                loadIcon("/empty_focus_O.png", size),
                loadIcon("/pressed_X.png", size),
                loadIcon("/pressed_O.png", size),
                loadIcon("/X.png", size),
                loadIcon("/O.png", size)
        );
    }

    private static ImageIcon loadIcon(String name, int size) throws IOException {
        final Image image = ImageIO.read(ImagePack.class.getResourceAsStream(name));
        return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    public void initButton(JButton button, Role role) {
        button.setPreferredSize(new Dimension(emptyCell.getIconWidth(), emptyCell.getIconHeight()));
        button.setIcon(emptyCell);
        if (role == Role.OBSERVER) {
            button.setSelectedIcon(emptyCell);
            button.setRolloverIcon(emptyCell);
            button.setPressedIcon(emptyCell);
        } else {
            button.setSelectedIcon(role == Role.X_PLAYER ? cellFocusedX : cellFocusedO);
            button.setRolloverIcon(role == Role.X_PLAYER ? cellFocusedX : cellFocusedO);
            button.setPressedIcon(role == Role.X_PLAYER ? cellPressedX : cellPressedO);
        }
    }

    public void updateButton(JButton button, Field.Cell cell) {
        final Icon cellIcon = Optional.ofNullable(cell)
                .map(c -> c == Field.Cell.X ? xCell : oCell)
                .orElse(null);
        if (button.getDisabledIcon() != cellIcon) {
            button.setDisabledIcon(cellIcon);
        }
    }

//    public int preferredSize() {
//        return xCell.getIconHeight();
//    }
}
