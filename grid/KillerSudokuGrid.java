/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import util.GridIndex;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Class implementing the grid for Killer Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task E and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class KillerSudokuGrid extends SudokuGrid
{
    private List<Cage> listOfCages;

    public KillerSudokuGrid() {
        super();
        listOfCages = new ArrayList<Cage>();
    } // end of KillerSudokuGrid()


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
            int target = reader.nextInt();
            //Value input format below
            //Column,Row Value

            String[] lineParts = reader.nextLine().split(" ");
            Cage cage = new Cage(target);

            for (String linePart : lineParts) {
                //Column
                String[] coords = linePart.split(",");

                //Column
                int x = Integer.parseInt(coords[1]);
                //Row
                int y = Integer.parseInt(coords[0]);

                cage.addPosition(x, y);
            }

            listOfCages.add(cage);
        }

        reader.close();
    } // end of initBoard()


    @Override
    public void outputGrid(String filename)
        throws FileNotFoundException, IOException
    {
        this.writeFile(filename);
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


    @Override
    public boolean validate() {
        boolean rowsValid = checkRows();
        boolean columnsValid = checkColumns();
        boolean squaresValid = checkSquares();

        if (rowsValid && columnsValid && squaresValid) {
            for (Cage cage : this.listOfCages) {
                if (!cage.checkCage(this.grid)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    } // end of validate()

    private class Cage {
        private List<GridIndex> indexes;
        private int target;

        public Cage(int target) {
            indexes = new ArrayList<GridIndex>();
            this.target = target;
        }

        public void addPosition(int x, int y) {
            GridIndex index = new GridIndex(x, y);
            indexes.add(index);
        }

        public boolean checkCage(int[][] grid) {
            int sum = 0;

            for (GridIndex index : indexes) {
                int value = grid[index.getY()][index.getX()];

                //Need to have the grid full before can test the cages
                if (value == 0) {
                    return true;
                }

                sum += grid[index.getY()][index.getX()];
            }

            return sum == this.target;
        }
    }
} // end of class KillerSudokuGrid
