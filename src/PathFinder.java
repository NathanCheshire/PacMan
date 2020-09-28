public abstract class PathFinder {
    private Node start;
    private Node goal;

    //abstract to allow for future algorithms
    public void refreshPath(Node[][] graph, boolean move) {}
}