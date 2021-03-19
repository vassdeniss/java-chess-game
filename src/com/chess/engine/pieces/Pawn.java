package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {
    // No comments on this class as it is not finished

    private final static int[] POSSIBLE_MOVE_COORDINATES = {8, 16};

    Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            final int possibleDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentPossibleMove);

            if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) {
                continue;
            }

            if (currentPossibleMove == 8 && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(/*placeholder*/);
            } else if (
                    currentPossibleMove == 16
                    && this.isFirstMove()
                    && (BoardUtils.SECOND_ROW(this.piecePosition)
                        && this.pieceAlliance.isBlack())
                    || (BoardUtils.SEVENTH_ROW(this.piecePosition)
                        && this.pieceAlliance.isWhite())
                    ) {
                final int behindPossibleDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindPossibleDestinationCoordinate).isTileOccupied
                    && !board.getTile(possibleDestinationCoordinate).isTileOccupied) {
                    legalMoves.add(/*placeholder*/);
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }
}
