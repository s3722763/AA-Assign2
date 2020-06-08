/*
package util;

public class GridIndexValue extends GridIndex {
    private int value;

    public GridIndexValue(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }

    public GridIndexValue(GridIndex gridIndex, Integer value) {
        super(gridIndex.getX(), gridIndex.getY());
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GridIndexValue) {
            GridIndexValue cmp = (GridIndexValue)obj;
            return this.equals(cmp);
        }

        return false;
    }

    public boolean equals(GridIndexValue cmp) {
        return cmp.getX() == this.getX() && cmp.getY() == this.getY() && cmp.getValue() == this.getValue();
    }

    @Override
    public int hashCode() {
        return this.getX() + this.getY() + this.getValue();
    }
}
*/
