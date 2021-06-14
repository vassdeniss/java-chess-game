package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.chess.gui.Table.MoveLog;

public class TakenPiecesPanel extends JPanel {
    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 8));
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog) {
        this.northPanel.removeAll();
        this.southPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move move : moveLog.getMoves()) {
            if (move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if (takenPiece.getPieceAlliance().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else if (takenPiece.getPieceAlliance().isBlack()) {
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("Whoops! This should not happen!");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(final Piece o1, final Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, (o1, o2) -> Ints.compare(o1.getPieceValue(), o2.getPieceValue()));

        for (final Piece takenPiece : whiteTakenPieces) {
            StringBuilder path = new StringBuilder();
            Character alliance = takenPiece.getPieceAlliance().toString().charAt(0);
            String piece = takenPiece.toString();
            path.append("pieces/"); path.append(alliance); path.append(piece); path.append(".png");
            URL pieceImage = ClassLoader.getSystemResource(path.toString());
            final ImageIcon icon = new ImageIcon(pieceImage);
            final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                    icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
            this.southPanel.add(imageLabel);
        }

        for (final Piece takenPiece : blackTakenPieces) {
            StringBuilder path = new StringBuilder();
            Character alliance = takenPiece.getPieceAlliance().toString().charAt(0);
            String piece = takenPiece.toString();
            path.append("pieces/"); path.append(alliance); path.append(piece); path.append(".png");
            URL pieceImage = ClassLoader.getSystemResource(path.toString());
            final ImageIcon icon = new ImageIcon(pieceImage);
            final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                    icon.getIconWidth() - 15, icon.getIconWidth() - 15, Image.SCALE_SMOOTH)));
            this.northPanel.add(imageLabel);
        }

        validate();
    }
}
