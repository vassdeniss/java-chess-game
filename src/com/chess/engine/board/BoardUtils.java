package com.chess.engine.board;

public class BoardUtils {
    public static final int TILES = 64;
    public static final int TILES_ROW = 8;

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[TILES];

        // Puts the number we specified into the array
        // Loops trough the 8x8 board setting every tile on the
        // column to true
        do {
            column[columnNumber] = true;
            columnNumber += TILES_ROW;
        } while(columnNumber < TILES);

        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[TILES];

        // Puts the number we specified into the array
        // Loops trough the 8x8 board setting every tile on the
        // row to true
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % TILES_ROW != 0);

        return row;
    }


    // Declaring boolean arrays for exceptions (columns)
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    // Declaring boolean arrays for exceptions (rows)
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] SEVENTH_ROW = initRow(45);

    // Throw a runtime exception on instance of class
    private BoardUtils() { throw new RuntimeException("You cannot instance me!"); }

    // Method for validating coordinates
    public static boolean isValidTileCoordinate(final int coordinates) { return coordinates >= 0 && coordinates < TILES; }
}