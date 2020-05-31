/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.SudokuGrid;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver
{
    final int CELL_MATRIX_INDEX = 0;
    final int ROWS_MATRIX_INDEX = 1;
    final int COLUMNS_MATRIX_INDEX = 2;
    final int BOXES_MATRIX_INDEX = 3;

    public DancingLinksSolver() {
        // TODO: any initialisation you want to implement.
    } // end of DancingLinksSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        //Initialize grid
        DancingLinkMatrix matrix = new DancingLinkMatrix(grid.getGrid().length);
        matrix.generateMatrixConstraints(grid.getGrid().length);

        //For testing
        //matrix.removeColumn(matrix.rootNode.right);
        //generateMatrixConstraints(matrix, grid.getGrid().length, grid);
        List<DancingLinkNode> solution = new ArrayList<>();
        matrix.process(solution, matrix.rootNode.getRight());

        solutionToGrid(solution, grid);

        // placeholder
        return false;
    } // end of solve()

    private void solutionToGrid(List<DancingLinkNode> solution, SudokuGrid grid) {
        for (DancingLinkNode node : solution) {
            for (int i = 0; i < 4; i++) {
                System.out.print(node.getDistanceFromRoot() + ",");
            }

            System.out.println();
        }

    }

    private void generateMatrixConstraints(DancingLinkMatrix matrix, int size, SudokuGrid grid) {
        int box_width = (int)Math.sqrt(size);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (grid.getGrid()[y][x] != 0) {
                    ArrayList<Integer> toKeep = new ArrayList<>(4);

                    int value = grid.getGrid()[y][x];

                    //Row Column
                    int temp = (x % size) + (y * size);
                    toKeep.add(temp);
                    //Row Val
                    temp = ((value - 1) % size) + (y * size);
                    temp += (size * size);
                    toKeep.add(temp);
                    //Col Val

                    temp = (value - 1) + (x * size);
                    temp += (2 * size * size);
                    toKeep.add(temp);
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
                    toKeep.add(temp);
                    //System.out.println(printLine(toKeep, size));
                    matrix.removeSame(toKeep);
                }
            }
        }
    }

    //TODO: Column header nodes point to null in their up direction. Fix
    private class DancingLinkMatrix {
        private int size;
        private DancingLinkNode rootNode;

        public DancingLinkMatrix(int size) {
            this.size = size;
            this.rootNode = new DancingLinkNode(0);
            DancingLinkNode currentNode = this.rootNode;
            //4 : number of constraints
            for (int i = 0; i < (size * size * 4); i++) {
                DancingLinkNode tempNode = new DancingLinkNode(0);

                currentNode.setRight(tempNode);
                tempNode.setLeft(currentNode);

                //The column node is itself
                tempNode.setColumn(tempNode);

                currentNode = tempNode;
                //TODO: What about setup?
                //Have the column node link to itself
                tempNode.setDown(tempNode);
            }

            //Set last node right to root node
            currentNode.setRight(this.rootNode);
            //TODO: Setup column nodes
        }

        public void printMatrix() {
            DancingLinkNode node = this.rootNode.getRight();

            while (node != this.rootNode) {
                System.out.print(node.getValue() + ",");
                node = node.getRight();
            }
            System.out.println();
        }

        private void removeSame(List<Integer> removalOffsets) {
            //TODO: use remove column on each of the offsets
            //The columns are gotten from this root node
            for (Integer i : removalOffsets) {
                DancingLinkNode node = getColumnNode(i);

                removeColumn(node);
            }
        }

        private DancingLinkNode getColumnNode(int offset) {
            DancingLinkNode node = this.rootNode.getRight();

            //TODO: Check < OR <=
            for (int i = 0; i < offset; i++) {
                node = node.getRight();
            }

            return node;
        }

        public boolean process(List<DancingLinkNode> solution, DancingLinkNode columnNode) {
            printMatrix();
            //Exit condition
            if (solution.size() == (this.size * this.size)) {
                return validate(solution);
            }

            //Just remove nodes in the column
            DancingLinkNode coveredColumn = columnNode;
            List<DancingLinkNode> candidates = removeColumn(columnNode);

            for (DancingLinkNode node : candidates) {
                //Add current row to solution
                solution.add(node);
                List<DancingLinkNode> removedNodes = new ArrayList<>();

                //For each node in the row
                List<DancingLinkNode> nodesToCoverFromColumn = getNodesInRow(node);

                for (DancingLinkNode nodeToCover : nodesToCoverFromColumn) {
                    List<DancingLinkNode> nodesInColumn = getNodesInColumn(nodeToCover);

                    for (DancingLinkNode nodeInColumn : nodesInColumn) {
                        if (!nodeInColumn.isColumnNode()) {
                            List<DancingLinkNode> rowNodes = getNodesInRow(nodeInColumn);

                            for (DancingLinkNode rowNode : rowNodes) {
                                coverRow(rowNode);
                                removedNodes.add(rowNode);
                            }

                            coverRow(nodeInColumn);
                            removedNodes.add(nodeInColumn);
                        }
                    }

                    coverRow(nodeToCover);
                    removedNodes.add(nodeToCover);
                }

                if (process(solution, columnNode.getRight())) {
                    return true;
                } else {
                    solution.remove(node);

                    for (DancingLinkNode nodeToUncover : removedNodes) {
                        uncoverRow(nodeToUncover);
                    }
                }
            }
            //uncoverColumn(coveredColumn);
            return false;
        }

        //hides the column. Returns all of the nodes in the
        private List<DancingLinkNode> removeColumn(DancingLinkNode node) {
            //TODO: Remove comment
            //Error caused by coverColumn
            //coverColumn(node);
            return getNodesInColumn(node);
        }

        //Gets all ndoes in row. DOES NOT INCLUDE NODE IN ARGUMENT
        private List<DancingLinkNode> getNodesInRow(DancingLinkNode rowNode) {
            List<DancingLinkNode> nodes = new ArrayList<>();
            DancingLinkNode tempNodeRef = rowNode.getRight();

            while (tempNodeRef != rowNode) {
                nodes.add(tempNodeRef);
                tempNodeRef = tempNodeRef.getRight();
            }

            return nodes;
        }

        //Returns nodes in cloumn. DOES NOT INCLUDE NODE SPECIFIED IN ARGUMENT
        private List<DancingLinkNode> getNodesInColumn(DancingLinkNode columnNode) {
            List<DancingLinkNode> nodes = new ArrayList<>();
            DancingLinkNode tempNodeRef = columnNode.getDown();

            while (tempNodeRef != columnNode) {
                nodes.add(tempNodeRef);
                tempNodeRef = tempNodeRef.getDown();
            }

            return nodes;
        }

        private boolean validate(List<DancingLinkNode> solution) {
            //TODO: Check if this works
            List<DancingLinkNode> columnsInSolution = new ArrayList<>();

            for (DancingLinkNode node : solution) {
                System.out.println(node.getColumn().getValue());

                DancingLinkNode tempNode = node;

                do {
                    if (columnsInSolution.contains(tempNode.getColumn())) {
                        return false;
                    } else {
                        columnsInSolution.add(tempNode.getColumn());
                    }

                    tempNode = tempNode.getRight();
                } while (tempNode != node);
            }

            return true;
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

            node.getColumn().setValue(node.getColumn().getValue() + 1);
            //System.out.println("Uncover row");
        }

        public boolean coverColumn(DancingLinkNode node) {
            //Cover column
            node.getLeft().setRight(node.getRight());
            node.getRight().setLeft(node.getLeft());
            //System.out.println("Cover column");
            return true;
        }

        public boolean coverRow(DancingLinkNode node) {
            //Cover row
            node.getUp().setDown(node.getDown());
            node.getDown().setUp(node.getUp());
            //System.out.println("Cover Row");
            if (!node.isColumnNode()) {
                node.getColumn().setValue(node.getColumn().getValue() - 1);
            }

            return true;
        }

        private void generateMatrixConstraints(int size) {
            int row_val_offset = 0;
            int box_offset = 0;
            int new_column_box_offset = 0;
            int box_pos = 0;
            int box_width = (int)Math.sqrt(size);

            for (int y = 0; y < size * size * size; y++) {
                System.out.println(y);

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

                this.add(rc_x_pos, y, posNode, CELL_MATRIX_INDEX);
                //Row Value
                this.add(row_val_x_pos, y, rowNode, ROWS_MATRIX_INDEX);
                //Column Value
                this.add(col_val_x_pos, y, columnNode, COLUMNS_MATRIX_INDEX);
                //Box Value
                this.add(box_val_x_pos, y, boxNode, BOXES_MATRIX_INDEX);
            }

            printMatrix();
        }


        private void add(int x, int y, DancingLinkNode node, int matrix_id) {
            int matrix_offset = matrix_id * (this.size * this.size);
            DancingLinkNode columnNode = getColumnNode(matrix_offset + x);
            node.setColumn(columnNode);
            columnNode.setValue(columnNode.getValue() + 1);

            DancingLinkNode tempNode = columnNode;

            while (tempNode.getDown() != columnNode) {
                tempNode = tempNode.getDown();
            }

            tempNode.setDown(node);
            node.setUp(tempNode);
            node.setDown(columnNode);
            columnNode.setUp(node);

            System.out.println("Adding to column: " + columnNode);
        }

    }
} // end of class DancingLinksSolver


