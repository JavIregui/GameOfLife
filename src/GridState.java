import java.io.Serializable;

public class GridState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int size;
    public int cellSize;
    public boolean[][] grid;

    public GridState(int size, int cellSize, boolean[][] grid) {
        this.size = size;
        this.cellSize = cellSize;
        this.grid = grid;
    }
}