package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class King extends Piece {
    // For an explanation of the King Class
    // please refer to the Knight Class
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    King(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            final int possibleDestinationCoordinate = this.piecePosition + currentPossibleMove;

            if (isFirstColumnExclusion(this.piecePosition, currentPossibleMove)
                || isEightColumnExclusion(this.piecePosition, currentPossibleMove)) {
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinate)) {
                final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinates);
                if (!possibleDestinationTile.isTileOccupied()) {
                    legalMoves.add(new NormalMove(
                            board,
                            this,
                            possibleDestinationCoordinate)
                    );
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (pieceAlliance != this.pieceAlliance) {
                        legalMoves.add(new AttackMove(
                                board,
                                this,
                                possibleDestinationCoordinate,
                                pieceAtDestination)
                        );
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    // King Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (
                possibleOffset == -1
                || possibleOffset == -9
                || possibleOffset == 7
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (
                possibleOffset == 1
                || possibleOffset == 9
                || possibleOffset == -7
        );
    }
}