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

public class Queen extends Piece {
    private static final int[] POSSIBLE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    Queen(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int possibleOffset : POSSIBLE_MOVE_VECTOR_COORDINATES) {
            int possibleDestinationCoordinates = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(possibleDestinationCoordinates)) {
                if (isFirstColumnExclusion(possibleDestinationCoordinates, possibleOffset)
                        || isEightColumnExclusion(possibleDestinationCoordinates, possibleOffset)) {
                    break;
                }

                possibleDestinationCoordinates += possibleOffset;

                if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinates)) {
                    final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinates);

                    if (!possibleDestinationTile.isTileOccupied()) {
                        legalMoves.add(new Move.NormalMove(
                                board,
                                this,
                                possibleDestinationCoordinates)
                        );
                    } else {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if (pieceAlliance != this.pieceAlliance) {
                            legalMoves.add(new Move.AttackMove(
                                    board,
                                    this,
                                    possibleDestinationCoordinates,
                                    pieceAtDestination)
                            );
                        }

                        break;
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (
                possibleOffset == -9
                || possibleOffset == 7
                || possibleOffset == -1
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (
                possibleOffset == 9
                || possibleOffset == -7
                || possibleOffset == 1
        );
    }
}
