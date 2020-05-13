/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

 package grid;

 import java.io.*;


/**
 * Abstract class representing the general interface for a Sudoku grid.
 * Both standard and Killer Sudoku extend from this abstract class.
 */
public abstract class SudokuGrid
{
    protected int[][] grid;
    protected int[] allowedValues;

    /**
     * Load the specified file and construct an initial grid from the contents
     * of the file.  See assignment specifications and sampleGames to see
     * more details about the format of the input files.
     *
     * @param filename Filename of the file containing the intial configuration
     *                  of the grid we will solve.
     *
     * @throws FileNotFoundException If filename is not found.
     * @throws IOException If there are some IO exceptions when openning or closing
     *                  the files.
     */
    public abstract void initGrid(String filename)
        throws FileNotFoundException, IOException;


    /**
     * Write out the current values in the grid to file.  This must be implemented
     * in order for your assignment to be evaluated by our testing.
     *
     * @param filename Name of file to write output to.
     *
     * @throws FileNotFoundException If filename is not found.
     * @throws IOException If there are some IO exceptions when openning or closing
     *                  the files.
     */
    public abstract void outputGrid(String filename)
        throws FileNotFoundException, IOException;


    /**
     * Converts grid to a String representation.  Useful for displaying to
     * output streams.
     *
     * @return String representation of the grid.
     */
    public abstract String toString();


    /**
     * Checks and validates whether the current grid satisfies the constraints
     * of the game in question (either standard or Killer Sudoku).  Override to
     * implement game specific checking.
     *
     * @return True if grid satisfies all constraints of the game in question.
     */
    public abstract boolean validate();

    protected boolean checkRows() {
        for (int y = 0; y < this.grid.length; y++) {
            int[] values = new int[this.grid.length + 1];

            for (int x = 0; x < this.grid[y].length; x++) {
                //TODO: Check if right to not include this
                /*if (this.grid[y][x] == 0) {
                    return false;
                } else */
                if (this.grid[y][x] != 0) {
                    if (values[this.grid[y][x]] != 0) {
                        return false;
                    } else {
                        values[this.grid[y][x]] = 1;
                    }
                }
            }
        }

        return true;
    }

    protected boolean checkColumns() {
        for (int x = 0; x < this.grid.length; x++) {
            int[] values = new int[this.grid.length + 1];

            for (int y = 0; y < this.grid.length; y++) {
                if (this.grid[y][x] != 0) {
                    if (values[this.grid[y][x]] != 0) {
                        return false;
                    } else {
                        values[this.grid[y][x]] = 1;
                    }
                }
            }
        }
        return true;
    }

    private int[] generateStartingIndexes(int size) {
        int[] startIndexes = new int[size];

        for (int i = 0; i < size; i++) {
            startIndexes[i] = size * i;
        }

        return startIndexes;
    }

    protected boolean checkSquares() {
        int size = (int)Math.sqrt(this.grid.length);
        int[] startingIndex = generateStartingIndexes(size);
        //int[] startingIndex = { 0, 3, 6 };

        for (int yStart : startingIndex) {
            for (int xStart : startingIndex) {
                int[] values = new int[this.grid.length + 1];

                for (int y = yStart; y < (yStart + size); y++) {
                    for (int x = xStart; x < (xStart + size); x++) {
                        if (this.grid[y][x] != 0) {
                            if(values[this.grid[y][x]] != 0) {
                                return false;
                            } else {
                                values[this.grid[y][x]] = 1;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void addNumber(int x, int y, int value) {
        this.grid[y][x] = value;
    }

    public int[][] getGrid() {
        return this.grid;
    }


    public int[] getAllowedValues() {
        return this.allowedValues;
    }

    protected void writeFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        PrintWriter writer = new PrintWriter(file);
        String toWrite = this.toString();
        writer.write(toWrite);
        writer.close();
    }
} // end of abstract class SudokuGrid
