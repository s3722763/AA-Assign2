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

        int number_constraints = generateMatrixConstraints(matrix, grid.getGrid().length, grid);
        //matrix.printMatrix();
        //System.out.println();

        List<DancingLinkNode> solution = new ArrayList<>();
        matrix.process(solution);

        solutionToGrid(solution, grid);
        /*for (DancingLinkNode node = matrix.rootNode.getRight(); node != matrix.rootNode; node = node.getRight()) {
            System.out.print(node.getDown().getValue() + ",");
        }*/

        //System.out.println();
        // placeholder
        int solutionSize = (grid.getGrid().length * grid.getGrid().length) - number_constraints;

        return solution.size() == solutionSize;
    } // end of solve()

    private void solutionToGrid(List<DancingLinkNode> solution, SudokuGrid grid) {
        //System.out.println("Solution");
        for (DancingLinkNode node : solution) {
            DancingLinkNode valueNode = node;
            DancingLinkNode posNode = node.getRight();

            int size =  grid.getGrid().length;
            int column_2_pos = posNode.getValue() - (size * size);

            int y = column_2_pos  / size;
            int x =  column_2_pos % size;
            int value = grid.getAllowedValues()[valueNode.getValue() % size];

            grid.addNumber(x, y, value);
        }
    }

    private int generateMatrixConstraints(DancingLinkMatrix matrix, int size, SudokuGrid grid) {
        int number_constraints = 0;
        int box_width = (int)Math.sqrt(size);
        //System.out.println("Remove Same");
        List<DancingLinkNode> nodes = new ArrayList<>();

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
                    nodes.add(matrix.findSame(toKeep));
                    number_constraints += 1;
                }
            }
        }

        matrix.removeSame(nodes);
        return number_constraints;
    }

    //TODO: Column header nodes point to null in their up direction. Fix
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
                System.out.print(node.getDown().getValue() + ",");
                node = node.getRight();
            }

            System.out.println();
        }

        private void removeSame(List<DancingLinkNode> nodes) {
            for (DancingLinkNode node : nodes) {
                DancingLinkNode rowNode = node;

                for (int i = 0; i < 4; i++) {
                    cover(rowNode);
                    //System.out.println("----------------");
                    //printMatrix();
                    rowNode = rowNode.getRight();
                }
            }
        }

        private DancingLinkNode findSame(List<Integer> removalOffsets) {
            //Get the first offset (this should be between 0 & 15 inclusive)
            DancingLinkNode columnNodes = getColumnNode(removalOffsets.get(0));
            DancingLinkNode row = null;

            for (DancingLinkNode columnNode = columnNodes.getDown(); columnNode != columnNodes; columnNode = columnNode.getDown()) {
                int index = 1;
                boolean correct = true;

                for (DancingLinkNode rowNode = columnNode.getRight(); rowNode != columnNode; rowNode = rowNode.getRight()) {
                    if (!(rowNode.getValue() == removalOffsets.get(index))) {
                        correct = false;
                        break;
                    }

                    index += 1;
                }

                if (correct) {
                    row = columnNode;
                    break;
                }
            }

            assert(row != null);

            return row;
        }

        private DancingLinkNode getColumnNode(int offset) {
            DancingLinkNode node = this.rootNode.getRight();

            //TODO: Check < OR <=
            for (int i = 0; i < offset; i++) {
                node = node.getRight();
            }

            return node;
        }

        public boolean process(List<DancingLinkNode> solution) {
            //printMatrix();
            //Exit condition
            if (this.rootNode.getRight() == this.rootNode) {
                return true;
            }

            //Just remove nodes in the column
            DancingLinkNode coveredColumn = this.rootNode.getRight();
            cover(coveredColumn);

            for (DancingLinkNode node = coveredColumn.getDown(); node != coveredColumn; node = node.getDown()) {
                //Add current row to solution
                solution.add(node);

                for (DancingLinkNode rowNode = node.getRight(); rowNode != node; rowNode = rowNode.getRight()) {
                    cover(rowNode);
                }

                if (process(solution)) {
                    return true;
                }

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

        //hides the column. Returns all of the nodes in the
        private List<DancingLinkNode> removeColumn(DancingLinkNode node) {
            //TODO: Remove comment
            //Error caused by coverColumn
            cover(node);
            return getNodesInColumn(node);
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
            /*if (!node.isColumnNode()) {
                node.getColumn().setValue(node.getColumn().getValue() - 1);
            }*/

            return true;
        }

        private void generateMatrixConstraints(int size) {
            int row_val_offset = 0;
            int box_offset = 0;
            int new_column_box_offset = 0;
            int box_pos = 0;
            int box_width = (int)Math.sqrt(size);

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

            //printMatrix();
        }


        private void add(int x, int y, DancingLinkNode node, int matrix_id) {
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
} // end of class DancingLinksSolver


