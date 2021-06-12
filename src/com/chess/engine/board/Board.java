package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;

public class Board {
    private final List<Tile> gameBoard; // List of tiles for the game board
    // Collection of white || black pieces
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    // Method fields for players
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    // Board Constructor
    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> standardWhiteMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> standardBlackMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, standardWhiteMoves, standardBlackMoves);
        this.blackPlayer = new BlackPlayer(this, standardWhiteMoves, standardBlackMoves);
        this.currentPlayer = builder.nextMoveDecider.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        // Loop through the board
        for (int i = 0; i < BoardUtils.TILES; i++) {
            // Method field for getting the pieces letter
            final String tileText = this.gameBoard.get(i).toString();
            // append the empty tile or the piece letter
            // separate by 3 spaces
            sb.append(String.format("%3s", tileText));

            // if i becomes 8 the modulus would be 0
            // i.e the row ended
            // add a new line
            if ((i + 1) % BoardUtils.TILES_ROW == 0) { sb.append("\n"); }
        }

        return sb.toString();
    }

    // Exposing fields
    public Player whitePlayer() { return this.whitePlayer; }
    public Player blackPlayer() { return this.blackPlayer; }
    public Player currentPlayer() { return this.currentPlayer; }
    public Collection<Piece> getBlackPieces() { return this.blackPieces; }
    public Collection<Piece> getWhitePieces() { return this.whitePieces; }
    public Pawn getEnPassantPawn() { return this.enPassantPawn; }

    // Method for making a list of all legal moves by looping through
    // the pieces and taking each ones moves and adding it to a list
    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (Piece piece : pieces) {
            legalMoves.addAll(piece.legalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    // Method for taking the pieces on the board
    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance pieceAlliance) {
        // List of active pieces
        final List<Piece> activePieces = new ArrayList<>();
        // For loop for going trough every single tile on the board and if the tile is occupied
        // take the piece and if that piece matches the color we gave it
        // add it to the list and return an immutable copy
        for (final Tile tile : gameBoard) {
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceAlliance() == pieceAlliance) {
                    activePieces.add(piece);
                }
            }
        }

        return ImmutableList.copyOf(activePieces);
    }

    // Get the tile on the specified int in the list of tiles
    public Tile getTile(final int tileCoordinate) { return gameBoard.get(tileCoordinate); }

    // Method for creating a game board
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

    // Build a standard chess board by assigning each piece
    // to its corresponding tile
    public static Board createStandardBoard() {
        final Builder builder = new Builder();

        // Black
        builder.setPiece(new Rook(0, Alliance.BLACK));
        builder.setPiece(new Knight(1, Alliance.BLACK));
        builder.setPiece(new Bishop(2, Alliance.BLACK));
        builder.setPiece(new Queen(3, Alliance.BLACK));
        builder.setPiece(new King(4, Alliance.BLACK));
        builder.setPiece(new Bishop(5, Alliance.BLACK));
        builder.setPiece(new Knight(6, Alliance.BLACK));
        builder.setPiece(new Rook(7, Alliance.BLACK));
        builder.setPiece(new Pawn(8, Alliance.BLACK));
        builder.setPiece(new Pawn(9, Alliance.BLACK));
        builder.setPiece(new Pawn(10, Alliance.BLACK));
        builder.setPiece(new Pawn(11, Alliance.BLACK));
        builder.setPiece(new Pawn(12, Alliance.BLACK));
        builder.setPiece(new Pawn(13, Alliance.BLACK));
        builder.setPiece(new Pawn(14, Alliance.BLACK));
        builder.setPiece(new Pawn(15, Alliance.BLACK));

        // White
        builder.setPiece(new Pawn(48, Alliance.WHITE));
        builder.setPiece(new Pawn(49, Alliance.WHITE));
        builder.setPiece(new Pawn(50, Alliance.WHITE));
        builder.setPiece(new Pawn(51, Alliance.WHITE));
        builder.setPiece(new Pawn(52, Alliance.WHITE));
        builder.setPiece(new Pawn(53, Alliance.WHITE));
        builder.setPiece(new Pawn(54, Alliance.WHITE));
        builder.setPiece(new Pawn(55, Alliance.WHITE));
        builder.setPiece(new Rook(56, Alliance.WHITE));
        builder.setPiece(new Knight(57, Alliance.WHITE));
        builder.setPiece(new Bishop(58, Alliance.WHITE));
        builder.setPiece(new Queen(59, Alliance.WHITE));
        builder.setPiece(new King(60, Alliance.WHITE));
        builder.setPiece(new Bishop(61, Alliance.WHITE));
        builder.setPiece(new Knight(62, Alliance.WHITE));
        builder.setPiece(new Rook(63, Alliance.WHITE));

        builder.setMoveDecider(Alliance.WHITE); // White to move first

        return builder.build();
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    // Builder Class
    public static class Builder {
        Map<Integer, Piece> boardConfiguration; // Map containing the positions of each piece
        Alliance nextMoveDecider;
        Pawn enPassantPawn;

        public Builder() { this.boardConfiguration = new HashMap<>(); } // Constructor

        // Method for setting the piece and the position of it
        // inside the board config list
        public Builder setPiece(final Piece piece) {
            this.boardConfiguration.put(piece.getPiecePosition(), piece);
            return this;
        }

        // Method for deciding next player
        public Builder setMoveDecider(final Alliance alliance) {
            this.nextMoveDecider = alliance;
            return this;
        }

        public Board build() { return new Board(this); } // Return the built board
        public void setEnPassantPawn(Pawn enPassantPawn) { this.enPassantPawn = enPassantPawn; }
    }
}