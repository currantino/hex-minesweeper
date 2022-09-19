package com.github.currantino;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.*;
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
            int col = ThreadLocalRandom.current().nextInt(0, COLUMNS - 1);
            int row = ThreadLocalRandom.current().nextInt(0, ROWS - 1);
            Coordinates coordinates = new Coordinates(col, row);
            Tile tile = tileMap.get(coordinates);
            if (tile == null || bombs.contains(tile)) i -= 1;
            else tileToBomb(tile);
        }
    }

    void tileToBomb(Tile tile) {
        tile.setBomb(true);
        tile.setImage("mine");
        bombs.add(tile);
    }

    public int countBombsAround(Tile tile) {
        return (int) getNeighboursOf(tile).stream().filter(Optional::isPresent).map(Optional::get).filter(Tile::isBomb).count();
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
                    openTilesAroundOf(tile);
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

    private void openTilesAroundOf(Tile tile) {
    }

    private Set<Optional<Tile>> getNeighboursOf(Tile tile) {
        Set<Optional<Tile>> neighbours = new HashSet<>();
        int col = tile.getCoordinates().getX();
        int row = tile.getCoordinates().getY();


        neighbours.add(getTile(row + 1, col));
        neighbours.add(getTile(row - 1, col));
        neighbours.add(getTile(row, col + 1));
        neighbours.add(getTile(row, col - 1));
        if (row % 2 == 0) {
            neighbours.add(getTile(row - 1, col - 1));
            neighbours.add(getTile(row + 1, col - 1));
        } else {
            neighbours.add(getTile(row - 1, col + 1));
            neighbours.add(getTile(row + 1, col + 1));
        }
        return neighbours;
    }

    private Optional<Tile> getTile(int row, int col) {
        return Optional.ofNullable(tileMap.get(Coordinates.getCoordinates(col, row)));
    }


}