package sk.tuke.gamestudio.game.numberlink.core;

public class Tile {

    private int row;
    private int col;

    private int number = 0;
    private boolean readonly = false;

    public Tile() {
    }

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Tile(int row, int col, int number) {
        this.row = row;
        this.col = col;
        this.number = number;
        readonly = true;
    }

    public boolean isEmpty() {
        return number <= 0;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (!readonly) {
            this.number = number;
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return ".";
        }

        return String.valueOf(number);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
