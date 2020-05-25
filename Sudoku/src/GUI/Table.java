package GUI;

import Board.Grid;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private final JFrame gameFrame;
    private Grid grid;
    private GridPanel gridPanel;
    private boolean isRainbowMode;
    private int difficultyLevel;
    private int subGridCandidateCoordinate;
    private int tileCandidateCoordinate;
    private TimePanel timePanel;
    private SidePanel sidePanel;
    private boolean isLeftTileClick;
    private boolean isRightTileClick;
    private boolean isFinalClick;

    private static final Random rgen = new Random();
    private static final String ART_PATH = "C:/Users/evcmo/intelliJWorkspace/Sudoku/art/numbers/";
    private static final String TEXT_PATH = "C:/Users/evcmo/intelliJWorkspace/Sudoku/text/";

    private static final Dimension GAME_FRAME_DIMENSION = new Dimension(700, 675);
    private static final Dimension SIDE_PANEL_DIMENSION = new Dimension(85, 650);
    private static final Dimension SELECTION_PANEL_DIMENSION = new Dimension(15, 15);
    private static final Dimension GRID_PANEL_DIMENSION = new Dimension(200, 200);
    private static final Dimension SUB_GRID_PANEL_DIMENSION = new Dimension(100, 100);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private static final BorderLayout TIME_PANEL_LAYOUT_MGR = new BorderLayout();
    private static final GridLayout SIDE_PANEL_LAYOUT_MGR = new GridLayout(9, 1);
    private static final GridBagLayout SELECTION_PANEL_LAYOUT_MGR = new GridBagLayout();
    private static final GridLayout GRID_PANEL_LAYOUT_MGR = new GridLayout(3, 3);
    private static final GridLayout SUB_GRID_PANEL_LAYOUT_MGR = new GridLayout(3, 3);
    private static final GridBagLayout TILE_PANEL_LAYOUT_MGR = new GridBagLayout();

    private static final Color DEFAULT_TILE_COLOR = new Color(150, 200, 200);
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

    private static final int LEVEL_ONE = 1;
    private static final int LEVEL_TWO = 2;
    private static final int LEVEL_THREE = 3;
    private static final int MS_PER_SECOND = 1000;
    private static final int RGB_LOWER_LIMIT = 55;
    private static final int RGB_UPPER_LIMIT = 200;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SELECTION_PANEL_ICON_SIZE = 95;
    private static final int TILE_PANEL_ICON_SIZE = 50;
    private static final int BORDER_THICKNESS = 5;
    private static final int PANELS_PER_GRID = 9;

    public Table() {

        this.gameFrame = new JFrame("JSudoku");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(GAME_FRAME_DIMENSION);
        final JMenuBar menuBar = createMenuBar();
        this.gameFrame.setJMenuBar(menuBar);
        this.difficultyLevel = LEVEL_ONE;
        this.grid = new Grid(this.difficultyLevel);
        this.timePanel = new TimePanel();
        this.gameFrame.add(timePanel, BorderLayout.NORTH);
        this.isRainbowMode = false;
        this.gridPanel = new GridPanel();
        this.gameFrame.add(this.gridPanel, BorderLayout.CENTER);
        this.sidePanel = new SidePanel();
        this.gameFrame.add(this.sidePanel, BorderLayout.EAST);
        this.gameFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        this.gameFrame.setResizable(false);
        this.gameFrame.setVisible(true);

    }

    private JMenuBar createMenuBar() {

        final JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createPreferencesMenu());
        menuBar.add(createHelpMenu());

        return menuBar;

    }

    private JMenu createFileMenu() {

        final JMenu fileMenu = new JMenu("File");

        final JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                confirmAndReset();

            }

        });
        fileMenu.add(newGame);
        fileMenu.addSeparator();

        final JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                confirmAndExit();

            }

        });
        fileMenu.add(exit);

        return fileMenu;

    }

    private JMenu createPreferencesMenu() {

        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenu changeDifficultyMenu = new JMenu("Change Difficulty");

        changeDifficultyMenu.add(createFirstLevel());
        changeDifficultyMenu.add(createSecondLevel());
        changeDifficultyMenu.add(createThirdLevel());
        preferencesMenu.add(changeDifficultyMenu);
        preferencesMenu.addSeparator();
        preferencesMenu.add(createRainbowMode());

        return preferencesMenu;

    }

    private JMenuItem createFirstLevel() {

        final JMenuItem firstLevel = new JMenuItem("Easy");
        firstLevel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                difficultyLevel = LEVEL_ONE;
                confirmAndReset();

            }

        });

        return firstLevel;

    }

    private JMenuItem createSecondLevel() {

        final JMenuItem secondLevel = new JMenuItem("Difficult");
        secondLevel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                difficultyLevel = LEVEL_TWO;
                confirmAndReset();

            }

        });

        return secondLevel;

    }

    private JMenuItem createThirdLevel() {

        final JMenuItem thirdLevel = new JMenuItem("Impossible");
        thirdLevel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                difficultyLevel = LEVEL_THREE;
                confirmAndReset();

            }

        });

        return thirdLevel;

    }

    private JMenuItem createRainbowMode() {

        final JCheckBoxMenuItem rainbowMode = new JCheckBoxMenuItem("Rainbow Mode");
        rainbowMode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isRainbowMode = !isRainbowMode;
                gridPanel.drawGridPanel();
                sidePanel.drawSidePanel();
            }

        });

        return rainbowMode;

    }

    private JMenu createHelpMenu() {

        final JMenu helpMenu = new JMenu("Help");

        final JMenuItem howToPlay = new JMenuItem("How To Play");
        howToPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                new TextBox(gameFrame, TEXT_PATH + "SudokuRules.txt");

            }

        });
        helpMenu.add(howToPlay);

        return helpMenu;

    }

    private void confirmAndReset() {

        final int yesNoOption = JOptionPane.showConfirmDialog(gameFrame,
                "Current game progress will be lost. Are you sure?");
        if (yesNoOption == JOptionPane.YES_OPTION) {

            grid = new Grid(difficultyLevel);
            gridPanel.drawGridPanel();
            gameFrame.remove(timePanel);
            timePanel = new TimePanel();
            gameFrame.add(timePanel, BorderLayout.NORTH);
            isFinalClick = false;

        }

    }

    private void confirmAndExit() {

        final int yesNoOption = JOptionPane.showConfirmDialog(gameFrame,
                "Current game progress will be lost. Are you sure?");
        if (yesNoOption == JOptionPane.YES_OPTION) {

            System.exit(0);

        }

    }

    private class TimePanel extends JPanel {

        private int hours;
        private int minutes;
        private int seconds;
        private JLabel currentTime;
        private JLabel level;
        private Timer timer;

        TimePanel() {

            super(TIME_PANEL_LAYOUT_MGR);
            setBackground(DEFAULT_BACKGROUND_COLOR);
            this.level = new JLabel();
            this.currentTime = new JLabel();
            this.level.setForeground(DEFAULT_FONT_COLOR);
            this.level.setText("    Difficulty Level:" +
                    ((difficultyLevel == LEVEL_ONE) ? " Easy" :
                            (difficultyLevel == LEVEL_TWO) ? " Difficult" :
                                    " Impossible"));
            this.currentTime.setForeground(Color.WHITE);
            add(level, BorderLayout.WEST);
            add(currentTime, BorderLayout.EAST);
            this.timer = new Timer(MS_PER_SECOND, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    seconds++;
                    if (seconds == SECONDS_PER_MINUTE) {
                        minutes++;
                        seconds = 0;
                    }
                    if (minutes == SECONDS_PER_MINUTE) {
                        hours++;
                        minutes = 0;
                    }
                    currentTime.setText(String.format("%02d:%02d:%02d    ", hours, minutes, seconds));

                }

            });
            this.timer.start();

        }

        public String getClockedTime() {

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);

        }

        public void haltTimePanel() {

            timer.stop();

        }

    }

    private class SidePanel extends JPanel {

        List<SelectionPanel> selections;

        SidePanel() {

            super(SIDE_PANEL_LAYOUT_MGR);
            this.selections = new ArrayList<>();
            setPreferredSize(SIDE_PANEL_DIMENSION);
            setBackground(DEFAULT_BACKGROUND_COLOR);
            setBorder(BorderFactory.createLineBorder(DEFAULT_BORDER_COLOR, BORDER_THICKNESS));
            for (int i = 1; i <= PANELS_PER_GRID; i++) {
                final SelectionPanel selection = new SelectionPanel(this, i);
                this.selections.add(selection);
                add(selection);
            }

        }

        public void drawSidePanel() {

            removeAll();
            for (final SelectionPanel selection : selections) {

                selection.drawSelectionPanel();
                add(selection);

            }
            validate();
            repaint();

        }

    }

    private class SelectionPanel extends JPanel {

        private int selectionId;

        SelectionPanel(final SidePanel side, final int selectionId) {

            super(SELECTION_PANEL_LAYOUT_MGR);
            this.selectionId = selectionId;
            assignSelectionColor();
            assignSelectionIcon();
            setSize(SELECTION_PANEL_DIMENSION);
            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isLeftMouseButton(e)) {
                        tryCandidateCoordinate();
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                gridPanel.drawGridPanel();
                                sidePanel.drawSidePanel();

                            }

                        });
                        subGridCandidateCoordinate = -1;
                        tileCandidateCoordinate = -1;
                        isLeftTileClick = false;
                        isRightTileClick = false;
                        congratulateWinner();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }

            });

        }

        private void tryCandidateCoordinate() {

            try {
                if (isLeftTileClick) {
                    if (subGridCandidateCoordinate != -1) {
                        if (grid.isAnswer(subGridCandidateCoordinate, tileCandidateCoordinate, selectionId)) {
                            grid.set(subGridCandidateCoordinate, tileCandidateCoordinate, selectionId);
                        } else {
                            JOptionPane.showMessageDialog(gameFrame,
                                    "Whoops! That doesn't go there!");
                        }
                    }
                } else if (isRightTileClick) {
                    grid.setGuess(subGridCandidateCoordinate, tileCandidateCoordinate, selectionId);
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }

        }

        private void congratulateWinner() {

            if (grid.isComplete() && !isFinalClick) {
                timePanel.haltTimePanel();
                final int yesNoOption = JOptionPane.showConfirmDialog(gameFrame,
                        "CONGRATULATIONS! You solved the puzzle!\n" +
                                "Your time: " + timePanel.getClockedTime() + "\n" +
                                "Play again?");
                if (yesNoOption == JOptionPane.YES_OPTION) {
                    grid = new Grid(difficultyLevel);
                    gridPanel.drawGridPanel();
                    gameFrame.remove(timePanel);
                    timePanel = new TimePanel();
                    gameFrame.add(timePanel, BorderLayout.NORTH);
                } else {
                    isFinalClick = true;
                }
            }

        }

        public void drawSelectionPanel() {

            removeAll();
            assignSelectionColor();
            assignSelectionIcon();
            validate();
            repaint();

        }

        private void assignSelectionIcon() {

            this.removeAll();
            try {
                final BufferedImage image = ImageIO.read(new File(ART_PATH + selectionId + ".png"));
                add(new JLabel(new ImageIcon(image.getScaledInstance(SELECTION_PANEL_ICON_SIZE,
                        SELECTION_PANEL_ICON_SIZE, Image.SCALE_SMOOTH))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void assignSelectionColor() {

            if (isRainbowMode) {
                setBackground(new Color(1 + rgen.nextInt(RGB_UPPER_LIMIT),
                        1 + rgen.nextInt(RGB_UPPER_LIMIT),
                        1 + rgen.nextInt(RGB_UPPER_LIMIT)));
            } else {
                setBackground(DEFAULT_TILE_COLOR);
            }

        }

    }

    private class GridPanel extends JPanel {

        private final List<SubGridPanel> subGrids;

        GridPanel() {

            super(GRID_PANEL_LAYOUT_MGR);
            this.subGrids = new ArrayList<>();
            this.setBorder(BorderFactory.createLineBorder(DEFAULT_BORDER_COLOR, BORDER_THICKNESS));
            setBackground(DEFAULT_BACKGROUND_COLOR);
            for (int i = 0; i < PANELS_PER_GRID; i++) {
                final SubGridPanel subGrid = new SubGridPanel(this, i);
                subGrids.add(subGrid);
                add(subGrid);
            }
            setPreferredSize(GRID_PANEL_DIMENSION);
            validate();

        }

        public void drawGridPanel() {

            removeAll();
            for (final SubGridPanel subGridPanel : subGrids) {
                subGridPanel.drawSubGrid();
                add(subGridPanel);
            }
            validate();
            repaint();

        }

    }

    private class SubGridPanel extends JPanel {

        private final List<TilePanel> tiles;
        private final int subGridId;

        SubGridPanel(final GridPanel gridPanel, final int subGridId) {

            super(SUB_GRID_PANEL_LAYOUT_MGR);
            this.tiles = new ArrayList<>();
            this.subGridId = subGridId;
            this.setBorder(BorderFactory.createLineBorder(DEFAULT_BORDER_COLOR, BORDER_THICKNESS));
            setBackground(DEFAULT_BACKGROUND_COLOR);
            for (int i = 0; i < PANELS_PER_GRID; i++) {
                final TilePanel tile = new TilePanel(gridPanel, this, subGridId, i);
                tiles.add(tile);
                add(tile);
            }
            setPreferredSize(SUB_GRID_PANEL_DIMENSION);
            validate();

        }

        public void drawSubGrid() {

            removeAll();
            for (final TilePanel tilePanel : tiles) {
                tilePanel.drawTile();
                add(tilePanel);
            }
            validate();
            repaint();

        }

    }

    private class TilePanel extends JPanel {

        private final int subGridId;
        private final int tileId;

        TilePanel(final GridPanel gridPanel,
                  final SubGridPanel subGridPanel,
                  final int subGridId,
                  final int tileId) {

            super(TILE_PANEL_LAYOUT_MGR);
            this.subGridId = subGridId;
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTileIcon();
            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    clickTile(e);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            gridPanel.drawGridPanel();
                            sidePanel.drawSidePanel();
                        }

                    });

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }

            });

        }

        private void clickTile(MouseEvent e) {

            if (isRightMouseButton(e)) {
                isLeftTileClick = false;
                if (grid.isEmptyIndex(subGridId, tileId)) {
                    if (isRightTileClick) {
                        if (subGridCandidateCoordinate != subGridId
                                || tileCandidateCoordinate != tileId) {
                            subGridCandidateCoordinate = subGridId;
                            tileCandidateCoordinate = tileId;
                        } else {
                            grid.setGuess(subGridId, tileId, 0);
                            subGridCandidateCoordinate = -1;
                            tileCandidateCoordinate = -1;
                            isRightTileClick = false;
                        }
                    } else {
                        subGridCandidateCoordinate = subGridId;
                        tileCandidateCoordinate = tileId;
                        isRightTileClick = true;
                    }
                }
            } else if (isLeftMouseButton(e)) {
                isRightTileClick = false;
                if (grid.isEmptyIndex(subGridId, tileId)) {
                    subGridCandidateCoordinate = subGridId;
                    tileCandidateCoordinate = tileId;
                    isLeftTileClick = true;
                } else if (isLeftTileClick
                        && (subGridCandidateCoordinate != subGridId
                        || tileCandidateCoordinate != tileId)) {
                    subGridCandidateCoordinate = -1;
                    tileCandidateCoordinate = -1;
                    isLeftTileClick = false;
                }
            }

        }

        public void drawTile() {

            this.removeAll();
            assignTileColor();
            if (isLeftTileClick && subGridCandidateCoordinate == subGridId
                    && tileCandidateCoordinate == tileId) {
                highlightTileIcon();
            } else if (isRightTileClick && subGridCandidateCoordinate == subGridId
                    && tileCandidateCoordinate == tileId) {
                highlightGuessIcon();
            } else {
                assignTileIcon();
            }
            validate();
            repaint();

        }

        private void assignTileIcon() {

            try {
                if (grid.getGuess(subGridId, tileId) != 0 && grid.get(subGridId, tileId) == 0) {
                    final BufferedImage guessImage = ImageIO.read(new File(ART_PATH
                            + grid.getGuess(subGridId, tileId) + "g.png"));
                    add(new JLabel(new ImageIcon(guessImage.getScaledInstance(TILE_PANEL_ICON_SIZE,
                            TILE_PANEL_ICON_SIZE, Image.SCALE_SMOOTH))));
                } else {
                    final BufferedImage image = ImageIO.read(new File(ART_PATH
                            + grid.get(subGridId, tileId) + ".png"));
                    add(new JLabel(new ImageIcon(image.getScaledInstance(TILE_PANEL_ICON_SIZE,
                            TILE_PANEL_ICON_SIZE, Image.SCALE_SMOOTH))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void assignTileColor() {

            if (isRainbowMode) {
                setBackground(new Color(RGB_LOWER_LIMIT + rgen.nextInt(RGB_UPPER_LIMIT),
                        RGB_LOWER_LIMIT + rgen.nextInt(RGB_UPPER_LIMIT),
                        RGB_LOWER_LIMIT + rgen.nextInt(RGB_UPPER_LIMIT)));
            } else {
                setBackground(DEFAULT_TILE_COLOR);
            }

        }

        private void highlightGuessIcon() {

            try {
                final BufferedImage guessImage = ImageIO.read(new File(ART_PATH + "0g_hl.png"));
                add(new JLabel(new ImageIcon(guessImage.getScaledInstance(TILE_PANEL_ICON_SIZE,
                        TILE_PANEL_ICON_SIZE, Image.SCALE_SMOOTH))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void highlightTileIcon() {

            try {
                final BufferedImage image = ImageIO.read(new File(ART_PATH
                        + "0_hl.png"));
                add(new JLabel(new ImageIcon(image.getScaledInstance(TILE_PANEL_ICON_SIZE,
                        TILE_PANEL_ICON_SIZE, Image.SCALE_SMOOTH))));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
