package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MakeTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.MoveFactory;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final JFrame takenPiecesFrame;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private boolean highlightMoves;

    // Tile colors
    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");
    private final Color whiteTileColor = Color.WHITE;
    private final Color blackTileColor = Color.BLACK;
    private Color chosenColorOne = lightTileColor;
    private Color chosenColorTwo = darkTileColor;

    private static String defaultPieceImagePath = "src/com/chess/art/pieces/";

    // Dimensions
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    public Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setResizable(false);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.takenPiecesFrame = new JFrame("Taken Pieces");
        this.takenPiecesFrame.setResizable(false);
        this.takenPiecesFrame.setLayout(new BorderLayout());
        this.takenPiecesFrame.setSize(300, 600);
        this.takenPiecesFrame.add(this.takenPiecesPanel);
        this.takenPiecesFrame.setVisible(true);
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightMoves = false;
        //this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createDevMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        final JMenuItem exitGame = new JMenuItem("Exit");

        // Open PGN Option
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { System.out.println("You pressed on the load pgn button"); }
        });
        fileMenu.add(openPGN);

        // Exit Game Option
        exitGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        });
        fileMenu.add(exitGame);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        final JCheckBoxMenuItem legalMovesHighligher = new JCheckBoxMenuItem("Highlight Moves", false);

        // Flip Board Option
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard, chosenColorOne, chosenColorTwo);
            }
        });
        preferencesMenu.add(flipBoard);

        preferencesMenu.addSeparator();

        // Highlight Moves Option
        legalMovesHighligher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { highlightMoves = legalMovesHighligher.isSelected(); }
        });
        preferencesMenu.add(legalMovesHighligher);

        return preferencesMenu;
    }

    private JMenu createDevMenu() {
        final JMenu devMenu = new JMenu("Dev");
        final JCheckBoxMenuItem openHistory = new JCheckBoxMenuItem("Move History Panel", true);
        final JCheckBoxMenuItem openTaken = new JCheckBoxMenuItem("Taken Pieces Panel", true);
        final JMenuItem normalTheme = new JMenuItem("Default Theme");
        final JMenuItem blackWhite = new JMenuItem("Black & White Theme");
        final JMenuItem customTheme = new JMenuItem("Custom Theme");

        openHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!openHistory.isSelected()) {
                    gameHistoryPanel.setVisible(false);
                } else if (openHistory.isSelected()) {
                    gameHistoryPanel.setVisible(true);
                }
            }
        });
        devMenu.add(openHistory);

        openTaken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!openTaken.isSelected()) {
                    takenPiecesFrame.setVisible(false);
                } else if (openTaken.isSelected()) {
                    takenPiecesFrame.setVisible(true);
                }
            }
        });
        devMenu.add(openTaken);

        devMenu.addSeparator();

        normalTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenColorOne = lightTileColor;
                chosenColorTwo = darkTileColor;
                boardPanel.drawBoard(chessBoard, chosenColorOne, chosenColorTwo);
            }
        });
        devMenu.add(normalTheme);

        blackWhite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenColorOne = whiteTileColor;
                chosenColorTwo = blackTileColor;
                boardPanel.drawBoard(chessBoard, chosenColorOne, chosenColorTwo);
            }
        });
        devMenu.add(blackWhite);

        devMenu.addSeparator();

        customTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorOne = JOptionPane.showInputDialog(gameFrame, "Please input hex code for the light color: ", "Light color", JOptionPane.QUESTION_MESSAGE);
                if (colorOne != null && !colorOne.equals("")) {
                    if (colorOne.charAt(0) == '#' && colorOne.length() == 7) {
                        String colorTwo = JOptionPane.showInputDialog(gameFrame, "Please input hex code for the dark color: ", "Dark color", JOptionPane.QUESTION_MESSAGE);
                        if (colorTwo != null && !colorTwo.equals("")) {
                            if (colorTwo.charAt(0) == '#' && colorTwo.length() == 7) {
                                chosenColorOne = Color.decode(colorOne);
                                chosenColorTwo = Color.decode(colorTwo);
                            } else {
                                JOptionPane.showMessageDialog(null, "Not a valid color hex!", "Error", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Not a valid color hex!", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }

                boardPanel.drawBoard(chessBoard, chosenColorOne, chosenColorTwo);
            }
        });
        devMenu.add(customTheme);

        return devMenu;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) { return boardTiles; }
            @Override
            BoardDirection opposite() { return FLIPPED; }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) { return Lists.reverse(boardTiles); }
            @Override
            BoardDirection opposite() { return NORMAL; }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();

            for (int i = 0; i < BoardUtils.TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        // Method to refresh GUI
        public void drawBoard(final Board board, final Color light, final Color dark) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board, light, dark);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;
        MoveLog() { this.moves = new ArrayList<>(); }
        public List<Move> getMoves() { return this.moves; }
        public void addMove(final Move move) { this.moves.add(move); }
        public int size() { return this.moves.size(); }
        public void clear() { this.moves.clear(); }
        public boolean removeMove(final Move move) { return this.moves.remove(move); }
        public Move removeMove(int index) { return this.moves.remove(index); }
    }

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor(chosenColorOne, chosenColorTwo);
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    // Right button cancels choice
                    if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    // Left button selects a piece
                    } else if (isLeftMouseButton(e)) {
                        // Selecting the tile if there is a piece on it
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        // Executing move on second tile selection i.e destination
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),
                                                                          destinationTile.getTileCoordinate());
                            final MakeTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard, chosenColorOne, chosenColorTwo);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });
            validate();
        }

        private void highlightLegals(final Board board) {
            if (highlightMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("src/com/chess/art/other/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.legalMoves(board);
            }
            return Collections.emptyList();
        }

        // Draw the tiles on the board
        public void drawTile(final Board board, final Color light, final Color dark) {
            assignTileColor(light, dark);
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        // Assign icons to the pieces
        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath
                            + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1)
                            + board.getTile(this.tileId).getPiece().toString() + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Assign colors based on tile oddness or evenness
        public void assignTileColor(final Color lightTileColor, final Color darkTileColor) {
            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
