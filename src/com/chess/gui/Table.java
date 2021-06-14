package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MakeTransition;
import com.chess.engine.player.ai.StockAlphaBeta;
import com.chess.pgn.FenUtilities;
import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

import static com.chess.engine.board.Move.MoveFactory;
import static com.chess.pgn.PGNUtilities.writeGameToPGNFile;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final JFrame takenPiecesFrame;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;
    private Move computerMove;
    private boolean highlightMoves;

    // Tile colors
    private final Color defauLightTileColor = Color.decode("#FFFACD");
    private final Color defauDarkTileColor = Color.decode("#593E1A");
    private final Color defaultBorderColor = Color.decode("#8B4726");
    private final Color whiteTileColor = Color.WHITE;
    private final Color blackTileColor = Color.BLACK;
    private final Color blackBorderColor = Color.BLACK;

    private Color lightTileColor = defauLightTileColor;
    private Color darkTileColor = defauDarkTileColor;
    private Color BORDER_COLOR = defaultBorderColor;

    ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icon.png"));

    // Dimensions
    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private static final Table INSTANCE = new Table();

    private Table() {
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setIconImage(icon.getImage());
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
        this.takenPiecesFrame.setIconImage(icon.getImage());
        this.takenPiecesFrame.setLayout(new BorderLayout());
        this.takenPiecesFrame.setSize(300, 600);
        this.takenPiecesFrame.add(this.takenPiecesPanel);
        this.takenPiecesFrame.setVisible(true);
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAiWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameSetup.setTitle("Game Setup");
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightMoves = false;
        //this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    public static Table get() { return INSTANCE; }
    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistory().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPieces().redo(Table.get().moveLog);
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private JFrame getGameFrame() { return this.gameFrame; }
    private GameSetup getGameSetup() { return gameSetup; }
    private Board getGameBoard() { return this.chessBoard; }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private static class TableGameAiWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                    !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    public void updateGameBoard(final Board board) { this.chessBoard = board; }
    public void updateComputerMove(final Move move) { this.computerMove = move; }
    private MoveLog getMoveLog() { return this.moveLog; }
    private GameHistoryPanel getGameHistory() { return this.gameHistoryPanel; }
    private TakenPiecesPanel getTakenPieces() { return this.takenPiecesPanel; }
    private BoardPanel getBoardPanel() { return this.boardPanel; }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() { }

        @Override
        protected Move doInBackground() {
            final Move bestMove;
            final StockAlphaBeta strategy = new StockAlphaBeta(Table.get().getGameSetup().getSearchDepth());
            bestMove = strategy.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistory().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPieces().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem saveFen = new JMenuItem("Save Game (FEN)");
        final JMenuItem savePGN = new JMenuItem("Save Game (PGN)");
        final JMenuItem openFEN = new JMenuItem("Load Game (FEN)");
        final JMenuItem exitGame = new JMenuItem("Exit");

        // Save Game (FEN) Option
        saveFen.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() { return ".txt"; }
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith("txt");
                }
            });
            final int option = chooser.showSaveDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION) { saveFENFile(chooser.getSelectedFile()); }
        });
        fileMenu.add(saveFen);

        // Load Game (FEN) Option
        openFEN.addActionListener(e -> {
            String path = "";
            String result = "";
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION) { path = chooser.getSelectedFile().getAbsolutePath(); }
            try { result = loadFENFile(path); }
            catch (IOException ioException) { ioException.printStackTrace(); }
            undoAllMoves();
            chessBoard = FenUtilities.createGameFromFEN(result);
            Table.get().getBoardPanel().drawBoard(chessBoard);
        });
        fileMenu.add(openFEN);

        // Save Game (PGN) Option
        savePGN.addActionListener(e -> {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public String getDescription() { return ".pgn"; }
                @Override
                public boolean accept(final File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith("pgn");
                }
            });
            final int option = chooser.showSaveDialog(Table.get().getGameFrame());
            if (option == JFileChooser.APPROVE_OPTION) { savePGNFile(chooser.getSelectedFile()); }
        });
        fileMenu.add(savePGN);

        // Exit Game Option
        exitGame.addActionListener(e -> System.exit(0));
        exitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK));
        fileMenu.add(exitGame);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenu colorMenu = new JMenu("Choose Theme");
        final JCheckBoxMenuItem openHistory = new JCheckBoxMenuItem("Move History Panel", true);
        final JCheckBoxMenuItem openTaken = new JCheckBoxMenuItem("Taken Pieces Panel", true);
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        final JCheckBoxMenuItem legalMovesHighligher = new JCheckBoxMenuItem("Highlight Moves", false);
        final JMenuItem normalTheme = new JMenuItem("Default Theme");
        final JMenuItem blackWhite = new JMenuItem("Black & White Theme");
        final JMenuItem chooseLight = new JMenuItem("Choose Light Tile Color");
        final JMenuItem chooseDark = new JMenuItem("Choose Dark Tile Color");
        final JMenuItem chooseBorder = new JMenuItem("Choose Border Color");

        preferencesMenu.add(colorMenu);

        // Move History Panel Option
        openHistory.addActionListener(e -> {
            if (!openHistory.isSelected()) {
                gameHistoryPanel.setVisible(false);
            } else if (openHistory.isSelected()) {
                gameHistoryPanel.setVisible(true);
            }
        });
        preferencesMenu.add(openHistory);

        // Taken Pieces Panel Option
        openTaken.addActionListener(e -> {
            if (!openTaken.isSelected()) { takenPiecesFrame.setVisible(false); }
            else if (openTaken.isSelected()) { takenPiecesFrame.setVisible(true); }
        });
        preferencesMenu.add(openTaken);

        // Flip Board Option
        flipBoard.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        flipBoard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        preferencesMenu.add(flipBoard);

        // Highlight Moves Option
        legalMovesHighligher.addActionListener(e-> highlightMoves = legalMovesHighligher.isSelected());
        legalMovesHighligher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        preferencesMenu.add(legalMovesHighligher);

        // Default Theme Option
        normalTheme.addActionListener(e -> {
            Table.get().getBoardPanel().setTileLightColor(chessBoard, defauLightTileColor);
            Table.get().getBoardPanel().setTileDarkColor(chessBoard, defauDarkTileColor);
            Table.get().getBoardPanel().setBackground(defaultBorderColor);
        });
        colorMenu.add(normalTheme);

        // Black & White Theme Option
        blackWhite.addActionListener(e -> {
            Table.get().getBoardPanel().setTileLightColor(chessBoard, whiteTileColor);
            Table.get().getBoardPanel().setTileDarkColor(chessBoard, blackTileColor);
            Table.get().getBoardPanel().setBackground(blackBorderColor);
        });
        colorMenu.add(blackWhite);

        colorMenu.addSeparator();

        // Choose Light Tile Color Option
        chooseLight.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Light Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileLightColor(chessBoard, colorChoice);
            }
        });
        colorMenu.add(chooseLight);

        // Choose Dark Tile Color Option
        chooseDark.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setTileDarkColor(chessBoard, colorChoice);
            }
        });
        colorMenu.add(chooseDark);

        // Choose Border Color Option
        chooseBorder.addActionListener(e -> {
            final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Color",
                    Table.get().getGameFrame().getBackground());
            if (colorChoice != null) {
                Table.get().getBoardPanel().setBackground(colorChoice);
            }
        });
        colorMenu.add(chooseBorder);

        return preferencesMenu;
    }

    final JMenu createOptionsMenu() {
        final JMenu optionMenu = new JMenu("Options");
        final JMenuItem resetGame = new JMenuItem("New Game");
        final JMenuItem setupGame = new JMenuItem("Setup Game");
        final JMenuItem undoMove = new JMenuItem("Undo Last Move");

        // Reset Game Option
        resetGame.addActionListener(e -> undoAllMoves());
        optionMenu.add(resetGame);

        // Setup Game Option
        setupGame.addActionListener(e ->  {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });
        setupGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        optionMenu.add(setupGame);

        // Undo Move Option
        undoMove.addActionListener(e -> {
            if (Table.get().getMoveLog().size() > 0) { undoLastMove(); }
        });
        undoMove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
        optionMenu.add(undoMove);

        return optionMenu;
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
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(BORDER_COLOR);
            validate();
        }

        void setTileLightColor(final Board board, final Color lightColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setLightTileColor(lightColor);
            }
            drawBoard(board);
        }

        void setTileDarkColor(final Board board, final Color darkColor) {
            for (final TilePanel boardTile : boardTiles) {
                boardTile.setDarkTileColor(darkColor);
            }
            drawBoard(board);
        }

        // Method to refresh GUI
        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
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

    enum PlayerType {HUMAN, COMPUTER}

    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            highlightTileBorder(chessBoard);
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) ||
                            BoardUtils.isEndGame(Table.get().getGameBoard())) {
                        return;
                    }
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
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(() -> {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            //if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                Table.get().moveMadeUpdate(PlayerType.HUMAN);
                            //}
                            boardPanel.drawBoard(chessBoard);
                        });
                    }
                }

                @Override
                public void mousePressed(final MouseEvent e) { }
                @Override
                public void mouseReleased(final MouseEvent e) { }
                @Override
                public void mouseEntered(final MouseEvent e) { }
                @Override
                public void mouseExited(final MouseEvent e) { }
            });
            validate();
        }

        private void highlightLegals(final Board board) {
            if (highlightMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("other/green_dot.png"))));
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
        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightTileBorder(board);
            highlightLegals(board);
            highlightAIMove();
            validate();
            repaint();
        }

        // Assign icons to the pieces
        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                StringBuilder path = new StringBuilder();
                Character alliance = board.getTile(this.tileId).getPiece().getPieceAlliance().toString().charAt(0);
                String piece = board.getTile(this.tileId).getPiece().toString();
                path.append("pieces/"); path.append(alliance); path.append(piece); path.append(".png");
                URL pieceImage = ClassLoader.getSystemResource(path.toString());
                add(new JLabel(new ImageIcon(pieceImage)));
            }
        }

        // Assign colors based on tile oddness or evenness
        public void assignTileColor() {
            if (BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId) ||
                    BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId) ||
                    BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId) ||
                    BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId)) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId) ||
                    BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId) ||
                    BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId)  ||
                    BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId)) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        private void highlightAIMove() {
            if (computerMove != null) {
                if (this.tileId == computerMove.getCurrentCoordinate()) {
                    setBackground(Color.pink);
                } else if(this.tileId == computerMove.getDestinationCoordinate()) {
                    setBackground(Color.red);
                }
            }
        }

        private void highlightTileBorder(final Board board) {
            if (humanMovedPiece != null &&
                    humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance() &&
                    humanMovedPiece.getPiecePosition() == this.tileId) {
                setBorder(BorderFactory.createLineBorder(Color.cyan));
            } else {
                setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }

        void setLightTileColor(final Color color) { lightTileColor = color; }
        void setDarkTileColor(final Color color) { darkTileColor = color; }
    }

    private void undoLastMove() {
        final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
        this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
        this.computerMove = null;
        Table.get().getMoveLog().removeMove(lastMove);
        Table.get().getGameHistory().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPieces().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }

    private void undoAllMoves() {
        for (int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
            final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
            this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
        }
        this.computerMove = null;
        Table.get().getMoveLog().clear();
        Table.get().getGameHistory().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPieces().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(chessBoard);
    }

    private static void savePGNFile(final File pgnFile) {
        try { writeGameToPGNFile(pgnFile, Table.get().getMoveLog()); }
        catch (final IOException e) { e.printStackTrace(); }
    }

    private static void saveFENFile(final File pgnFile) {
        try { FenUtilities.createFENFromGame(pgnFile, Table.get().chessBoard); }
        catch (final IOException e) { e.printStackTrace(); }
    }

    private static String loadFENFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
