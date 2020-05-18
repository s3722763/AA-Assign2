package util;

public class GridIndex {
    private final int x;
    private final int y;

    public GridIndex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GridIndex) {
            GridIndex cmp = (GridIndex)obj;
            return this.equals(cmp);
        }

        return false;
    }

    public boolean equals(GridIndex cmp) {
        return cmp.getX() == this.getX() && cmp.getY() == this.getY();
    }

    @Override
    public int hashCode() {
        return this.x + this.y;
    }
}
