package solver;

public class DancingLinkNode {
    private DancingLinkNode up;
    private DancingLinkNode down;
    private DancingLinkNode left;
    private DancingLinkNode right;
    private DancingLinkNode column;
    private int value;

    public DancingLinkNode(/*int value*/) {
        this.up = null;
        this.down = null;
        this.left = null;
        this.right = null;
        this.column = null;
        //this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        //System.out.println(value);
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
        this.column = columnNode;
    }

  /*  public boolean isColumnNode() {
        return value >= 0;
    }*/
}