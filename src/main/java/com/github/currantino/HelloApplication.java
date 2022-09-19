//package com.github.currantino;//package com.github.currantino;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Polygon;
//import javafx.stage.Stage;
//
//public class HelloApplication extends Application {
//    private final static int WINDOW_WIDTH = 800;
//    private final static int WINDOW_HEIGHT = 600;
//
//    private final static double r = 20; // the inner radius from hexagon center to outer corner
//    private final static double innerRadius = Math.sqrt(r * r * 0.75); // the inner radius from hexagon center to middle of the axis
//    private final static double TILE_WIDTH = 2 * innerRadius;
//    private final static double TILE_HEIGHT = 2 * r;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    public void start(Stage primaryStage) {
//        AnchorPane board = new AnchorPane();
//        Scene content = new Scene(board, WINDOW_WIDTH, WINDOW_HEIGHT);
//        primaryStage.setScene(content);
//
//        int rowCount = 18; // how many rows of tiles should be created
//        int tilesPerRow = 20; // the amount of tiles that are contained in each row
//        int xStartOffset = 40; // offsets the entire field to the right
//        int yStartOffset = 40; // offsets the entire fields downwards
//
//        for (int x = 0; x < tilesPerRow; x++) {
//            for (int y = 0; y < rowCount; y++) {
//                double xCord = x * TILE_WIDTH + (y % 2) * innerRadius + xStartOffset;
//                double yCord = y * TILE_HEIGHT * 0.75 + yStartOffset;
//
//                Polygon tile = new Cell(xCord, yCord);
//                board.getChildren().add(tile);
//            }
//        }
//        primaryStage.show();
//    }
//
//
//    private static class Cell extends Polygon {
//        private final String EXTENSION = ".png";
//        private Coordinates coordinates;
//        private boolean isFlagged;
//        private boolean isBomb;
//        private boolean isEmpty;
//        private int bombsAround;
//
////        public Cell(int x, int y) {
////            this.coordinates = new Coordinates(x, y);
////            setImage("cover");
////            setPreferredSize(new Dimension(15, 15));
////        }
//
//        public Cell(double x, double y) {
//            // creates the polygon using the corner coordinates
//            getPoints().addAll(
//                    x, y,
//                    x, y + r,
//                    x + innerRadius, y + r * 1.5,
//                    x + TILE_WIDTH, y + r,
//                    x + TILE_WIDTH, y,
//                    x + innerRadius, y - r * 0.5
//            );
//
//            // set up the visuals and a click listener for the tile
//            setFill(Color.ANTIQUEWHITE);
//            setStrokeWidth(1);
//            setStroke(Color.BLACK);
//            setOnMouseClicked(e -> System.out.println("Clicked: " + this));
//        }
//
//        public Coordinates getCoordinates() {
//            return coordinates;
//        }
//
//        public void setCoordinates(Coordinates coordinates) {
//            this.coordinates = coordinates;
//        }
//
//        public boolean isFlagged() {
//            return isFlagged;
//        }
//
//        public void setFlagged(boolean flagged) {
//            isFlagged = flagged;
//        }
//
//        public boolean isBomb() {
//            return isBomb;
//        }
//
//        public void setBomb(boolean bomb) {
//            isBomb = bomb;
//        }
//
//        public boolean isEmpty() {
//            return isEmpty;
//        }
//
//        public void setEmpty(boolean empty) {
//            isEmpty = empty;
//        }
//
//        public int getBombsAround() {
//            return bombsAround;
//        }
//
//        public void setBombsAround(int bombsAround) {
//            this.bombsAround = bombsAround;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof Cell)) return false;
//
//            Cell cell = (Cell) o;
//
//            return getCoordinates().equals(cell.getCoordinates());
//        }
//
//        @Override
//        public int hashCode() {
//            return getCoordinates().hashCode();
//        }
//
//        @Override
//        public String toString() {
//            return "Cell{" +
//                    "coordinates=" + coordinates +
//                    ", isFlagged=" + isFlagged +
//                    ", hasBomb=" + isBomb +
//                    ", isEmpty=" + isEmpty +
//                    ", bombsAround=" + bombsAround +
//                    '}';
//        }
//
//        public void setImage(String filename) {
//
//            //this.setIcon(new ImageIcon(getClass().getResource(String.format("%s%s", filename, EXTENSION))));
//        }
//    }
//
//}
//
////private static class Tile extends Polygon {
////        Tile(double x, double y) {
////            // creates the polygon using the corner coordinates
////            getPoints().addAll(
////                    x, y,
////                    x, y + r,
////                    x + innerRadius, y + r * 1.5,
////                    x + TILE_WIDTH, y + r,
////                    x + TILE_WIDTH, y,
////                    x + innerRadius, y - r * 0.5
////            );
////
////            // set up the visuals and a click listener for the tile
////            setFill(Color.ANTIQUEWHITE);
////            setStrokeWidth(1);
////            setStroke(Color.BLACK);
////            setOnMouseClicked(e -> System.out.println("Clicked: " + this));
////        }
////
////    }

package com.github.currantino;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class HelloApplication extends Application {
    private final static int WINDOW_WIDTH = 300;
    private final static int WINDOW_HEIGHT = 300;

    private final static double r = 20; // the inner radius from hexagon center to outer corner
    private final static double innerRadius = Math.sqrt(r * r * 0.75); // the inner radius from hexagon center to middle of the axis
    private final static double TILE_WIDTH = 2 * innerRadius;
    private final static double TILE_HEIGHT = 2 * r;
    private final int ROWS = 10; // how many rows of tiles should be created
    private final int COLUMNS = 10; // the amount of tiles that are contained in each row
    private final int BOMBS = 10;
    private AnchorPane board = new AnchorPane();
    private Map<Coordinates, Tile> tileMap;
    //    private Tile[][] tiles;
    private Set<Tile> bombs;
    private Set<Tile> openTiles;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
//        tiles = new Tile[ROWS][COLUMNS];
        tileMap = new HashMap<>(ROWS * COLUMNS);
        bombs = new HashSet<>();
        openTiles = new HashSet<>();
        Scene content = new Scene(board, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(content);


        fillBoardWithTiles();
        plantBombs();
        primaryStage.show();
    }


    private void fillBoardWithTiles() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Tile tile = new Tile(row, col);
                Coordinates coordinates = new Coordinates(col, row);
                tileMap.put(coordinates, tile);
//                tiles[col][row] = tile;
                tile.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        openTile(tile);
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        if (tile.isFlagged()) removeFlag(tile);
                        else setFlag(tile);
                    }
                });
                board.getChildren().add(tile);
            }
        }
    }

    private void plantBombs() {
        for (int i = 0; i < BOMBS; i++) {
            int col = ThreadLocalRandom.current().nextInt(0, COLUMNS * 2 - 1);
            int row = ThreadLocalRandom.current().nextInt(0, ROWS * 2 - 1);
            Coordinates coordinates = new Coordinates(col, row);
            Tile tile = tileMap.get(coordinates);
            if (tile == null || bombs.contains(tile)) i -= 1;
            else {
                tile.setBomb(true);
                tile.setImage("mine");
                bombs.add(tile);
            }
        }
//        Tile bomb1 = tileMap.get(Coordinates.getCoordinates(2, 2));
//        tileToBomb(bomb1);
//        Tile bomb2 = tileMap.get(Coordinates.getCoordinates(5, 5));
//        tileToBomb(bomb2);
//        Tile bomb3 = tileMap.get(Coordinates.getCoordinates(7, 2));
//        tileToBomb(bomb3);
//        Tile bomb4 = tileMap.get(Coordinates.getCoordinates(2, 7));
//        tileToBomb(bomb4);

    }

    void tileToBomb(Tile tile) {
        tile.setBomb(true);
        tile.setImage("mine");
        bombs.add(tile);
    }

    public int countBombsAround(Tile tile) {
        int count = 0;
        int row = tile.getCoordinates().getY();
        int col = tile.getCoordinates().getX();

        if (isBombOnTheTop(row, col)) {
            System.out.println("top");
            count++;
        }

        if (isBombOnTheRight(row, col)) {
            System.out.println("right");
            count++;
        }

        if (isBombOnTheBottom(row, col)) {
            System.out.println("bottom");
            count++;
        }

        if (isBombOnTheLeft(row, col)) {
            System.out.println("left");
            count++;
        }

        if (isBombOnTheBottomRight(row, col)) {
            System.out.println("bottom right");
            count++;
        }
        if (isBombOnTheTopRightCorner(row, col)) {
            System.out.println("top right");
            count++;
        }
        if (isBombOnTheTopLeftCorner(row, col)) {
            System.out.println("top left");
            count++;
        }
        if (isBombOnTheBottomLeftCorner(row, col)) {
            System.out.println("bottom left");
            count++;
        }
        return count;
    }

    private boolean isBombOnTheTop(final int row, final int col) {
        return row != 0 && tileMap.get(Coordinates.getCoordinates(col, row - 1)).isBomb();
    }

    private boolean isBombOnTheTopRightCorner(final int row, final int col) {
        return row != 0 && col != COLUMNS - 1 && tileMap.get(Coordinates.getCoordinates(col + 1, row - 1)).isBomb();
    }

    private boolean isBombOnTheRight(final int row, final int col) {
        return col != COLUMNS - 1 && tileMap.get(Coordinates.getCoordinates(col + 1, row)).isBomb();
    }

    private boolean isBombOnTheBottomRight(final int row, final int col) {
        return row != ROWS - 1 && col != COLUMNS - 1 && tileMap.get(Coordinates.getCoordinates(col + 1, row + 1)).isBomb();
    }

    private boolean isBombOnTheBottom(final int row, final int col) {
        return row != ROWS - 1 && tileMap.get(Coordinates.getCoordinates(col, row + 1)).isBomb();
    }

    private boolean isBombOnTheBottomLeftCorner(final int row, final int col) {
        return row != ROWS - 1 && col != 0 && tileMap.get(Coordinates.getCoordinates(col - 1, row + 1)).isBomb();
    }

    private boolean isBombOnTheLeft(final int row, final int col) {
        return col != 0 && tileMap.get(Coordinates.getCoordinates(col - 1, row)).isBomb();
    }

    private boolean isBombOnTheTopLeftCorner(final int row, final int col) {
        return row != 0 && col != 0 && tileMap.get(Coordinates.getCoordinates(col - 1, row - 1)).isBomb();
    }

    public void setFlag(Tile tile) {
        tile.setFlagged(true);
        tile.setImage("flag");
    }

    public void removeFlag(Tile tile) {
        tile.setFlagged(false);
        tile.setImage("cover");
    }

    private void openTile(Tile tile) {
        if (tile.isBomb()) {
            lose();
        } else {
            int bombsAround = countBombsAround(tile);
            switch (bombsAround) {
                case 0:
                    tile.setImage("empty");
                    break;
                case 1:
                    tile.setImage("oneMineAround");
                    break;
                case 2:
                    tile.setImage("twoMinesAround");
                    break;
                case 3:
                    tile.setImage("threeMinesAround");
                    break;
                case 4:
                    tile.setImage("fourMinesAround");
                    break;
                case 5:
                    tile.setImage("fiveMinesAround");
                    break;
                case 6:
                    tile.setImage("sixMinesAround");
                    break;
                case 7:
                    tile.setImage("sevenMinesAround");
                    break;
                case 8:
                    tile.setImage("eightMinesAround");
                    break;
                case 9:
                    tile.setImage("mine");
                    break;
            }
        }
        openTiles.add(tile);
        if (openTiles.size() == ROWS * COLUMNS - BOMBS) {
            win();
        }
        System.out.printf("row: %d, col: %d%n", tile.getCoordinates().getY(), tile.getCoordinates().getX());
    }

    private void lose() {
        bombs.forEach(bomb -> bomb.setImage("mine"));
        System.out.println("oh no you have died!");
    }

    private void win() {
        System.out.println("congratulations! you're the best minesweeper!");
    }

}