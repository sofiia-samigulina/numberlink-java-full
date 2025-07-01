package sk.tuke.gamestudio.game.numberlink.core;

import java.util.ArrayList;
import java.util.List;

public class Link {

    private final int number;
    private final List<Tile> tiles;

    public Link(Tile[][] fieldTiles, int number) {
        this.number = number;
        tiles = extractTiles(fieldTiles, number);
    }

    public boolean isValid() {
        int tilesCount = tiles.size();
        if (tilesCount <= 1) {
            return false;
        }

        return isLink(tiles);
    }

    // order Tiles Into Link to check if link is valid
    private boolean isLink(List<Tile> tiles) {
        List<Tile> visitedTiles = new ArrayList<>();

        // 1st readonly tile, will be head of the link
        Tile currentTile = tiles.stream().filter(Tile::isReadonly).findFirst().orElse(null);
        if (currentTile == null) {
            return false;
        }

        do {
            visitedTiles.add(currentTile);
            Tile finalCurrentTile = currentTile;
            currentTile = tiles.stream().filter(tile -> {
                if (visitedTiles.contains(tile)) {
                    return false;
                }

                if (finalCurrentTile.getRow() == tile.getRow()
                        && !(finalCurrentTile.getCol() == tile.getCol() + 1
                        || finalCurrentTile.getCol() == tile.getCol() - 1)) {
                    return false;
                }

                if (finalCurrentTile.getCol() == tile.getCol()
                        && !(finalCurrentTile.getRow() == tile.getRow() + 1
                        || finalCurrentTile.getRow() == tile.getRow() - 1)) {
                    return false;
                }

                if (finalCurrentTile.getCol() != tile.getCol() && finalCurrentTile.getRow() != tile.getRow()) {
                    return false;
                }

                return true;
            }).findFirst().orElse(null);

        } while (currentTile != null && !currentTile.isReadonly());

        return currentTile != null && currentTile.isReadonly();
    }

    private List<Tile> extractTiles(Tile[][] fieldTiles, int number) {
        List<Tile> tiles = new ArrayList<>();

        for (int row = 0; row < fieldTiles.length; row++) {
            for (int col = 0; col < fieldTiles[row].length; col++) {
                if (fieldTiles[row][col].getNumber() == number) {
                    tiles.add(fieldTiles[row][col]);
                }
            }
        }

        return tiles;
    }
}
