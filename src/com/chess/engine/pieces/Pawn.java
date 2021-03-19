package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {
    // No comments on this class as it is not finished

    private final static int[] POSSIBLE_MOVE_COORDINATES = {8};

    Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            int possibleDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentPossibleMove);

            if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) {
                continue;
            }

            if (currentPossibleMove == 8 && !board.getTile(possibleDestinationCoordinate).isTileOccupied()) {
                legalMoves.add(/*placeholder*/);
            }
        }
    }
}
