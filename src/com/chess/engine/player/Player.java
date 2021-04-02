package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        this.isInCheck = !Player.calculateAttackOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    // Expose player king
    public King getPlayerKing() { return this.playerKing; }

    // Expose own legal moves
    public Collection<Move> getLegalMoves() { return this.legalMoves; }

    // Method for calculating check
    // Pass in the player's king position and a collection of all possible enemy moves
    // If an enemy move overlaps the king's tile position
    // Add it to the attack moves list and return it
    // If the list is not empty the player is in check
    private static Collection<Move> calculateAttackOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }

        return ImmutableList.copyOf(attackMoves);
    }

    // Method for looping through the active pieces
    // if a king is found return it
    // if not throw a runtime exception
    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Not a valid board!");
    }

    // Method for checking is a move is contained in
    // the collection of moves
    // STUB
    public boolean isMoveLegal(final Move move) { return this.legalMoves.contains(move); }

    // Method for checking if you're in check
    public boolean isInCheck() { return this.isInCheck; }

    // Method for checking if you're in checkmate
    public boolean isInCheckMate() { return this.isInCheck && !hasEscapeMoves(); }

    // Method for checking escape moves in case of checkmate
    // Loop through our possible moves
    // Make the move on a new off-game board
    // If the move succeeds the checkmate is escapable
    // Otherwise it is not escapable if the move fails
    private boolean hasEscapeMoves() {
        for (final Move move : legalMoves) {
            final MakeTransition transition = makeMove(move);

            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }

        return false;
    }

    // Method for checking if you're in stalemate
    public boolean isInStaleMate() { return !isInCheck && !hasEscapeMoves(); }

    // Method for checking if you're castled
    // STUB
    public boolean isCastled() { return false; }

    // Method for making the move
    // If the move is illegal return the same board
    // Check if the enemy player will be in check if he makes the move
    // and if he is return the same board
    // Otherwise make the move and return a new board
    public MakeTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MakeTransition(this.board, move, MoveStatus.ILLEGAL);
        }

        final Board transitionBoard = move.execute();

        final Collection<Move> kingAttacks = Player.calculateAttackOnTile(transitionBoard
                .currentPlayer().getPlayerKing().getPiecePosition(),
                transitionBoard.currentPlayer().getLegalMoves()
        );

        if (!kingAttacks.isEmpty()) {
            return new MakeTransition(this.board, move, MoveStatus.CHECK);
        }

        return new MakeTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
}