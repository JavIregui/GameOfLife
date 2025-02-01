import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameOfLife extends JPanel {

    private static int SIZE = 50;
    private static int CELL_SIZE = 5;
    private Color[][] grid = new Color[500/CELL_SIZE][500/CELL_SIZE];
    
    private Color[] colors = {Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED};

    private double population = 0.3;
    private int timerSpeed = 8;
    private boolean showGrid = true;
    private boolean isAutoMode = false;    
    
    private JButton toggleButton;
    private Timer autoTimer;
    private boolean loading = false;
    
    public GameOfLife() {

        resetGrid();
        
        setPreferredSize(new Dimension(500, 500 + 50));
        setLayout(null);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !isAutoMode) {
                    nextGeneration();
                    repaint();
                }
            }
        });
        
        setFocusable(true);
        
        toggleButton = new JButton("Auto Mode");
        toggleButton.setBounds(0, 0, 240, 30);
        toggleButton.addActionListener(e -> toggleMode());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onWindowResized();
            }
        });
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        add(toggleButton);
        updateButtonColor();
        
        autoTimer = new Timer(555-55*timerSpeed, e -> {
            if (isAutoMode) {
                nextGeneration();
                repaint();
            }
        });
    }

    private void onWindowResized() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        toggleButton.setBounds(topFrame.getWidth() / 2 - 120, topFrame.getHeight()-60, 240, 30);
    
        if (!loading) {        
            int side = Math.min(topFrame.getWidth(), topFrame.getHeight() - 75);
            SIZE = side / CELL_SIZE;
            grid = new Color[SIZE][SIZE];
            setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE + 50));
            resetGrid();
        }
    }

    private void resetGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(Math.random() > 1-population){
                    int color = (int) (Math.random() * colors.length);
                    grid[i][j] = colors[color];
                }
                else {
                    grid[i][j] = Color.WHITE;
                }
            }
        }
        repaint();
    }
 
    private void nextGeneration() {
        Color[][] newGrid = new Color[SIZE][SIZE];
        
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int livingNeighbors = 0;
                Map<Color, Integer> colorCounts = new HashMap<>();
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE) {
                            Color neighbor = grid[nx][ny];
                            if (!neighbor.equals(Color.WHITE)) {
                                livingNeighbors++;
                                colorCounts.put(neighbor, colorCounts.getOrDefault(neighbor, 0) + 1);
                            }
                        }
                    }
                }
                
                Color current = grid[x][y];

                if (!current.equals(Color.WHITE)) {
                    if (livingNeighbors == 2 || livingNeighbors == 3) {
                        newGrid[x][y] = current;
                    } else {
                        newGrid[x][y] = Color.WHITE;
                    }
                } else {
                    if (livingNeighbors == 3) {
                        newGrid[x][y] = determineNewCellColor(colorCounts);
                    } else {
                        newGrid[x][y] = Color.WHITE;
                    }
                }
            }
        }
        grid = newGrid;
    }
    
    private Color determineNewCellColor(Map<Color, Integer> colorCounts) {
        int maxCount = 0;
        ArrayList<Color> candidateColors = new ArrayList<>();
        for (Map.Entry<Color, Integer> entry : colorCounts.entrySet()) {
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
                candidateColors.clear();
                candidateColors.add(entry.getKey());
            } else if (count == maxCount) {
                candidateColors.add(entry.getKey());
            }
        }

        if (candidateColors.isEmpty()) {
            int index = (int) (Math.random() * colors.length);
            return colors[index];
        }

        int index = (int) (Math.random() * candidateColors.size());
        return candidateColors.get(index);
    }

    private void toggleMode() {
        isAutoMode = !isAutoMode;

        if (isAutoMode) {
            autoTimer.start();
        } else {
            autoTimer.stop();
        }
        updateButtonColor();

        toggleButton.setFocusable(false);
        requestFocusInWindow();

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setResizable(!isAutoMode);
    }
    
    private void updateButtonColor() {
        if (isAutoMode) {
            toggleButton.setBackground(Color.LIGHT_GRAY);
            SwingUtilities.updateComponentTreeUI(this);
        } else {
            toggleButton.setBackground(Color.WHITE);
            SwingUtilities.updateComponentTreeUI(this);
        }
    }
    
    private void saveGridToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save simulation");

        fileChooser.setSelectedFile(new File("gameoflife_state.save"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            if (!fileToSave.getName().toLowerCase().endsWith(".save")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".save");
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                GridState state = new GridState(SIZE, CELL_SIZE, grid);
                oos.writeObject(state);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadGridFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open simulation");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Save Files (*.save)", "save"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                GridState state = (GridState) ois.readObject();

                loading = true;

                SIZE = state.size;
                CELL_SIZE = state.cellSize;
                grid = state.grid;

                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE + 50));
                topFrame.setSize(getPreferredSize());
                topFrame.validate();

                repaint();

                SwingUtilities.invokeLater(() -> {
                    loading = false;
                });

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveScreenshot() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save screenshot");
        fileChooser.setSelectedFile(new File("gameoflife_screenshot.png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String path = fileToSave.getAbsolutePath();

            if (!path.toLowerCase().endsWith(".png")) {
                path += ".png";
            }

            BufferedImage image = new BufferedImage(
                SIZE * CELL_SIZE,
                SIZE * CELL_SIZE,
                BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = image.createGraphics();

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    g2d.setColor(grid[i][j]);
                    g2d.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    if (CELL_SIZE > 2 && showGrid) {
                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            g2d.dispose();

            try {
                ImageIO.write(image, "PNG", new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);


        int offsetX = (topFrame.getWidth() / 2) - (SIZE * CELL_SIZE / 2);
        int offsetY = (topFrame.getHeight() / 2) - ((SIZE * CELL_SIZE + 50) / 2);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.setColor(grid[i][j]);
                g.fillRect(j * CELL_SIZE + offsetX, i * CELL_SIZE + offsetY, CELL_SIZE, CELL_SIZE);

                if(CELL_SIZE > 2 && showGrid){
                    g.setColor(Color.GRAY);
                    g.drawRect(j * CELL_SIZE + offsetX, i * CELL_SIZE + offsetY, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String os = System.getProperty("os.name").toLowerCase();
            boolean isMac = os.contains("mac");

            if (isMac) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Game of Life");
            }

            JFrame frame = new JFrame("Game of Life");
            GameOfLife game = new GameOfLife();
            frame.add(game);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = screenSize.width;
            int maxHeight = screenSize.height;

            int minWidth = 500;
            int minHeight = 500 + 50;

            frame.setMinimumSize(new Dimension(minWidth, minHeight));
            frame.setMaximumSize(new Dimension(maxWidth, maxHeight));

            JMenuBar menuBar = createMenuBar(game);
            frame.setJMenuBar(menuBar);

            ImageIcon icon = new ImageIcon("../media/icon.png");
            frame.setIconImage(icon.getImage());

            frame.setResizable(true);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private static JMenuBar createMenuBar(GameOfLife game) {

        JMenuBar menuBar = new JMenuBar();

        // FILE MENU
        JMenu fileMenu = new JMenu("File");

        // Restart
        JMenuItem restart = new JMenuItem("New simulation");
        restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        restart.addActionListener(e -> game.resetGrid());

        fileMenu.add(restart);
        fileMenu.addSeparator();

        // Open
        JMenuItem open = new JMenuItem("Open simulation");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        open.addActionListener(e -> game.loadGridFromFile());

        fileMenu.add(open);

        // Save Submenu
        JMenu saveSubMenu = new JMenu("Save");

        JMenuItem saveScreenShot = new JMenuItem("Save screenshot");
        saveScreenShot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveScreenShot.addActionListener(e -> game.saveScreenshot());

        JMenuItem saveSim = new JMenuItem("Save simulation");
        saveSim.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveSim.addActionListener(e -> game.saveGridToFile());

        saveSubMenu.add(saveSim);
        saveSubMenu.add(saveScreenShot);
        fileMenu.add(saveSubMenu);
        
        // EDIT MENU
        JMenu editMenu = new JMenu("Edit");

        // Auto Mode
        JMenuItem toggleAuto = new JMenuItem("Toggle Auto Mode");
        toggleAuto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        toggleAuto.addActionListener(e -> game.toggleMode());

        editMenu.add(toggleAuto);

        // Settings TIENE QUE ABRIR UNA VENTANA PARA %, TAMAÑO Y VELOCIDAD
        JMenuItem settings = new JMenuItem("Settings");
        settings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        editMenu.add(settings);

        // HELP MENU
        JMenu helpMenu = new JMenu("Help");

        // Game rules TIENE QUE ABRIR UNA VENTANA CON LAS NORMAS
        JMenuItem rules = new JMenuItem("Game rules");

        helpMenu.add(rules);

        // Github
        JMenuItem github = new JMenuItem("Open on GitHub");
        github.addActionListener(e -> {
            try {
                URI url = new URI("https://github.com/JavIregui/GameOfLife");

                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(url);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        helpMenu.add(github);

        // Credits
        JMenu credits = new JMenu("Credits");

        JMenuItem dev = new JMenuItem("Developed by Javier Iregui");
        dev.setEnabled(false);

        credits.add(dev);
        helpMenu.add(credits);

        // Adding menus to the bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }
}