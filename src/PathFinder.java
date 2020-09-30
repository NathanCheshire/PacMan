public abstract class PathFinder {
    //abstract class means each path finding algorithm that can be used in this program must have a start and goal node
    //must also implement a refreshPath algorithm

    private Node start;
    private Node goal;

    //abstract to allow for future algorithms
    public void refreshPath(Node[][] graph, boolean move) {}
}