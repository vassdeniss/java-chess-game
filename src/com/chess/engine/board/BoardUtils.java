package com.chess.engine.board;

import com.google.common.collect.ImmutableBiMap;

import java.util.HashMap;
import java.util.Map;

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
        // Loops trough the 8x8 board setting every tile on the row to true
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % TILES_ROW != 0);

        return row;
    }

    // Creating columns
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHT_COLUMN = initColumn(7);

    // Creating rows
    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(8);
    public static final boolean[] SIXTH_RANK = initRow(16);
    public static final boolean[] FIFTH_RANK = initRow(24);
    public static final boolean[] FOURTH_RANK = initRow(32);
    public static final boolean[] THIRD_RANK = initRow(40);
    public static final boolean[] SECOND_RANK = initRow(48);
    public static final boolean[] FIRST_RANK = initRow(56);

    public static final String[] ALGEBRAIC_NOTATION = inilialiseAlgebreicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initialisePositionCoordinateMap();


    private static String[] inilialiseAlgebreicNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
        };
    }

    private static Map<String, Integer> initialisePositionCoordinateMap() {
        final Map<String, Integer> positionCoordinate = new HashMap<>();
        for (int i = 0; i < TILES; i++) {
            positionCoordinate.put(ALGEBRAIC_NOTATION[i], i);
        }
        return ImmutableBiMap.copyOf(positionCoordinate);
    }

    private BoardUtils() { throw new RuntimeException("You cannot instance me!"); }

    // Method for validating coordinates
    public static boolean isValidTileCoordinate(final int coordinates) { return coordinates >= 0 && coordinates < TILES; }
    // Methods for returning algebraic notation
    public static int getCoordinateAtPostiion(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }
    /*
    public static String getPostionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION(coordinate);
    }
    */
}