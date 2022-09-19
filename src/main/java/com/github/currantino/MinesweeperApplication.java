package com.github.currantino;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
Пулы потоков бывают:
 FixedThreadPool - при создании задается конкретное число потоков в этом пуле. При передачи задачи в пул - берется любой свободный поток оттуда.
 CachedThreadPool - его размер определяется автоматически JVM в зависимости от количества свободных ресурсов процессора и памяти.
 **/


public class MinesweeperApplication extends Application {

    private final static double r = 20; // the inner radius from hexagon center to outer corner
    private final static double innerRadius = Math.sqrt(r * r * 0.75); // the inner radius from hexagon center to middle of the axis
    private static final int ROWS = 10; // how many rows of tiles should be created
    private static final int COLUMNS = 10; // the amount of tiles that are contained in each row
    private final static int WINDOW_WIDTH = ROWS * 50;
    private final static int WINDOW_HEIGHT = COLUMNS * 40;
    private final int BOMBS = ROWS * COLUMNS / 10;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private AnchorPane board = new AnchorPane();
    private Map<Coordinates, Tile> tileMap;
    private Set<Tile> bombs;
    private Set<Tile> openTiles;
    private Scene content;
    private Stage root;
    private BorderPane borderPane = new BorderPane(board);

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage root) {
        tileMap = new HashMap<>(ROWS * COLUMNS);
        bombs = new HashSet<>();
        openTiles = new HashSet<>();
        this.root = root;
        newGame();
    }

    private void newGame() {
        content = new Scene(borderPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setScene(content);
        root.setTitle("hex-minesweeper");
        fillBoardWithTiles();
        plantBombs();
        root.show();
    }

    private void fillBoardWithTiles() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {

                Tile tile = new Tile(row, col);
                Coordinates coordinates = new Coordinates(col, row);
                tileMap.put(coordinates, tile);
                tile.setOnMouseClicked(new TileMouseClickHandler(tile));
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
        bombs.add(tile);
    }

    public int countBombsAround(Tile tile) {
        if (tile.getBombsAround() == -1) {
            int bombsAround = (int) getNeighboursOf(tile).stream().filter(Optional::isPresent).map(Optional::get).filter(Tile::isBomb).count();
            tile.setBombsAround(bombsAround);
            return bombsAround;
        } else {
            return tile.getBombsAround();
        }
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
        if (openTiles.contains(tile)) {
            return;
        }
        if (tile.isBomb()) {
            lose();
        } else {

            switch (countBombsAround(tile)) {
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
                default:
                    break;
            }
        }
        openTiles.add(tile);
        if (openTiles.size() == ROWS * COLUMNS - BOMBS) {
            win();
        }
    }

    private void lose() {
        bombs.forEach(bomb -> bomb.setImage("mine"));
        System.out.println("oh no you have died!");
        endGame();
    }

    private void win() {
        System.out.println("congratulations! you're the best minesweeper!");
        endGame();
    }

    private void endGame() {
        tileMap.values().stream().filter(tile -> tile.isFlagged() && !tile.isBomb()).forEach(tile -> tile.setImage("incorrectFlag"));
        tileMap.values().forEach(tile -> tile.setOnMouseClicked(null));
    }

    private void openTilesAroundOf(Tile tile) {
        getNeighboursOf(tile).stream().filter(Optional::isPresent).map(Optional::get).forEach(neighbour -> threadPool.submit(() -> openTile(neighbour)));
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

    private class TileMouseClickHandler implements EventHandler<MouseEvent> {
        private Tile tile;

        public TileMouseClickHandler(Tile tile) {
            this.tile = tile;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                openTile(tile);
            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                if (tile.isFlagged()) removeFlag(tile);
                else setFlag(tile);
            }
        }
    }
}