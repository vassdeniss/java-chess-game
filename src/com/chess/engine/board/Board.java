package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class Board {
    // STUB
    private final List<Tile> gameBoard;

    // Board Constructor
    private Board(Builder builder) {
        this.gameBoard = createGameBoard(builder);
    }

    // STUB
    public Tile getTile(final int tileCoordinate) {
        return null;
    }

    // Method for creating a game board
    // STUB
    private static List<Tile> createGameBoard(final Builder builder) {
        // Creates a tile array from 0 to 63
        final Tile[] tiles = new Tile[BoardUtils.TILES];

        // Loops trough the tile array and for each number get the piece associated with
        // number i from the map and create a tile from it
        for (int i = 0; i < BoardUtils.TILES; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfiguration.get(i));
        }

        // Return immutable list
        return ImmutableList.copyOf(tiles);
    }

    // STUB
    public static Board createStandardBoard() {

    }

    // Builder Class (Design Pattern)
    public static class Builder {
        // Map containing the positions of each piece
        Map<Integer, Piece> boardConfiguration;
        // Alliance variable for next player
        Alliance nextMoveDecider;

        // Constructor
        // STUB
        public Builder() {

        }

        // Method for setting the piece and the position of it
        // inside the board config list
        public Builder setPiece(final Piece piece) {
            this.boardConfiguration.put(piece.getPiecePosition(), piece);
            return this;
        }

        // Method for deciding next palyer
        public Builder setMoveDecider(final Alliance alliance) {
            this.nextMoveDecider = alliance;
            return this;
        }

        // Gets the built board (normal builder class behaviour)
        public Board build() {
            return new Board(this);
        }
    }
}