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

public class Rook extends Piece {
    // For an explanation of the Rook Class
    // please refer to the Bishop Class

    private static final int[] POSSIBLE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};

    public Rook(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.ROOK, piecePosition, pieceAlliance);
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

    @Override
    public String toString() { return pieceType.ROOK.toString(); }

    // Rook Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (possibleOffset == -1);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (possibleOffset == 1);
    }
}