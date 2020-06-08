/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.SudokuGrid;
import util.GridIndex;

import java.util.ArrayList;
import java.util.List;


/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver
{
    public BackTrackingSolver() {

    } // end of BackTrackingSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        List<GridIndex> posToModify = new ArrayList<GridIndex>();

        for (int y = 0; y < grid.getGrid().length; y++) {
            for (int x = 0; x < grid.getGrid()[y].length; x++) {
                if (grid.getGrid()[y][x] == 0) {
                    posToModify.add(new GridIndex(x, y, grid.getGrid().length));
                }
            }
        }

        int[] values = grid.getAllowedValues();

        return modify(grid, posToModify, 0, values);
    } // end of solve()

    private boolean modify(SudokuGrid grid, List<GridIndex> positionsToModify, int indexToModify, int[] allowedValues) {
        GridIndex posToModify = positionsToModify.get(indexToModify);

        for (int i = 0; i < allowedValues.length; i++) {
            grid.getGrid()[posToModify.getY()][posToModify.getX()] = allowedValues[i];
            //System.out.println(grid.toString());

            if (grid.validate()) {
                if (indexToModify == (positionsToModify.size() - 1)) {
                    return true;
                }

                boolean foundSolution = modify(grid, positionsToModify, indexToModify + 1, allowedValues);

                if (foundSolution) {
                    return true;
                }
            }
        }

        grid.getGrid()[posToModify.getY()][posToModify.getX()] = 0;

        return false;
    }

} // end of class BackTrackingSolver()
