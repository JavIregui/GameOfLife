import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOfLife extends JPanel {
    private static int SIZE = 50; // Tamaño de la cuadrícula
    private static int CELL_SIZE = 10; // Tamaño de cada celda
    private boolean[][] grid = new boolean[SIZE][SIZE];
    
    private boolean isAutoMode = false; // Indica si estamos en modo automático
    private Timer autoTimer; // Timer para el modo automático
    private JButton toggleButton; // Botón para cambiar de modo
    
    public GameOfLife() {
        // Inicializa con un patrón aleatorio
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Math.random() > 0.7;
            }
        }
        
        setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE + 50)); // Añadir espacio para el botón
        setLayout(null); // Establecer el layout a null para poder posicionar el botón manualmente
        
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
        
        // Crear el botón de toggle
        toggleButton = new JButton("Auto Mode");
        toggleButton.setBounds(SIZE * CELL_SIZE - 250, SIZE * CELL_SIZE + 10, 240, 30); // Posición y tamaño
        toggleButton.addActionListener(e -> toggleMode());
        
        // Establecer un Look and Feel personalizado
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Look and Feel consistente
            SwingUtilities.updateComponentTreeUI(this); // Actualiza la interfaz con el nuevo Look and Feel
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        
        add(toggleButton); // Añadir el botón al panel

        // Cambiar el color inicial del botón
        updateButtonColor();
        
        // Crear el Timer para el modo automático
        autoTimer = new Timer(100, e -> {
            if (isAutoMode) {
                nextGeneration();
                repaint();
            }
        });
    }

    private void resetGrid() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = Math.random() > 0.7;
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
            autoTimer.start(); // Iniciar el temporizador en modo automático
        } else {
            autoTimer.stop(); // Detener el temporizador en modo manual
        }
        updateButtonColor(); // Actualizar el color del botón

        // Quitar el enfoque del botón y ponerlo en el JPanel
        toggleButton.setFocusable(false); // Deshabilitar el enfoque en el botón
        requestFocusInWindow(); // Darle enfoque al JPanel
    }
    
    private void updateButtonColor() {
        if (isAutoMode) {
            toggleButton.setBackground(Color.GREEN); // Botón verde en modo automático
            toggleButton.setText("Manual Mode");
            SwingUtilities.updateComponentTreeUI(this);
        } else {
            toggleButton.setBackground(Color.WHITE); // Color normal en modo manual
            toggleButton.setText("Auto Mode");
            SwingUtilities.updateComponentTreeUI(this);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g.setColor(grid[i][j] ? Color.BLACK : Color.WHITE);
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
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

            int minWidth = SIZE * CELL_SIZE;
            int minHeight = SIZE * CELL_SIZE + 50;

            frame.setMinimumSize(new Dimension(minWidth, minHeight));
            frame.setMaximumSize(new Dimension(maxWidth, maxHeight));

            // Crear la barra de menú
            JMenuBar menuBar = createMenuBar(game);
            frame.setJMenuBar(menuBar); // Siempre se asigna, pero en macOS se muestra en la barra del sistema

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

        // Github TIENE QUE LLEVAR A GITHUB
        JMenuItem github = new JMenuItem("Open on GitHub");

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
