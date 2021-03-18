package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    // Int for tile location
    // int tileCoordinate - mutable - not good for a lot of reasons
    // i.e (garbage collection, memory etc...)
    protected final int tileCoordinate;
    // Protected i.e only subclases can access it
    // final - once set it cannot be overwritten

    // Dictionary basically
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllEmptyTiles();

    // Method to set empty tiles on grid
    private static Map<Integer, EmptyTile> createAllEmptyTiles() {
        // Create a hashmap
        final Map<Integer, EmptyTile> tileMap = new HashMap<>();

        // Loop 64 times (chess grid = 8x8) and put each number in the dictionary
        for (int i = 0; i < BoardUtils.TILES; i++) {
            tileMap.put(i, new EmptyTile(i));
        }

        // Return immutable map from the Guava library
        return ImmutableMap.copyOf(tileMap);
    }

    // The only constructor than will be used
    public static Tile createTile(final int coordinate, final Piece piece) {
        return piece != null ? new OccupiedTile(coordinate, piece) : EMPTY_TILES_CACHE.get(coordinate);
    }

    // Constructor for setting location
    private Tile(final int tileCoordinate) { this.tileCoordinate = tileCoordinate; }

    // True or false depending if piece is on tile
    // Get the piece if its occupied
    public abstract boolean isTileOccupied();
    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile {
        private EmptyTile(int coordinate) { super(coordinate); }

        // Return false - no piece on empty tile
        @Override
        public boolean isTileOccupied() { return false; }

        // Return null - no piece on empty tile
        @Override
        public Piece getPiece() { return null; }
    }

    public static final class OccupiedTile extends Tile {
        // Type of Piece for recognizing the piece
        // private - only for this subclass
        private final Piece tilePiece;

        private OccupiedTile(int coordinate, final Piece tilePiece) {
            super(coordinate);
            this.tilePiece = tilePiece;
        }

        // Return true - there will be always a piece on the tile
        @Override
        public boolean isTileOccupied() { return true; }

        // Return tilePiece - there will be always a piece on the tile
        @Override
        public Piece getPiece() { return this.tilePiece; }
    }
}