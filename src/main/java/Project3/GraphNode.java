package Project3;

public class GraphNode {
    public static final int NORTH   = 1;
    public static final int EAST    = 2;
    public static final int SOUTH   = 4;
    public static final int WEST    = 8;

    private int direction = 0;
    public static final int [] DIRECTIONS = {1, 2, 4, 8, 3, 6, 12, 9};
    private final GraphEdge[] neighbours;


    public GraphNode(int direction) {
        this.direction = direction;
        this.neighbours = new GraphEdge[] {
                null, null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null, null
        };
    }

    private boolean isDirectionAvailable(int direction) {
        return (direction & this.direction) != 0;
    }

    public GraphEdge getNeighbour(int direction) {
        return this.neighbours[direction];
    }

    public void setNeighbour(int direction, GraphNode neighbour, int weight) {
        this.neighbours[direction] = new GraphEdge(this, neighbour, weight);
    }
}