/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import grid.SudokuGrid;
import util.GridIndexValue;
import util.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver
{
    final int CELL_MATRIX_INDEX = 0;
    final int ROWS_MATRIX_INDEX = 1;
    final int COLUMNS_MATRIX_INDEX = 2;
    final int BOXES_MATRIX_INDEX = 3;

    public AlgorXSolver() {
        // TODO: any initialisation you want to implement.
    } // end of AlgorXSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        //Generate tables for the size of the grid with options for each
        BinaryMatrix[] coverConstraints = generateMatrixConstraints(grid.getGrid().length);

        //TODO: Make recursive loop. Each iteration of the method has a different constraints matrix
        // The index of the array is stored in a list. The value at the index the same as the current recurion loop (loop 0 = index 0)
        // is the current line which is being checked. The loop calling the method is responsible for creating a new constraints array.
        // The new constraints matrix must be created not an old one. This is because if the old one is used past values will be lost.
        // A successful find is when all 4 of the lists produced by the matrixes are all 1

        //Algo: Chose first line. In this implementation chose first index (0) therefore R1C1#1. Remove this line
        // For the row also remove any which have a one in the same position. The remove the column after that
        return false;
    } // end of solve()

    private BinaryMatrix[] generateMatrixConstraints(int size) {
        //Cells, Rows, Columns, Boxes
        BinaryMatrix[] arrays = {
                //Row column
                new BinaryMatrix(size),
                //Row value
                new BinaryMatrix(size),
                //Column value
                new BinaryMatrix(size),
                //Box Value
                new BinaryMatrix(size)
        };

        int value = 0;
        for (int y = 0; y < size; y++) {
            int offset = 0;

            for (int x = 0; x < size; x++) {
                //Row Column
                arrays[CELL_MATRIX_INDEX].add(value, y, offset, true);
                //Row Value
                arrays[ROWS_MATRIX_INDEX].add(x, y, offset, true);
                //Column Value
                arrays[COLUMNS_MATRIX_INDEX].add(x, y, offset, true);
                //Box Value
                arrays[BOXES_MATRIX_INDEX].add(x, y, offset, true);
                offset += 1;
            }

            value += 1;
        }

        for (BinaryMatrix bm : arrays) {
            System.out.println(bm.toString());
            System.out.println("");
        }

        return arrays;
    }

    private class BinaryMatrix {
        private List<ArrayList<Boolean>> rowColumn;
        private int size;

        public BinaryMatrix(int size) {
            this.size = size;
            rowColumn = new ArrayList<ArrayList<Boolean>>(size * size);

            for (int i = 0; i < size * size; i++) {
                ArrayList rowList = new ArrayList<Boolean>(size);

                for (int j = 0; j < size; j++) {
                    rowList.add(Boolean.FALSE);
                }

                rowColumn.add(rowList);
            }
        }

        public void add(int x, int y, int offset, boolean value) {
            List row = rowColumn.get((y * size) + offset);
            row.set(x, value ? Boolean.TRUE : Boolean.FALSE);
        }

        @Override
        public String toString() {
            String temp = "";

            for (int y = 0; y < rowColumn.size(); y++) {
                for (int x = 0; x < rowColumn.get(y).size(); x++) {
                    if (x != 0) {
                        temp += ",";
                    }

                    temp += (rowColumn.get(y).get(x) ? "1" : "0");
                }

                temp += '\n';
            }

            return temp;
        }
    }

} // end of class AlgorXSolver
