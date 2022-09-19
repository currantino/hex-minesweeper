package com.github.currantino;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

import java.util.HashMap;
import java.util.Map;

public class Tile extends Polygon {
    private final static double r = 20; // the inner radius from hexagon center to outer corner
    private final static double innerRadius = Math.sqrt(r * r * 0.75); // the inner radius from hexagon center to middle of the axis
    private final static double TILE_WIDTH = 2 * innerRadius;
    private final static double TILE_HEIGHT = 2 * r;
    private final String EXTENSION = ".png";
    private Coordinates coordinates;
    private boolean isFlagged;
    private boolean isBomb;
    private boolean isEmpty;
    private int bombsAround = -1;
    int xStartOffset = 40; // offsets the entire field to the right
    int yStartOffset = 40; // offsets the entire fields downwards

    Tile(int row, int col) {
        coordinates = new Coordinates(col, row);
        double x = col * TILE_WIDTH + (row % 2) * innerRadius + xStartOffset;
        double y = row * TILE_HEIGHT * 0.75 + yStartOffset;
        // creates the polygon using the corner coordinates
        getPoints().addAll(
                x, y,
                x, y + r,
                x + innerRadius, y + r * 1.5,
                x + TILE_WIDTH, y + r,
                x + TILE_WIDTH, y,
                x + innerRadius, y - r * 0.5
        );
        setImage("cover");
        setStrokeWidth(1);
        setStroke(Color.BLACK);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public int getBombsAround() {
        return bombsAround;
    }

    public void setBombsAround(int bombsAround) {
        this.bombsAround = bombsAround;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile)) return false;

        Tile tile = (Tile) o;

        return getCoordinates().equals(tile.getCoordinates());
    }

    @Override
    public int hashCode() {
        return getCoordinates().hashCode();
    }

    @Override
    public String toString() {
        return "Cell{" +
                "coordinates=" + coordinates +
                ", isFlagged=" + isFlagged +
                ", hasBomb=" + isBomb +
                ", isEmpty=" + isEmpty +
                ", bombsAround=" + bombsAround +
                '}';
    }

    public void setImage(String name) {
        if (images.containsKey(name)) setFill(images.get(name));
        else {
            ImagePattern img = new ImagePattern(new Image(String.valueOf(getClass().getResource(String.format("%s%s", name, EXTENSION)))));
            setFill(img);
            images.put(name, img);
        }

    }
    private Map<String, ImagePattern> images = new HashMap<>();
}