/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;

import grid.StdSudokuGrid;
import grid.SudokuGrid;
import util.GridIndexValue;
import util.Tuple2;

import javax.naming.BinaryRefAddr;
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
        BinaryMatrix binaryMatrix = new BinaryMatrix(grid.getGrid().length);
        binaryMatrix.generateMatrixConstraints(grid.getGrid().length);
        //Using cutback breaks it will find out problerm later
        cutbackMatrixConstraints(binaryMatrix, grid.getGrid().length, grid);
        //System.out.println();
        //TODO: Make recursive loop. Each iteration of the method has a different constraints matrix
        // The index of the array is stored in a list. The value at the index the same as the current recurion loop (loop 0 = index 0)
        // is the current line which is being checked. The loop calling the method is responsible for creating a new constraints array.
        // The new constraints matrix must be created not an old one. This is because if the old one is used past values will be lost.
        // A successful find is when all 4 of the lists produced by the matrixes are all 1

        //Algo: Chose first line. In this implementation chose first index (0) therefore R1C1#1. Remove this line
        // For the row also remove any which have a one in the same position. The remove the column after that
        //binaryMatrix.printMatrix();
        //System.out.println("Starting algo");
        BinaryMatrix foundSolution = algorithmx(binaryMatrix, 0);

        if (foundSolution != null) {
            matrixToGrid(foundSolution, grid);
            return true;
        }

        //binaryMatrix.printMatrix();
        //return false;
        return false;
    } // end of solve()

    private void matrixToGrid(BinaryMatrix binaryMatrix, SudokuGrid grid) {
        //binaryMatrix.printMatrix();

        int size  = binaryMatrix.size;
        int column = 0;

        for (int i = 0; i < binaryMatrix.binaryMatrix.size(); i++) {
            for (int j = (size * size); j < (2 * size * size); j++) {
               if (binaryMatrix.binaryMatrix.get(i).get(j) == Boolean.TRUE) {
                    int number = grid.getAllowedValues()[j % size];
                    int row = i % size;

                    if ((i % size == 0) && i != 0) {
                        column += 1;
                    }

                    //System.out.println(String.format("Column: %d% Row: d Value: %d", column, row, number));
                    grid.addNumber(column, row, number);
               }
            }
        }
    }

    private void cutbackMatrixConstraints(BinaryMatrix constraints, int size, SudokuGrid grid) {
        int box_width = (int)Math.sqrt(size);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                 if (grid.getGrid()[y][x] != 0) {
                     ArrayList<Boolean> toKeep = new ArrayList<>(size * size * 4);
                     int value = grid.getGrid()[y][x];

                     for (int i = 0; i < size * size * 4; i++) {
                         toKeep.add(Boolean.FALSE);
                     }

                     //Row Column
                     int temp = (x % size) + (y * size);
                     toKeep.set(temp, Boolean.TRUE);
                     //Row Val
                     temp = ((value - 1) % size) + (y * size);
                     temp += (size * size);
                     toKeep.set(temp, Boolean.TRUE);
                     //Col Val

                     temp = (value - 1) + (x * size);
                     temp += (2 * size * size);
                     toKeep.set(temp, Boolean.TRUE);
                     //Block Val

                     temp = (value - 1) % size;
                     int multiplier = x / box_width;
                     temp += multiplier * size;

                     int temp_y = y;

                     while (temp_y - box_width >= 0) {
                        temp_y -= box_width;
                        temp += (size * box_width);
                     }

                     temp += (3 * size * size);
                     toKeep.set(temp, Boolean.TRUE);
                     //System.out.println(printLine(toKeep, size));
                     constraints.removeSimilar(toKeep);
                 }
            }
        }
    }

    private BinaryMatrix algorithmx(BinaryMatrix bm, int columnIndex) {
        //What happens if it cant remove a column

        //NOTE: this method will also remove valid rows from the list
        //These are to be added later
        ArrayList<ArrayList<Boolean>> validRows = bm.getColumnsWhereSet(columnIndex);
        columnIndex += 1;
        BinaryMatrix found = null;

        for (ArrayList<Boolean> row : validRows) {
            //System.out.println(printLine(row, bm.size));

            //System.out.println(columnIndex);
            //System.out.println("Column: " + columnIndex);
            BinaryMatrix newbm = new BinaryMatrix(bm, bm.size);

            /*System.out.println("Old Matrix");
            newbm.printMatrix();
            System.out.println("");*/

            newbm.removeSimilar(row);

           /* System.out.println("New Matrix: ");
            newbm.printMatrix();
            System.out.println("");*/
            if (bm.isComplete()) {
                //bm.printMatrix();

                if (bm.checkValidity()) {
                    return newbm;
                }
                //Check valididy of
                //The current partial solution is valid
            }

            found = algorithmx(newbm, columnIndex);

            if (found != null) {
                break;
            }
        }

        return found;
    }

    private String printLine(ArrayList<Boolean> binaryMatrix, int size) {
        String temp = "";

        for (int x = 0; x < binaryMatrix.size(); x++) {
            if (x != 0) {
                temp += ",";
            }

            if ((x % (size * size)) == 0 && x != 0) {
                temp += " ";
            }

            temp += (binaryMatrix.get(x) ? "1" : "0");
        }

        return temp;
    }

    private class BinaryMatrix {
        private ArrayList<ArrayList<Boolean>> binaryMatrix;
        private int size;

        public BinaryMatrix(BinaryMatrix bm, int size) {
            this.size = size;
            this.binaryMatrix = new ArrayList<>(bm.binaryMatrix.size());

            for (int i = 0; i < (bm.binaryMatrix.size()); i++) {
                //4 - Number Constraints
                ArrayList<Boolean> row = new ArrayList<Boolean>(size * size * 4);
                ArrayList<Boolean> oldRow = bm.binaryMatrix.get(i);

                for (int j = 0; j < (size * size * 4); j++) {
                    row.add(oldRow.get(j));
                }

                binaryMatrix.add(row);
            }
        }

        public BinaryMatrix(int size) {
            this.size = size;

            this.binaryMatrix = new ArrayList<>(size * size * size);

            for (int i = 0; i < (size * size * size); i++) {
                //4 - Number Constraints
                ArrayList<Boolean> row = new ArrayList<Boolean>(size * size * 4);

                for (int j = 0; j < (size * size * 4); j++) {
                    row.add(Boolean.FALSE);
                }

                binaryMatrix.add(row);
            }
        }

        public void add(int x, int y, boolean value, int matrix_id) {
            int matrix_offset = matrix_id * (this.size * this.size);
            List row = binaryMatrix.get(y);
            row.set(x + matrix_offset, value ? Boolean.TRUE : Boolean.FALSE);
        }

        public void generateMatrixConstraints(int size) {
            int row_val_offset = 0;
            int box_offset = 0;
            int new_column_box_offset = 0;
            int box_pos = 0;
            int box_width = (int)Math.sqrt(size);

            //TODO: Finish expanding this for all possible values
            for (int y = 0; y < size * size * size; y++) {
                if (y != 0) {
                    //Go the box ->
                    if (y % (box_width * size) == 0) {
                        box_offset += size;
                    }

                    //Reset box but don't go to new column box
                    if (box_offset % (box_width * size) == 0) {
                        box_offset = 0;
                    }

                    //Go to new column box
                    if (y % (size * size * box_width) == 0) {
                        new_column_box_offset += (size * box_width);
                    }
                }

                /*if (y % 16 == 0 && y != 0) {
                    row_val_offset += 4;
                }
*/
                if (y % (size * size) == 0 && y != 0) {
                    row_val_offset += size;
                }

                int rc_x_pos = y / size;
                int row_val_x_pos = (y % size) + row_val_offset;
                int col_val_x_pos = y % (size * size);
                int box_val_x_pos = (y % size) + box_offset + new_column_box_offset;

                this.add(rc_x_pos, y, true, CELL_MATRIX_INDEX);
                //Row Value
                this.add(row_val_x_pos, y, true, ROWS_MATRIX_INDEX);
                //Column Value
                this.add(col_val_x_pos, y, true, COLUMNS_MATRIX_INDEX);
                //Box Value
                this.add(box_val_x_pos, y, true, BOXES_MATRIX_INDEX);
            }

            //printMatrix();
        }

        public void printMatrix() {
            for (ArrayList<Boolean> bm : this.binaryMatrix) {
                System.out.println(matrixToString(bm));
            }
        }

        private String matrixToString(ArrayList<Boolean> binaryMatrix) {
            String temp = "";

            for (int x = 0; x < binaryMatrix.size(); x++) {
                if (x != 0) {
                    temp += ",";
                }

                if (x % (size * size) == 0 && x != 0) {
                    temp += " ";
                }

                temp += (binaryMatrix.get(x) ? "1" : "0");
            }

            return temp;
        }

        //This is not working properly
        public boolean isComplete() {
            if (binaryMatrix.size() <= (this.size * this.size)) {
                return true;
            }

            return false;
        }

        public ArrayList<ArrayList<Boolean>> getColumnsWhereSet(int columnIndex) {
            ArrayList<ArrayList<Boolean>> rows = new ArrayList<>();

            for (ArrayList<Boolean> row : this.binaryMatrix) {
                if (row.get(columnIndex) == Boolean.TRUE) {
                    rows.add(row);
                }
            }

            return rows;
        }

        public void removeSimilar(ArrayList<Boolean> rows) {
            ArrayList<ArrayList<Boolean>> toRemove = new ArrayList<>();

//            int row_number = 0;
            for (ArrayList<Boolean> comparisonRow : this.binaryMatrix) {
                for (int i = 0; i < comparisonRow.size(); i++) {
                    if (comparisonRow.get(i) == Boolean.TRUE && rows.get(i) == Boolean.TRUE) {
                        if (!comparisonRow.equals(rows)) {
                            //TODO: Track what is being removed and see if all the right ones are being removed
                            //System.out.print(i + ",");
                            toRemove.add(comparisonRow);
                            break;
                        }
                    }
                }

                //row_number += 1;
            }

            this.binaryMatrix.removeAll(toRemove);
        }

        public boolean checkValidity() {
            //System.out.print("Checking validity: ");
            //4 as this si the amount of restrictions placed
            ArrayList<Boolean> hasValue = new ArrayList<>(size * size * 4);

            for (int i = 0; i < this.size * this.size * 4; i++) {
                hasValue.add(Boolean.FALSE);
            }


            for (int index = 0; index < this.binaryMatrix.size(); index++) {
                ArrayList<Boolean> row = this.binaryMatrix.get(index);

                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i) == Boolean.TRUE) {
                        if (hasValue.get(i) == Boolean.TRUE) {
                            //System.out.println("Invalid " + i);
                             //System.out.println(matrixToString(row));
                            return false;
                        } else {
                            hasValue.set(i, Boolean.TRUE);
                        }
                    }
                }
            }

            for (int i = 0; i < hasValue.size(); i++) {
                if (hasValue.get(i) == Boolean.FALSE) {
                    //System.out.println("Invalid " + i);
                    return false;
                }
            }

            System.out.println("Valid");
            return true;
        }
    }

} // end of class AlgorXSolver
