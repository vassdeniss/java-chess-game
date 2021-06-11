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

public class Bishop extends Piece {
    // int array *not* for the legal moves but instead numbers to add or subtract
    // from current position to get one of the possible diagonal vectors
    private static final int[] POSSIBLE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, true);
    }

    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        // Go over every diagonal vectors then for each of them
        // check if the piece is in the first or eight column
        // if it is break the loop
        // if its not check if its a valid tile
        // if it is a valid tile and has an enemy piece on it break the loop and add the move
        // if it is a valid tile and it does not have an enemy piece add the move and continue
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
                        legalMoves.add(new NormalMove(
                                board,
                                this,
                                possibleDestinationCoordinates)
                        );
                    } else {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if (pieceAlliance != this.pieceAlliance) {
                            legalMoves.add(new AttackMove(
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
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    @Override
    public String toString() { return pieceType.BISHOP.toString(); }

    // Bishop Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (
                possibleOffset == -9
                || possibleOffset == 7
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (
                possibleOffset == 9
                || possibleOffset == -7
        );
    }
}