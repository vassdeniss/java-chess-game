package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MakeTransition;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() { return "MiniMax"; }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();

        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            if (makeTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(makeTransition.getTransitionBoard(), this.searchDepth - 1) :
                        max(makeTransition.getTransitionBoard(), this.searchDepth - 1);
                if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;

        return bestMove;
    }

    public int min(final Board board, final int depth) {
        // Variable for lowest seen value
        int lowestSeenValue = Integer.MAX_VALUE;
        if (depth == 0 || isEndGameScenario(board)) { return this.boardEvaluator.evaluate(board, depth); }
        // Loop through every move of the current player
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            // Make every move
            if (makeTransition.getMoveStatus().isDone()) {
                final int currentValue = max(makeTransition.getTransitionBoard(), depth - 1);
                // Evaluate the move to the lowest value and update if necessary
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    public int max(final Board board, final int depth) {
        // Variable for highest seen value
        int highestSeenValue = Integer.MIN_VALUE;
        if (depth == 0 || isEndGameScenario(board)) { return this.boardEvaluator.evaluate(board, depth); }
        // Loop through every move of the current player
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            // Make every move
            if (makeTransition.getMoveStatus().isDone()) {
                final int currentValue = min(makeTransition.getTransitionBoard(), depth - 1);
                // Evaluate the move to the highest value and update if necessary
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().getOpponent().isInCheckMate();
    }
}
