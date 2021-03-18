package com.chess.engine.board;

public class BoardUtils {
    public static final int TILES = 64;
    public static final int TILES_ROW = 8;

    public static boolean[] initColumn(int columnNumber) {
        // Creates a boolean array
        final boolean[] column = new boolean[TILES];

        // Puts the number we specified into the array
        // Loops trough the 8x8 board setting every tile on the
        // row to true
        do {
            column[columnNumber] = true;
            columnNumber += TILES_ROW;
        } while(columnNumber < TILES);

        return column;
    }

    // Declaring boolean arrays for exceptions
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    // Throw a runtime exception on instance of class
    private BoardUtils() { throw new RuntimeException("You cannot instance me!"); }

    // Method for validating coordiantes
    public static boolean isValidTileCoordinate(final int coordinates) { return coordinates >= 0 && coordinates < TILES; }
}