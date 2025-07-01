package sk.tuke.gamestudio.game.numberlink.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Field {
    private int rowCount;
    private int columnCount;
    private Tile[][] tiles;
    private int numberCount;
    private String ui;

    private GameState state = GameState.OFF;

    public Field(String UI) {
        this.ui = UI;
    }

    public void loadMap(FieldMap map) {
        rowCount = map.getRowCount();
        columnCount = map.getColCount();
        numberCount = map.getNumberCount();

        tiles = new Tile[rowCount][columnCount];

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                tiles[row][col] = new Tile(row, col);
            }
        }

        for (Tile tile : map.getTiles()) {
            tiles[tile.getRow()][tile.getCol()] = new Tile(tile.getRow(), tile.getCol(), tile.getNumber());
        }
    }

    public boolean isGameWon() {
        if (hasEmptyTiles()) {
            return false;
        }

        for (int i = 0; i < numberCount; i++) {
            Link link = new Link(tiles, i + 1);
            if (!link.isValid()) {
                return false;
            }
        }

        state = GameState.SOLVED;
        return true;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Tile getTile(int row, int column) {
        return tiles[row][column];
    }

    public boolean setTile(Field field, int row, int column, int number) {
        if (boundariesCheck(row, column) && (number>0 || number<=numberCount) && !(field.getTile(row,column).isReadonly())) {
            tiles[row][column].setNumber(number);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        int numberOfDigits = Math.max(2,(int) Math.log10(columnCount) + 2);
        String template = "%-" + numberOfDigits + "s";

        StringBuilder sb = new StringBuilder((rowCount + 1) + (columnCount + 1));

        sb.append(String.format(template, ""));
        sb.append(String.format(template, ""));
        for (int col = 0; col < columnCount; col++) {
            sb.append(String.format(template, col + 1));
        }

        sb.append("\n");

        sb.append(String.format(template, ""));
        sb.append(String.format(template, ""));
        for (int col = 0; col < columnCount; col++) {
            sb.append(String.format(template, "_"));
        }
        sb.append("\n");

        for (int row = 0; row < rowCount; row++) {
            sb.append(String.format(template, row + 1));
            sb.append(String.format(template, "| "));
            for (int col = 0; col < columnCount; col++) {
                sb.append(String.format(template, tiles[row][col].toString()));
            }
            sb.append(String.format(template, "| "));
            sb.append("\n");
        }

        sb.append(String.format(template, ""));
        sb.append(String.format(template, ""));
        for (int col = 0; col < columnCount; col++) {
            sb.append(String.format(template, "_"));
        }

        return sb.toString();
    }

    private boolean boundariesCheck(int x, int y) {
        if ((x >= 0 && x < rowCount) && (y >= 0 && y < columnCount)) {
            return true;
        }
        return false;
    }

    private boolean hasEmptyTiles() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (tiles[i][j].isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private String getUI () {
        return ui;
    }

    private void setUI (String UI) {
        this.ui = UI;
    }

    private List<FieldMap> loadMaps() {
        List<FieldMap> maps = new ArrayList<>();

        //1
        FieldMap map = new FieldMap();
        List<Tile> tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(0, 0, 1));
        tiles.add(new Tile(3, 2, 1));
        tiles.add(new Tile(1, 1, 2));
        tiles.add(new Tile(4, 0, 2));
        tiles.add(new Tile(2, 1, 3));
        tiles.add(new Tile(4, 4, 3));
        tiles.add(new Tile(1, 4, 4));
        tiles.add(new Tile(3, 3, 4));
        tiles.add(new Tile(0, 4, 5));
        tiles.add(new Tile(2, 3, 5));
        map.setTiles(tiles);
        maps.add(map);

        //2
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(3, 1, 1));
        tiles.add(new Tile(1, 3, 1));
        tiles.add(new Tile(0, 1, 2));
        tiles.add(new Tile(0, 4, 2));
        tiles.add(new Tile(0, 0, 3));
        tiles.add(new Tile(4, 1, 3));
        tiles.add(new Tile(1, 4, 4));
        tiles.add(new Tile(3, 2, 4));
        tiles.add(new Tile(3, 3, 5));
        tiles.add(new Tile(4, 2, 5));
        map.setTiles(tiles);
        maps.add(map);

        //3
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(2, 0, 1));
        tiles.add(new Tile(3, 3, 1));
        tiles.add(new Tile(3, 4, 2));
        tiles.add(new Tile(4, 0, 2));
        tiles.add(new Tile(0, 2, 3));
        tiles.add(new Tile(1, 1, 3));
        tiles.add(new Tile(1, 4, 4));
        tiles.add(new Tile(2, 1, 4));
        tiles.add(new Tile(0, 4, 5));
        tiles.add(new Tile(1, 2, 5));
        map.setTiles(tiles);
        maps.add(map);

        //4
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(3, 0, 1));
        tiles.add(new Tile(3, 3, 1));
        tiles.add(new Tile(1, 2, 2));
        tiles.add(new Tile(3, 2, 2));
        tiles.add(new Tile(0, 1, 3));
        tiles.add(new Tile(2, 0, 3));
        tiles.add(new Tile(0, 2, 4));
        tiles.add(new Tile(1, 3, 4));
        tiles.add(new Tile(2, 2, 5));
        tiles.add(new Tile(4, 4, 5));
        map.setTiles(tiles);
        maps.add(map);

        //5
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(0, 2, 1));
        tiles.add(new Tile(4, 4, 1));
        tiles.add(new Tile(3, 3, 2));
        tiles.add(new Tile(4, 0, 2));
        tiles.add(new Tile(1, 3, 3));
        tiles.add(new Tile(2, 1, 3));
        tiles.add(new Tile(2, 0, 4));
        tiles.add(new Tile(3, 2, 4));
        tiles.add(new Tile(0, 1, 5));
        tiles.add(new Tile(1, 2, 5));
        map.setTiles(tiles);
        maps.add(map);

        //6
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(1, 2, 1));
        tiles.add(new Tile(4, 2, 1));
        tiles.add(new Tile(1, 4, 2));
        tiles.add(new Tile(4, 3, 2));
        tiles.add(new Tile(0, 4, 3));
        tiles.add(new Tile(3, 3, 3));
        tiles.add(new Tile(0, 2, 4));
        tiles.add(new Tile(3, 1, 4));
        tiles.add(new Tile(0, 0, 5));
        tiles.add(new Tile(4, 1, 5));
        map.setTiles(tiles);
        maps.add(map);

        //7
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(0, 4, 1));
        tiles.add(new Tile(2, 0, 1));
        tiles.add(new Tile(1, 3, 2));
        tiles.add(new Tile(2, 1, 2));
        tiles.add(new Tile(3, 1, 3));
        tiles.add(new Tile(3, 4, 3));
        tiles.add(new Tile(3, 0, 4));
        tiles.add(new Tile(4, 4, 4));
        tiles.add(new Tile(1, 4, 5));
        tiles.add(new Tile(2, 2, 5));
        map.setTiles(tiles);
        maps.add(map);

        //8
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(2, 1, 1));
        tiles.add(new Tile(4, 3, 1));
        tiles.add(new Tile(3, 2, 2));
        tiles.add(new Tile(4, 2, 2));
        tiles.add(new Tile(0, 0, 3));
        tiles.add(new Tile(0, 4, 3));
        tiles.add(new Tile(1, 3, 4));
        tiles.add(new Tile(2, 0, 4));
        tiles.add(new Tile(1, 4, 5));
        tiles.add(new Tile(4, 4, 5));
        map.setTiles(tiles);
        maps.add(map);

        //9
        map = new FieldMap();
        tiles = new ArrayList<>();
        map.setRowCount(5);
        map.setColCount(5);
        map.setNumberCount(5);
        tiles.add(new Tile(3, 0, 1));
        tiles.add(new Tile(4, 4, 1));
        tiles.add(new Tile(2, 0, 2));
        tiles.add(new Tile(3, 3, 2));
        tiles.add(new Tile(2, 2, 3));
        tiles.add(new Tile(3, 4, 3));
        tiles.add(new Tile(0, 0, 4));
        tiles.add(new Tile(1, 3, 4));
        tiles.add(new Tile(0, 1, 5));
        tiles.add(new Tile(1, 4, 5));
        map.setTiles(tiles);
        maps.add(map);

        return maps;
    }

    public FieldMap getMap() {
        List<FieldMap> maps = loadMaps();
        Random random = new  Random();
        return maps.get(random.nextInt(maps.size()));
    }

    public int countScore(Field field, long startTime, long endTime, int steps, int startScore) {
        float countEmpty = (field.getRowCount()* field.getColumnCount())-(field.getMap().getNumberCount()*2);
        int defaultTime=0;
        if (ui == "console") {
           defaultTime = (int)(5*countEmpty);
        }
        if (ui == "web") {
            defaultTime = (int)(0.5*countEmpty);
        }
        int time_points=0;
        int currentTime = (int)(endTime-startTime)/1000;
        if (currentTime > defaultTime) {
            time_points = currentTime-defaultTime;
        }
        int res = startScore - ( ( (steps-(int)countEmpty) *5) + time_points);
        if (res<=0) {
            res = 0;
        }
        return res;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
