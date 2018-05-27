package ru.spbau.mit.game.client;

import ru.spbau.mit.game.client.Game.Role;
import ru.spbau.mit.game.common.api.units.Field;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ImagePack {
    private final Image emptyCell;
    private final Image cellFocusedX;
    private final Image cellFocusedO;
    private final Image cellPressedX;
    private final Image cellPressedO;
    private final Image xCell;
    private final Image oCell;

    private final Map<Integer, IconPack> cache = new HashMap<>();

    ImagePack(Image emptyCell,
              Image cellFocusedX, Image cellFocusedO,
              Image cellPressedX, Image cellPressedO,
              Image xCell, Image oCell) {
        this.emptyCell = emptyCell;
        this.cellFocusedX = cellFocusedX;
        this.cellFocusedO = cellFocusedO;
        this.cellPressedX = cellPressedX;
        this.cellPressedO = cellPressedO;
        this.xCell = xCell;
        this.oCell = oCell;
    }

    public static ImagePack loadDefaultPack() throws IOException {
        return new ImagePack(
                loadImage("/empty_no_focus.png"),
                loadImage("/empty_focus_X.png"),
                loadImage("/empty_focus_O.png"),
                loadImage("/pressed_X.png"),
                loadImage("/pressed_O.png"),
                loadImage("/X.png"),
                loadImage("/O.png")
        );
    }

    public IconPack getIconPack(int size) {
        return cache.computeIfAbsent(size, IconPack::new);
    }

    private static Icon getScaledIcon(Image image, int size) {
        return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    private static Image loadImage(String name) throws IOException {
        return ImageIO.read(ImagePack.class.getResource(name));
    }

    public class IconPack {
        private final Icon emptyCell;
        private final Icon cellFocusedX;
        private final Icon cellFocusedO;
        private final Icon cellPressedX;
        private final Icon cellPressedO;
        private final Icon xCell;
        private final Icon oCell;


        private IconPack(int size) {
            this.emptyCell = getScaledIcon(ImagePack.this.emptyCell, size);
            this.cellFocusedX = getScaledIcon(ImagePack.this.cellFocusedX, size);
            this.cellFocusedO = getScaledIcon(ImagePack.this.cellFocusedO, size);
            this.cellPressedX = getScaledIcon(ImagePack.this.cellPressedX, size);
            this.cellPressedO = getScaledIcon(ImagePack.this.cellPressedO, size);
            this.xCell = getScaledIcon(ImagePack.this.xCell, size);
            this.oCell = getScaledIcon(ImagePack.this.oCell, size);
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
    }
}
