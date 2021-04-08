package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;

import java.util.Collection;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board,
                       final Collection<Move> standardWhiteMoves,
                       final Collection<Move> standardBlackMoves) {
        super(board, standardWhiteMoves, standardBlackMoves);
    }

    // Override and expose method fields
    @Override
    public Collection<Piece> getActivePieces() { return this.board.getWhitePieces(); }
    @Override
    public Alliance getAlliance() { return Alliance.WHITE; }
    @Override
    public Player getOpponent() { return this.board.blackPlayer(); }
}