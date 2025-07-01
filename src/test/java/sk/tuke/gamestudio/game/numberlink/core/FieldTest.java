package sk.tuke.gamestudio.game.numberlink.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FieldTest {

    private Field field;

    //testmap
    @BeforeEach
    public void setUp() {
        field = new Field("console");
        FieldMap map = new FieldMap();
        List<Tile> tiles = new ArrayList<>();
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
        field.loadMap(map);
    }

    @Test
    public void testLoadMap() {
        Assertions.assertEquals(5, field.getRowCount(), "Incorrect row count");
        Assertions.assertEquals(5, field.getColumnCount(), "Incorrect column count");
        Assertions.assertNotNull(field.getTile(0, 0), "Tile 0 doesn't exist");
    }

    @Test
    public void testSetTile() {
        boolean result = field.setTile(field,3, 1, 1);
        Assertions.assertTrue(result, "Tile didn't set correctly");
        Assertions.assertEquals(1, field.getTile(3, 1).getNumber(), "Numbers don't match");
    }

    @Test
    public void testSetTileOutOfBounds() {
        boolean result = field.setTile(field,10, 10, 3);
        Assertions.assertFalse(result, "Tile couldn't set correctly");
    }

    @Test
    public void testIsGameWonInitially() {
        boolean result = field.isGameWon();
        Assertions.assertFalse(result, "Game couldn't be initially won");
    }

    @Test
    public void testBoundaryCheck() {
        boolean result = field.setTile(field,-1, -1, 3);
        Assertions.assertFalse(result, "Tile couldn't set correctly");
    }
}
