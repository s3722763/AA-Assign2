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

        int numberCages = reader.nextInt();

        while (reader.hasNextLine()) {
            int target = reader.nextInt();
            //Value input format below
            //Column,Row Value

            String[] lineParts = reader.nextLine().split(" ");
            Cage cage = new Cage(target, size);

            //Start from 1 as 0 is the target for this cage
            for (int i = 1; i < lineParts.length; i++) {
                String linePart = lineParts[i];
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

    boolean checkCages() {
        for (Cage cage : this.listOfCages) {
            if (!cage.checkCage(this.grid)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public boolean validate() {
        boolean rowsValid = checkRows();
        boolean columnsValid = checkColumns();
        boolean squaresValid = checkSquares();

        if (rowsValid && columnsValid && squaresValid) {
            return checkCages();
        } else {
            return false;
        }
    } // end of validate()

    public boolean killerConstraintsValid() {
        return checkCages();
    }

    public void reset() {
        for (int y = 0; y < this.grid.length; y++) {
            for (int x = 0; x < this.grid[y].length; x++) {
                this.grid[y][x] = 0;
            }
        }
    }

    public List<Cage> getCages() {
        return listOfCages;
    }

    public class Cage {
        private List<GridIndex> indexes;
        private int target;
        private int size;

        public Cage(int target, int size) {
            indexes = new ArrayList<GridIndex>();
            this.target = target;
            this.size = size;
        }

        public void addPosition(int x, int y) {
            GridIndex index = new GridIndex(x, y, size);
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

        public int getSize() {
            return this.indexes.size();
        }

        public int getTarget() {
            return this.target;
        }

        public List<GridIndex> getPositions() {
            return this.indexes;
        }
    }
} // end of class KillerSudokuGrid
