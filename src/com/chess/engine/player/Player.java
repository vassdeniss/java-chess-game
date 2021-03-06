package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> playerLegals,
           final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttackOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(playerLegals, calculateKingCastles(playerLegals, opponentMoves)));
    }

    // Expose fields
    public King getPlayerKing() { return this.playerKing; }
    public Collection<Move> getLegalMoves() { return this.legalMoves; }

    // Method for calculating check
    // Pass in the player's king position and a collection of all possible enemy moves
    // If an enemy move overlaps the king's tile position
    // Add it to the attack moves list and return it
    // If the list is not empty the player is in check
    protected static Collection<Move> calculateAttackOnTile(final int piecePosition, final Collection<Move> moves) {
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
        King result = null;
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                result = (King) piece;
                break;
            }
        }
        if (result == null) {
            throw new RuntimeException("Not a valid board!");
        }
        return result;
    }

    // Method for checking is a move is contained in
    // the collection of moves
    public boolean isMoveLegal(final Move move) { return this.legalMoves.contains(move); }
    public boolean isInCheck() { return this.isInCheck; } // Method for checking if you're in check
    public boolean isInCheckMate() { return this.isInCheck && !hasEscapeMoves(); } // Method for checking if you're in checkmate
    public boolean isKingSideCastleCapable() { return this.playerKing.isKingSideCastleCapable(); }
    public boolean isQueenSideCastleCapable() { return this.playerKing.isQueenSideCastleCapable(); }

    // Method for checking escape moves in case of checkmate
    // Loop through our possible moves
    // Make the move on a new off-game board
    // If the move succeeds the checkmate is escapable
    // Otherwise it is not escapable if the move fails
    private boolean hasEscapeMoves() {
        for (final Move move : legalMoves) {
            final MakeTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) { return true; }
        }
        return false;
    }

    public boolean isInStaleMate() { return !isInCheck && !hasEscapeMoves(); } // Method for checking if you're in stalemate
    public boolean isCastled() { return false; } // Method for checking if you're castled

    // Method for making the move
    // If the move is illegal return the same board
    // Check if the enemy player will be in check if he makes the move
    // and if he is return the same board
    // Otherwise make the move and return a new board
    public MakeTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MakeTransition(this.board, this.board, move, MoveStatus.ILLEGAL);
        }

        final Board transitionBoard = move.execute();

        final Collection<Move> kingAttacks =
                Player.calculateAttackOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                        transitionBoard.currentPlayer().getLegalMoves());

        if (!kingAttacks.isEmpty()) {
            return new MakeTransition(this.board, this.board, move, MoveStatus.CHECK);
        }

        return new MakeTransition(this.board, transitionBoard, move, MoveStatus.DONE);
    }

    public MakeTransition unMakeMove(final Move move) {
        return new MakeTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);
    protected boolean hasCastleOpportunities() {
        return !this.isInCheck && !this.playerKing.isCastled() &&
                (this.playerKing.isKingSideCastleCapable() || this.playerKing.isQueenSideCastleCapable());
    }
}