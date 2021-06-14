package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.player.MakeTransition;

import java.util.concurrent.atomic.AtomicLong;

import static com.chess.engine.board.Move.*;

public final class MiniMax implements MoveStrategy {

    private final BoardEvaluator evaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private long executionTime;
    private FreqTableRow[] freqTable;
    private int freqTableIndex;

    public MiniMax(final int searchDepth) {
        this.evaluator = StandardBoardEvaluator.get();
        this.boardsEvaluated = 0;
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() { return "MiniMax"; }
    @Override
    public long getNumBoardsEvaluated() { return this.boardsEvaluated; }

    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = MoveFactory.getNullMove();
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        this.freqTable = new FreqTableRow[board.currentPlayer().getLegalMoves().size()];
        this.freqTableIndex = 0;
        int moveCounter = 1;
        final int numMoves = board.currentPlayer().getLegalMoves().size();
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            if (makeTransition.getMoveStatus().isDone()) {
                final FreqTableRow row = new FreqTableRow(move);
                this.freqTable[this.freqTableIndex] = row;
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(makeTransition.getToBoard(), this.searchDepth - 1) :
                        max(makeTransition.getToBoard(), this.searchDepth - 1);
                this.freqTableIndex++;
                if (board.currentPlayer().getAlliance().isWhite() &&
                        currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() &&
                        currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
            moveCounter++;
        }
        this.executionTime = System.currentTimeMillis() - startTime;
        return bestMove;
    }

    private int min(final Board board,
                    final int depth) {
        if (depth == 0) {
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.evaluator.evaluate(board, depth);
        }
        if (isEndGameScenario(board)) { return this.evaluator.evaluate(board, depth); }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            if (makeTransition.getMoveStatus().isDone()) {
                final int currentValue = max(makeTransition.getToBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) { lowestSeenValue = currentValue; }
            }
        }
        return lowestSeenValue;
    }

    private int max(final Board board,
                    final int depth) {
        if (depth == 0) {
            this.boardsEvaluated++;
            this.freqTable[this.freqTableIndex].increment();
            return this.evaluator.evaluate(board, depth);
        }
        if (isEndGameScenario(board)) { return this.evaluator.evaluate(board, depth); }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MakeTransition makeTransition = board.currentPlayer().makeMove(move);
            if (makeTransition.getMoveStatus().isDone()) {
                final int currentValue = min(makeTransition.getToBoard(), depth - 1);
                if (currentValue >= highestSeenValue) { highestSeenValue = currentValue; }
            }
        }
        return highestSeenValue;
    }

    private static boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
    }

    private static class FreqTableRow {
        private final Move move;
        private final AtomicLong count;

        FreqTableRow(final Move move) {
            this.count = new AtomicLong();
            this.move = move;
        }

        void increment() { this.count.incrementAndGet(); }

        @Override
        public String toString() {
            return BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getCurrentCoordinate()) +
                    BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : " + this.count;
        }
    }
}