import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOfLife extends JPanel {
    private static final int SIZE = 50; // Tamaño de la cuadrícula
    private static final int CELL_SIZE = 10; // Tamaño de cada celda
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
        toggleButton = new JButton("Modo Automático");
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
            toggleButton.setText("Modo Manual");
            SwingUtilities.updateComponentTreeUI(this);
        } else {
            toggleButton.setBackground(Color.WHITE); // Color normal en modo manual
            toggleButton.setText("Modo Automático");
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
        JFrame frame = new JFrame("Game of Life");
        GameOfLife game = new GameOfLife();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
