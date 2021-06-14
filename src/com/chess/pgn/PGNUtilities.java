package com.chess.pgn;

import com.chess.engine.board.Move;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.chess.gui.Table.MoveLog;

public class PGNUtilities {
    public static void writeGameToPGNFile(final File pgnFile, final MoveLog moveLog) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append(calculateEventString()).append("\n");
        builder.append(calculateDateString()).append("\n");
        builder.append(calculatePlyCountString(moveLog)).append("\n");
        for (final Move move : moveLog.getMoves()) { builder.append(move.toString()).append(" "); }
        try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pgnFile, true)))) {
            writer.write(builder.toString());
        }
    }

    private static String calculateEventString() { return "[Event \"" + "vass's chess game" + "\"]"; }
    private static String calculateDateString() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return "[Date \"" + dateFormat.format(new Date()) + "\"]";
    }
    private static String calculatePlyCountString(final MoveLog moveLog) {
        return "[PlyCount \"" + moveLog.size() + "\"]";
    }
}
