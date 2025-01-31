import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class GameOfLife extends JPanel {
    private static int SIZE = 50; // Grid size
    private static int CELL_SIZE = 5;
    private double population = 0.3;
    private boolean[][] grid = new boolean[500/CELL_SIZE][500/CELL_SIZE];
    private boolean showGrid = true;
    
    private boolean isAutoMode = false;
    private Timer autoTimer;
    private int timerSpeed = 8;
    private JButton toggleButton;
    
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
        int side = Math.min(topFrame.getWidth(), topFrame.getHeight() - 75);
    
        SIZE = side / CELL_SIZE;
        grid = new boolean[SIZE][SIZE];
    
        setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE + 50));

        toggleButton.setBounds(topFrame.getWidth() / 2 - 120, topFrame.getHeight()-60, 240, 30);

        resetGrid();
    }

    private void resetGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Math.random() > 1-population;
            }
        }
        repaint();
    }
 
    private void nextGeneration() {
        boolean[][] newGrid = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int neighbors = countNeighbors(i, j);
                if (grid[i][j]) {
                    newGrid[i][j] = neighbors == 2 || neighbors == 3;
                } else {
                    newGrid[i][j] = neighbors == 3;
                }
            }
        }
        grid = newGrid;
    }
    
    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && grid[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
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
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);


        int offsetX = (topFrame.getWidth() / 2) - (SIZE * CELL_SIZE / 2);
        int offsetY = (topFrame.getHeight() / 2) - ((SIZE * CELL_SIZE + 50) / 2);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.setColor(grid[i][j] ? Color.BLACK : Color.WHITE);
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

        // Open TIENE QUE IMPORTAR
        JMenuItem open = new JMenuItem("Open simulation");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        fileMenu.add(open);

        // Save Submenu TIENE QUE GUARDAR Y HACER CAPTURAS
        JMenu saveSubMenu = new JMenu("Save");

        JMenuItem saveScreenShot = new JMenuItem("Save screenshot");
        saveScreenShot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK | Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        JMenuItem saveSim = new JMenuItem("Save simulation");
        saveSim.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

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
