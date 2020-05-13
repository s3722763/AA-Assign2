/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;
import java.util.Scanner;


/**
 * Class implementing the grid for standard Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task A and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class StdSudokuGrid extends SudokuGrid
{
    // TODO: Add your own attributes
    private int[][] grid;
    private int[] allowedValues;

    public StdSudokuGrid() {
        super();

        // TODO: any necessary initialisation at the constructor
    } // end of StdSudokuGrid()


    /* ********************************************************* */


    @Override
    public void initGrid(String filename)
        throws FileNotFoundException, IOException
    {
        File file = new File(filename);
        Scanner reader = new Scanner(file);

        //Create new grid
        int size = reader.nextInt();
        grid = new int[size][size];

        reader.nextLine();
        String allowedLine = reader.nextLine();
        String[] allowed = allowedLine.split(" ");
        this.allowedValues= new int[allowed.length];

        for (int i = 0; i < allowed.length; i++){
            this.allowedValues[i] = Integer.parseInt(allowed[i]);
        }

        while (reader.hasNextLine()) {
            //Value input format below
            //Column,Row Value

            String[] lineParts = reader.nextLine().split(" ");
            String[] coords = lineParts[0].split(",");
            String value = lineParts[1];

            //Column
            int x = Integer.parseInt(coords[1]);
            //Row
            int y = Integer.parseInt(coords[0]);

            this.grid[y][x] = Integer.parseInt(value);
        }

        reader.close();
    } // end of initBoard()


    @Override
    public void outputGrid(String filename)
        throws FileNotFoundException, IOException
    {
        File file = new File(filename);
        PrintWriter writer = new PrintWriter(file);
        String toWrite = this.toString();
        writer.write(toWrite);
        writer.close();
    } // end of outputBoard()

    @Override
    public String toString() {
        String s = new String();

        for (int y = 0; y < this.grid.length; y++) {
            for (int x = 0; x < this.grid[y].length; x++) {
                if (x != 0) {
                    s += (',');
                }

               s += (this.grid[y][x]);
            }

            s += '\n';
        }
        return s;
    } // end of toString()

    private boolean checkRows() {
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

    private boolean checkColumns() {
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

    private boolean checkSquares() {
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

    @Override
    public boolean validate() {
        //TODO: Combine check rows and check columns if have time
        //TODO: if vlaue is zero then does woory about counting
        boolean columnsValid = checkColumns();
        boolean rowsValid = checkRows();
        boolean squaresValid = checkSquares();

        return columnsValid && rowsValid && squaresValid;
    } // end of validate()

    @Override
    public void addNumber(int x, int y, int value) {
        this.grid[y][x] = value;
    }

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public int[] getAllowedValues() {
        return this.allowedValues;
    }
} // end of class StdSudokuGrid
