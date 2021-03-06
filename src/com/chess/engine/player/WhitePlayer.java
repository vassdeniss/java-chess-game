package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.KingSideCastleMove;
import static com.chess.engine.board.Move.QueenSideCastleMove;

public class WhitePlayer extends Player {
    public WhitePlayer(final Board board,
                       final Collection<Move> standardWhiteMoves,
                       final Collection<Move> standardBlackMoves) {
        super(board, standardWhiteMoves, standardBlackMoves);
    }

    // Expose method fields
    @Override
    public Collection<Piece> getActivePieces() { return this.board.getWhitePieces(); }
    @Override
    public Alliance getAlliance() { return Alliance.WHITE; }
    @Override
    public Player getOpponent() { return this.board.blackPlayer(); }

    @Override
    public String toString() { return "White"; }

    // Method for calculating king castle moves depending on the castling chess rules
    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
        if (!hasCastleOpportunities()) { return Collections.emptyList(); }

        final List<Move> kingCastles = new ArrayList<>();

        if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()) {
            // White King Side Castle
            if (this.board.getPiece(61) == null && this.board.getPiece(62) == null) {
                final Piece kingSideRook = this.board.getPiece(63);
                if (kingSideRook != null && kingSideRook.isFirstMove()) {
                    if (Player.calculateAttackOnTile(61, opponentLegals).isEmpty() &&
                            Player.calculateAttackOnTile(62, opponentLegals).isEmpty() &&
                            kingSideRook.getPieceType().isRook()) {
                        if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
                            kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62, (Rook) kingSideRook, kingSideRook.getPiecePosition(), 61));
                        }
                    }
                }
            }
            // White Queen Side Castle
            if (this.board.getPiece(59) == null && this.board.getPiece(58) == null &&
                    this.board.getPiece(57) == null) {
                final Piece queenSideRook = this.board.getPiece(56);
                if (queenSideRook != null && queenSideRook.isFirstMove()) {
                    if (Player.calculateAttackOnTile(58, opponentLegals).isEmpty() &&
                            Player.calculateAttackOnTile(59, opponentLegals).isEmpty() && queenSideRook.getPieceType().isRook()) {
                        if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
                            kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58, (Rook) queenSideRook, queenSideRook.getPiecePosition(), 59));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(kingCastles);
    }
}