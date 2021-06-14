package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {
    private final static int[] POSSIBLE_MOVE_COORDINATES = {7, 8, 9, 16};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public int locationBonus() { return this.pieceAlliance.pawnBonus(this.piecePosition); }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        // Loop through all possible moves
        // Note*: 8 is normal move, 16 is jump, 7 and 9 are diagonal attack moves
        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            int possibleDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentPossibleMove);
            // if the tile is valid continue
            if (!BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) { continue; }
            // if the move is 8 (normal) and the tile is not occupied
            // check if the tile is a pawn promotion tile and promote it
            // otherwise add a normal move
            if (currentPossibleMove == 8 && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                if (this.pieceAlliance.isPawnPromotionTile(possibleDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, possibleDestinationCoordinate), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, possibleDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, possibleDestinationCoordinate), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, possibleDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, possibleDestinationCoordinate), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, possibleDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, possibleDestinationCoordinate), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, possibleDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, possibleDestinationCoordinate));
                }
            // Determine the move is 16 (jump) (only executable on first move)
            // Then determine wheres the pawn i.e if its black and on second row or
            // white on seventh row
            } else if (currentPossibleMove == 16 && this.isFirstMove() &&
                    ((BoardUtils.INSTANCE.SECOND_ROW.get(this.piecePosition) && this.pieceAlliance.isBlack())
                            || (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.piecePosition) && this.pieceAlliance.isWhite()))) {
                // get for the tile behind the jumped tile
                final int behindPossibleDestinationCoordinate = this.piecePosition
                        + (this.pieceAlliance.getDirection() * 8);
                // if the behind tile is not occupied and the jump tile is not occupied
                // add a jump move
                if (!board.getTile(behindPossibleDestinationCoordinate).isTileOccupied()
                        && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, possibleDestinationCoordinate));
                }
                // If the attack is 7 (diagonal attack right)
                // and if the piece is white and not on the eight column (impossible diagonal attack)
                // or if the piece is black and not on the first column (impossible diagonal attack)
                // continue
            } else if (currentPossibleMove == 7 &&
                    !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.INSTANCE.FIFTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                // if the tile is occupied check if the tile is a pawn promotion tile and promote it
                // otherwise add a normal move
                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionTile(possibleDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnTile), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnTile), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnTile), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnTile), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, possibleDestinationCoordinate)));
                        } else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate, pieceOnTile));
                        }
                    }
                // if an en passant pawn exists calculate the possible capture move
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                possibleDestinationCoordinate, pieceOnCandidate)
                        );
                    }
                }
                // If the attack is 9 (diagonal attack left)
                // and if the piece is white and not on the first column (impossible diagonal attack)
                // or if the piece is black and not on the eight column (impossible diagonal attack)
                // continue
            } else if (currentPossibleMove == 9 &&
                    !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()) ||
                            (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()))) {
                // if the tile is occupied check if the tile is a pawn promotion tile and promote it
                // otherwise add a normal move
                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnTile.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionTile(possibleDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate,
                                            board.getPiece(possibleDestinationCoordinate)), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate,
                                            board.getPiece(possibleDestinationCoordinate)), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate,
                                            board.getPiece(possibleDestinationCoordinate)), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, possibleDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate,
                                            board.getPiece(possibleDestinationCoordinate)), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, possibleDestinationCoordinate)));
                        }
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, possibleDestinationCoordinate,
                                            board.getPiece(possibleDestinationCoordinate)));
                        }
                    }
                // if an en passant pawn exists calculate the possible capture move
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                possibleDestinationCoordinate, pieceOnCandidate)
                        );
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() { return pieceType.PAWN.toString(); }

    public Piece getPromotionPiece() {
        return new Queen(this.piecePosition, this.pieceAlliance, false);
    }
}