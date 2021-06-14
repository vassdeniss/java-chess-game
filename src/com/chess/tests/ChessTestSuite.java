package com.chess.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestPieces.class,
        TestBoard.class,
        TestStaleMate.class,
        TestPlayer.class,
        TestCheckMate.class,
        TestMiniMax.class,
        TestAlphaBeta.class,
        TestCastling.class,
        TestPawnStructure.class,
        TestFENParser.class,
        TestEngine.class,
        TestPieces.class})
public class ChessTestSuite {
}