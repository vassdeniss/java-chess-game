package com.chess.engine.board;

public class BoardUtils {
    // Declaring boolean arrays for exceptions
    // *currently empty*
    public static final boolean[] FIRST_COLUMN = null;
    public static final boolean[] SECOND_COLUMN = null;
    public static final boolean[] SEVENTH_COLUMN = null;
    public static final boolean[] EIGHT_COLUMN = null;

    // Throw a runtime exception on instance of class
    private BoardUtils() {
        throw new RuntimeException("You cannot instance me!");
    }

    // Method for validating coordiantes
    public static boolean isValidTileCoordinate(int coordinates) {
        return coordinates >= 0 && coordinates < 64;
    }
}
