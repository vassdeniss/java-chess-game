package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;

import java.util.Collection;

public class WhitePlayer extends Player {
    public WhitePlayer(Board board,
                       Collection<Move> standardWhiteMoves,
                       Collection<Move> standardBlackMoves) {
        super(board, standardWhiteMoves, standardBlackMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }
}
