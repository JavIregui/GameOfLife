import java.io.Serializable;
import java.awt.*;

public class GridState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int size;
    public int cellSize;
    public Color[][] grid;

    public GridState(int size, int cellSize, Color[][] grid) {
        this.size = size;
        this.cellSize = cellSize;
        this.grid = grid;
    }
}