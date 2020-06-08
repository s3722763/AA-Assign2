/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.KillerSudokuGrid;
import grid.SudokuGrid;
import util.GridIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Your advanced solver for Killer Sudoku.
 */
public class KillerAdvancedSolver extends KillerSudokuSolver
{
    public KillerAdvancedSolver() {

    } // end of KillerAdvancedSolver()

    @Override
    public boolean solve(SudokuGrid grid) {
        //Initialize grid
        DancingLinkMatrix matrix = new DancingLinkMatrix(grid.getGrid().length);
        Map<GridIndex, List<Integer>> allowedValues = generateCageValues((KillerSudokuGrid) grid);
        matrix.generateMatrixConstraints(grid.getGrid().length, allowedValues, grid);

        List<DancingLinkNode> solution = new ArrayList<>();
        matrix.process(solution, (KillerSudokuGrid) grid);
        solutionToGrid(solution, grid);

        return solution.size() > 0;
    } // end of solve()

    private void solutionToGrid(List<DancingLinkNode> solution, SudokuGrid grid) {
        //System.out.println("Solution");
        for (DancingLinkNode node : solution) {
            DancingLinkNode valueNode = node;
            DancingLinkNode posNode = node.getRight();

            int size =  grid.getGrid().length;
            int column_2_pos = posNode.getValue() - (size * size);

            int y = column_2_pos / size;
            int x =  column_2_pos % size;
            int value = grid.getAllowedValues()[valueNode.getValue() % size];

            grid.addNumber(x, y, value);
        }
    }

    private Map<GridIndex, List<Integer>> generateCageValues(KillerSudokuGrid grid) {
        Map<GridIndex, List<Integer>> allowedValuesMap = new HashMap<>();

        for (KillerSudokuGrid.Cage cage : grid.getCages()) {
            int target = cage.getTarget();
            int size = cage.getSize();
            List<Integer> currentCombo = new ArrayList<>();
            List<List<Integer>> combinations = new ArrayList<>();
            countStart(target, size, grid, currentCombo, combinations);

            for (List<Integer> combo : combinations) {
                for (int i = 0; i < combo.size(); i++) {
                    int value = combo.get(i);
                    GridIndex pos = cage.getPositions().get(i);

                    if (allowedValuesMap.get(pos) == null) {
                        allowedValuesMap.put(pos, new ArrayList<>());
                    }
                    //System.out.println("X: " + pos.getX() + "\tY: " + pos.getY() + "\tValue: " + value);
                    allowedValuesMap.get(pos).add(value);
                }
            }
        }

        //System.out.println();
        return allowedValuesMap;
    }


    private void countStart(int target, int size, SudokuGrid grid, List<Integer> currentComb, List<List<Integer>> combinations) {
        for (Integer number : grid.getAllowedValues()) {
            List<Integer> currentCombo = new ArrayList<>(size);

            currentCombo.add(number);
            int currentIter = 1;

            count(target, size, grid, currentIter, currentCombo, combinations);
        }
    }

    private void count(int target, int count, SudokuGrid grid, int currentIter, List<Integer> currentCombo, List<List<Integer>> combinations) {
        currentIter += 1;

        for (Integer number : grid.getAllowedValues()) {
            currentCombo.add(number);

            if (currentIter >= count) {
                if (sum(currentCombo) == target) {
                    copyToList(currentCombo, combinations);
                }
            } else {
                count(target, count, grid, currentIter, currentCombo, combinations);
            }

            currentCombo.remove(currentCombo.size() - 1);
        }
    }

    private void copyToList(List<Integer> currentCombo, List<List<Integer>> combinations) {
        List<Integer> newlist = new ArrayList<Integer>(currentCombo.size());

        for (Integer number : currentCombo) {
            newlist.add(number);
        }

        combinations.add(newlist);
    }

    private int sum(List<Integer> numbers) {
        int amount = 0;

        for (Integer number : numbers) {
            amount += number;
        }

        return amount;
    }

    private class DancingLinkMatrix {
        private int size;
        private DancingLinkNode rootNode;

        public DancingLinkMatrix(int size) {
            this.size = size;
            this.rootNode = new DancingLinkNode();
            DancingLinkNode currentNode = this.rootNode;
            //4 : number of constraints
            for (int i = 0; i < (size * size * 4); i++) {
                DancingLinkNode tempNode = new DancingLinkNode();

                currentNode.setRight(tempNode);
                tempNode.setLeft(currentNode);

                //The column node is itself
                tempNode.setColumn(tempNode);

                currentNode = tempNode;
                //Have the column node link to itself
                tempNode.setDown(tempNode);
            }

            //Set last node right to root node
            currentNode.setRight(this.rootNode);
            this.rootNode.setLeft(currentNode);
        }

        public void printMatrix() {
            DancingLinkNode node = this.rootNode.getRight();

            while (node != this.rootNode) {
                System.out.print(node.getDown().getValue() + ",");
                node = node.getRight();
            }

            System.out.println();
        }

        private DancingLinkNode getColumnNode(int offset) {
            DancingLinkNode node = this.rootNode.getRight();

            for (int i = 0; i < offset; i++) {
                node = node.getRight();
            }

            return node;
        }

        public boolean process(List<DancingLinkNode> solution, KillerSudokuGrid grid) {
            //printMatrix();

            //Exit condition
            if (this.rootNode.getRight() == this.rootNode) {
                solutionToGrid(solution, grid);
                //System.out.println(grid.toString());
                if (grid.killerConstraintsValid()) {
                    return true;
                } else {
                    grid.reset();
                    return false;
                }
            }

            //Just remove nodes in the column
            DancingLinkNode coveredColumn = this.rootNode.getRight();
            cover(coveredColumn);

            for (DancingLinkNode node = coveredColumn.getDown(); node != coveredColumn; node = node.getDown()) {
                //Add current row to solution
                solution.add(node);

                //solutionToGrid(solution, grid);
                //System.out.println(grid.toString());

                for (DancingLinkNode rowNode = node.getRight(); rowNode != node; rowNode = rowNode.getRight()) {
                    cover(rowNode);
                }

                if (process(solution, grid)) {
                    return true;
                }

                //coveredColumn = node.getColumn();
                solution.remove(node);

                for (DancingLinkNode rowNode = node.getLeft(); rowNode != node; rowNode = rowNode.getLeft()) {
                    uncover(rowNode);
                }
            }

            uncover(coveredColumn);
            return false;
        }

        private void uncover(DancingLinkNode node) {
            DancingLinkNode columnNode = node.getColumn();

            for (DancingLinkNode nodeInColumn = columnNode.getUp(); nodeInColumn != columnNode; nodeInColumn = nodeInColumn.getUp()) {
                for (DancingLinkNode nodeInRow = nodeInColumn.getLeft(); nodeInRow != nodeInColumn; nodeInRow = nodeInRow.getLeft()) {
                    uncoverRow(nodeInRow);
                }
            }

            uncoverColumn(columnNode);
        }

        private void cover(DancingLinkNode node) {
            node = node.getColumn();
            coverColumn(node);

            for (DancingLinkNode columnNode = node.getDown(); columnNode != node; columnNode = columnNode.getDown()) {
                for (DancingLinkNode rowNode = columnNode.getRight(); rowNode != columnNode; rowNode = rowNode.getRight()) {
                    //assert(rowNode.isColumnNode());
                    coverRow(rowNode);
                    //System.out.println("Covering: " + rowNode.getValue());
                }
            }
        }

        private void uncoverColumn(DancingLinkNode node) {
            //Cover column
            node.getLeft().setRight(node);
            node.getRight().setLeft(node);
            //System.out.println("Uncover column");
        }

        private void uncoverRow(DancingLinkNode node) {
            //Cover row
            node.getUp().setDown(node);
            node.getDown().setUp(node);
            //node.getColumn().setValue(node.getColumn().getValue() + 1);
            //System.out.println("Uncover row");
        }

        public void coverColumn(DancingLinkNode node) {
            //Cover column
            node.getLeft().setRight(node.getRight());
            node.getRight().setLeft(node.getLeft());
            //System.out.println("Cover column");
        }

        public void coverRow(DancingLinkNode node) {
            //Cover row
            node.getUp().setDown(node.getDown());
            node.getDown().setUp(node.getUp());
            //System.out.println("Cover Row");
            /*if (!node.isColumnNode()) {
                node.getColumn().setValue(node.getColumn().getValue() - 1);
            }*/
        }

        private void generateMatrixConstraints(int size, Map<GridIndex, List<Integer>> allowedValues, SudokuGrid grid) {
            int row_val_offset = 0;
            int box_offset = 0;
            int new_column_box_offset = 0;
            int box_width = (int) Math.sqrt(size);

            for (int y = 0; y < size * size * size; y++) {
                //System.out.println(y);

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

                if (y % (size * size) == 0 && y != 0) {
                    row_val_offset += size;
                }

                int rc_x_pos = y / size;
                int row_val_x_pos = (y % size) + row_val_offset;
                int col_val_x_pos = y % (size * size);
                int box_val_x_pos = (y % size) + box_offset + new_column_box_offset;

                //if (isValidValueAndPos(rc_x_pos, row_val_x_pos, allowedValues, grid)) {
                    //System.out.println("X: " + rc_x_pos % size + "\tY: " + row_val_x_pos / size + "\tValue: " + (row_val_x_pos % size));
                    DancingLinkNode posNode = new DancingLinkNode();
                    DancingLinkNode rowNode = new DancingLinkNode();
                    DancingLinkNode columnNode = new DancingLinkNode();
                    DancingLinkNode boxNode = new DancingLinkNode();

                    posNode.setLeft(boxNode);
                    posNode.setRight(rowNode);

                    rowNode.setLeft(posNode);
                    rowNode.setRight(columnNode);

                    columnNode.setLeft(rowNode);
                    columnNode.setRight(boxNode);

                    boxNode.setLeft(columnNode);
                    boxNode.setRight(posNode);

                    this.add(rc_x_pos, posNode, CELL_MATRIX_INDEX);
                    //Row Value
                    this.add(row_val_x_pos, rowNode, ROWS_MATRIX_INDEX);
                    //Column Value
                    this.add(col_val_x_pos, columnNode, COLUMNS_MATRIX_INDEX);
                    //Box Value
                    this.add(box_val_x_pos, boxNode, BOXES_MATRIX_INDEX);
                    //System.out.println(rc_x_pos);
                //}
            }

            //printMatrix();
        }

        private boolean isValidValueAndPos(int rc_x_pos, int row_val_x_pos, Map<GridIndex, List<Integer>> allowedValues, SudokuGrid grid) {
            int y = rc_x_pos / size;
            int x =  rc_x_pos % size;
            int value = grid.getAllowedValues()[row_val_x_pos % size];

            boolean allowed = allowedValues.get(new GridIndex(x, y, size)).contains(value);

           /* if (allowed) {
                System.out.println("X: " + x + "\tY: " + y + "\tValue: " + value);
            }*/

            return allowed;
        }

        private void add(int x, DancingLinkNode node, int matrix_id) {
            int matrix_offset = matrix_id * (this.size * this.size);
            DancingLinkNode columnNode = getColumnNode(matrix_offset + x);
            node.setColumn(columnNode);
            node.setValue(matrix_offset + x);

            DancingLinkNode tempNode = columnNode;

            while (tempNode.getDown() != columnNode) {
                tempNode = tempNode.getDown();
            }

            tempNode.setDown(node);
            node.setUp(tempNode);
            node.setDown(columnNode);
            columnNode.setUp(node);
            //System.out.println("Adding to column: " + columnNode);
        }
    }
} // end of class KillerAdvancedSolver
