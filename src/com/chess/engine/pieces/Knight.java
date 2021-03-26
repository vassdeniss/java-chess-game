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

public class Knight extends Piece {
    // int array for possible knight moves
    private final static int[] POSSIBLE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        // Loop through the array mentioned on top
        for (final int currentPossibleMove : POSSIBLE_MOVE_COORDINATES) {
            final int possibleDestinationCoordinates = currentPossibleMove + this.piecePosition;
            // if its a valid in bounds tile continue
            if (BoardUtils.isValidTileCoordinate(possibleDestinationCoordinates)) {
                // if it fits in the exclusion continue
                if (isFirstColumnExclusion(this.piecePosition, currentPossibleMove)
                    || isSecondColumnExclusion(this.piecePosition, currentPossibleMove)
                    || isSeventhColumnExclusion(this.piecePosition, currentPossibleMove)
                    || isEightColumnExclusion(this.piecePosition, currentPossibleMove)) {
                    continue;
                }

                final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinates);

                // if it is not occupied add a normal move
                if (!possibleDestinationTile.isTileOccupied()) {
                    legalMoves.add(new NormalMove(
                            board,
                            this,
                            possibleDestinationCoordinates)
                    );
                // else get the tile piece color and type...
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    // and if its a different color add an attack move
                    if (pieceAlliance != this.pieceAlliance) {
                        legalMoves.add(new AttackMove(
                                board,
                                this,
                                possibleDestinationCoordinates,
                                pieceAtDestination)
                        );
                    }
                }
            }
        }

        // Return the move list (immutable)
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return pieceType.KNIGHT.toString();
    }

    // Knight Exclusions
    private static boolean isFirstColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (
                possibleOffset == -17
                || possibleOffset == -10
                || possibleOffset == 6
                || possibleOffset == 15
        );
    }

    private static boolean isSecondColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && (
                possibleOffset == -10
                || possibleOffset == 6
        );
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (
                possibleOffset == -6
                || possibleOffset == 10
        );
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int possibleOffset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (
                possibleOffset == 10
                || possibleOffset == 17
        );
    }
}