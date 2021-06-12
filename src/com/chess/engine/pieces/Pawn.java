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
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        // Loop through all possible moves
        // Note*: 8 is normal move, 16 is jump, 7 and 9 are diagonal attack moves
        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            final int possibleDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentPossibleMove);
            // if the tile is valid continue
            if (!BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) { continue; }
            // if the move is 8 (normal) and the tile is not occupied
            // add a normal move
            if (currentPossibleMove == 8 && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(new PawnMove(board, this, possibleDestinationCoordinate));
                // Determine the move is 16 (jump) (only executable on first move)
                // Then determine wheres the pawn i.e if its black and on second row or
                // white on seventh row
            } else if (currentPossibleMove == 16 && this.isFirstMove() &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack())
                            || (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))) {
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
                    !((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()))) {
                // if the tile is occupied add an attack move
                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceAtTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceAtTile.getPieceAlliance()) {
                        legalMoves.add(new PawnAttackMove(board, this,
                                possibleDestinationCoordinate, pieceAtTile)
                        );
                    }
                }
                // If the attack is 9 (diagonal attack left)
                // and if the piece is white and not on the first column (impossible diagonal attack)
                // or if the piece is black and not on the eight column (impossible diagonal attack)
                // continue
            } else if (currentPossibleMove == 9 &&
                    !((BoardUtils.EIGHT_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()))) {
                // if the tile is occupied add an attack move
                if (board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceAtTile = board.getTile(possibleDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceAtTile.getPieceAlliance()) {
                        legalMoves.add(new PawnAttackMove(board, this,
                                possibleDestinationCoordinate, pieceAtTile)
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
}