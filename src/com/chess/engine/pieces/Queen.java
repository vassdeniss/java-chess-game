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

public class Queen extends Piece {
    // For an explanation of the Queen Class
    // please refer to the Bishop Class

    private static final int[] POSSIBLE_MOVE_VECTOR_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public Queen(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, true);
    }

    public Queen(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public int locationBonus() { return this.pieceAlliance.queenBonus(this.piecePosition); }

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
                        legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinates));
                    } else {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if (pieceAlliance != this.pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this,
                                    possibleDestinationCoordinates, pieceAtDestination)
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
    public Queen movePiece(Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() { return pieceType.QUEEN.toString(); }

    // Queen Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (
                possibleOffset == -9
                || possibleOffset == 7
                || possibleOffset == -1
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (
                possibleOffset == 9
                || possibleOffset == -7
                || possibleOffset == 1
        );
    }
}
