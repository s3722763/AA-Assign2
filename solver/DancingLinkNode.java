package solver;

public class DancingLinkNode {
    private DancingLinkNode up;
    private DancingLinkNode down;
    private DancingLinkNode left;
    private DancingLinkNode right;
    private DancingLinkNode column;
    private int value;

    public DancingLinkNode(int value) {
        this.up = null;
        this.down = null;
        this.left = null;
        this.right = null;
        this.column = null;
        this.value = value;
    }

    public DancingLinkNode() {
        this(-1);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public DancingLinkNode getUp() {
        return up;
    }

    public void setUp(DancingLinkNode up) {
        this.up = up;
    }

    public DancingLinkNode getDown() {
        return down;
    }

    public void setDown(DancingLinkNode down) {
        this.down = down;
    }

    public DancingLinkNode getLeft() {
        return left;
    }

    public void setLeft(DancingLinkNode left) {
        this.left = left;
    }

    public DancingLinkNode getRight() {
        return right;
    }

    public void setRight(DancingLinkNode right) {
        this.right = right;
    }

    public DancingLinkNode getColumn() {
        return column;
    }

    public void setColumn(DancingLinkNode columnNode) {
        //System.out.print(columnNode);

        System.out.println("Changing column");
        this.column = columnNode;
    }

    public boolean isColumnNode() {
        return value >= 0;
    }

    public int getDistanceFromRoot() {
        DancingLinkNode col = this.column;
        int hops = 0;

        while (col.down != null) {
            col = col.left;
            hops += 1;
        }

        return hops;
    }
}