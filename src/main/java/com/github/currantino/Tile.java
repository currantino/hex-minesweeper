package com.github.currantino;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;

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
    private int bombsAround;
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

        // set up the visuals and a click listener for the tile
        if (col % 2 == 0)
        setFill(Color.ANTIQUEWHITE);
        else setFill(Color.GREENYELLOW);
        setStrokeWidth(1);
        setStroke(Color.BLACK);
//        setOnMouseClicked(e -> System.out.println("Clicked: " + this));
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

    public void setImage(String filename) {
        Image img = new Image(String.valueOf(getClass().getResource(String.format("%s%s", filename, EXTENSION))));
        this.setFill(new ImagePattern(img));
    }
}