package Board;

import java.util.*;

/**
 * Grid
 *
 * This is a Grid for the classic puzzle game 'Sudoku.'
 * It incorporates a backtracking solving algorithm which is
 * capable of generating/solving 'Sudoku' puzzles.
 *
 * @author Ellie Moore
 * @version 03.15.2020
 */
public class Grid {

    private final int LEVEL_MULTIPLIER = 22;
    private final int SUB_GRID_SIZE = 3;
    private final int GRID_SIZE = 9;

    /**
     * A random number generator.
     */
    private final Random rgen = new Random();

    /**
     * The answer array to check the client's attempts.
     */
    private int[][] answerArray;

    /**
     * An array that will hold the clients guesses.
     */
    private int[][] guessArray;

    /**
     * An array to hold the grid.
     */
    private int[][] grid;

    /**
     * A default constructor for a {@code Grid}.
     *
     * @param level the level of difficulty.
     */
    public Grid(final int level) {

        this.grid = new int[GRID_SIZE][GRID_SIZE];
        this.guessArray = new int[GRID_SIZE][GRID_SIZE];
        populate(level);

    }

    /**
     * A method to check if a number exists in a row.
     *
     * @param row the row to be checked
     * @param num the number to look for
     * @return whether or not the row contains the number
     */
    private boolean isInRow(final int row, final int num) {

        for (int i = 0; i < grid[row].length; i++) {
            if (grid[row][i] == num) {
                return true;
            }
        }
        return false;

    }

    /**
     * A method to check if a number exists in a column.
     *
     * @param col the column to be checked
     * @param num the number to look for
     * @return whether or not the column contains the number
     */
    private boolean isInColumn(final int col, final int num) {

        for (int[] row: grid) {
            if (row[col] == num) {
                return true;
            }
        }
        return false;

    }

    /**
     * A method which determines whether or not a specified value shares a 3x3
     * sub-array with the given coordinates.
     *
     * @param row the row of the 3x3
     * @param col the column of the 3x3
     * @param num the number to look for
     */
    private boolean isIn3x3(final int row, final int col, final int num) {

        int i = row - (row % 3);
        int j = col - (col % 3);
        for (int k = i; k < (i + SUB_GRID_SIZE); k++) {
            for (int n = j; n < (j + SUB_GRID_SIZE); n++) {
                // System.out.print(k + " ");
                // System.out.println(n);
                // System.out.println(grid[k][n]); //<-- Debug.
                if (grid[k][n] == num) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * A method which determines if the specified value shares a row, column, or
     * sub-array with the specified coordinates.
     *
     * @param row the row to be checked
     * @param col the column to be checked
     * @param num the number to look for
     */
    private boolean hasNumberAt(final int row, final int col, final int num) {

        return isInRow(row, num) || isInColumn(col, num) || isIn3x3(row, col, num);

    }

    /**
     * This is a method that tries out each fitting value (1-9) at
     * each empty index in the {@code grid}. If no values fit, the method returns
     * {@code false}, replaces the previous index with a zero, increments and tries
     * the next highest value at that cell. In other words, it back-tracks whenever
     * no values will fit at a current index. It does this until the entire {@code grid} is
     * filled. It utilizes a brute-force, recursive algorithm.
     *
     * @return whether or not the grid is solved
     */
    private boolean isSolved() {

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    for (int k = 1; k <= GRID_SIZE; k++) {
                        if (!hasNumberAt(i, j, k)) {
                            grid[i][j] = k;
                            if (isSolved()) {
                                return true;
                            } else {
                                grid[i][j] = 0;
                            }
                        }
                    }
                    // System.out.println(this); //<--Debug.
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * This method uses the same algorithm as {@code isSolved()} only, it removes
     * every value that it places upon solving the puzzle.
     *
     * @return whether or not the puzzle is solved
     * @see Grid#isSolved()
     */
    private boolean isSolvedAndErased() {

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    for (int k = 1; k <= GRID_SIZE; k++) {
                        if (!hasNumberAt(i, j, k)) {
                            grid[i][j] = k;
                            if (isSolvedAndErased()) {
                                grid[i][j] = 0;
                                return true;
                            } else {
                                grid[i][j] = 0;
                            }
                        }
                    }
                    // System.out.println(this); //<--Debug.
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Determines if the puzzle has a unique solution by trying all possible numbers
     * at each open index and calling {@code isSolvedAndErased()} in order to test
     * if the puzzle is solvable. If the puzzle is solvable for more than one of the
     * possible values, then the method returns {@code false}.
     *
     * @return whether or not the puzzle has one unique solution
     */
    private boolean isUnique() {

        boolean solvedOnce = false;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    for (int k = 1; k <= GRID_SIZE; k++) {
                        if (!hasNumberAt(i, j, k)) {
                            //System.out.println(k);
                            //System.out.println(this); //<--Debug.
                            grid[i][j] = k;
                            if (isSolvedAndErased()) {
                                //System.out.println("solved once"); //<--Debug.
                                if (solvedOnce) {
                                    //System.out.println("solved twice"); //<--Debug.
                                    grid[i][j] = 0;
                                    return false;
                                }
                                solvedOnce = true;
                            }
                        }
                    }
                    grid[i][j] = 0;
                    solvedOnce = false;
                }
            }
        }

        return true;

    }

    /**
     * This method seeds a puzzle by placing random values in the 0th row.
     * Next, it calls {@code isSolved()} to generate a valid {@code answerGrid}.
     * Finally, it replaces random indices with zeros. The maximum amount of these
     * zeros is determined by the specified difficulty level, and the minimum
     * amount is determined by whether or not the placement of each zero leads to
     * a puzzle with more than one solution (An unsolvable puzzle).
     *
     * @param level the difficulty level
     */
    private void populate(final int level) {

        final int maxNumberOfEmptySpaces = (level * LEVEL_MULTIPLIER);

        for (int i = 1, random; i <= GRID_SIZE; i++) {
            do {
                random = rgen.nextInt(GRID_SIZE);
            } while (grid[0][random] != 0);
            grid[0][random] = i;
        }
        if (isSolved()) {
            this.answerArray = subGridsToRows();
        }
        for (int i = 0, k, z, temp; i < maxNumberOfEmptySpaces; i++) {
            do {
                k = rgen.nextInt(GRID_SIZE);
                z = rgen.nextInt(GRID_SIZE);
            } while (grid[k][z] == 0);
            temp = grid[k][z];
            grid[k][z] = 0;
            if (!isUnique()) {
                grid[k][z] = temp; //replace the number if a unique solution cannot be found.
            }
        }
        this.grid = subGridsToRows();

    }

    /**
     * A method for checking whether or not a value at a specified index is equal to
     * the value on the {@code answerGrid}.
     *
     * @param row the row
     * @param col the column
     * @return whether or not the index is empty
     */
    public boolean isEmptyIndex(final int row, final int col) {

        return grid[row][col] == 0;

    }

    /**
     * This is a method for determining whether or not a number corresponds with
     * the indicated element int the {@code answerGrid}.
     *
     * @param row the row
     * @param col the column
     * @param num the value to check for
     * @return whether or not the number is present at a given index in
     * the {@code answerGrid}
     */
    public boolean isAnswer(final int row, final int col, final int num) {

        return answerArray[row][col] == num;

    }

    /**
     * A method for converting the {@code grid} into an {@code Array} of rows that the client
     * might find easier to work with.
     *
     * @return a new {@code Array} with sub-grids re-arranged as rows
     */
    private int[][] subGridsToRows() {

        final int[][] outputArray = new int[GRID_SIZE][GRID_SIZE];

        for (int i = 0, j = 0; i < grid.length - 2; i += 3) {
            for (int k = 0; k < grid[i].length - 2; k += 3, j++) {
                for (int p = i, n = 0; p < i + SUB_GRID_SIZE; p++) {
                    for (int z = k; z < k + SUB_GRID_SIZE; z++) {
                        outputArray[j][n++] = grid[p][z];
                    }
                }
            }
        }

        return outputArray;

    }

    /**
     * A method that allows the client to access a value in the {@code Grid}.
     *
     * @param row the row
     * @param col the column
     * @return the value at the given indices
     */
    public int get(final int row, final int col) {

        return grid[row][col];

    }

    /**
     * A method that allows access to the {@code guessArray}.
     *
     * @param row the row
     * @param col the column
     * @return the value at the specified indices
     */
    public int getGuess(final int row, final int col) {

        return guessArray[row][col];

    }

    /**
     * A method for setting the given index in the rowArray to a given value.
     *
     * @param row the row
     * @param col the column
     * @param num the value to set
     */
    public void set(final int row, final int col, final int num) {

        grid[row][col] = num;

    }

    /**
     * A method for setting the given index in the guessArray to a given value.
     *
     * @param row the row
     * @param col the column
     * @param num the value to set
     */
    public void setGuess(final int row, final int col, final int num) {

        guessArray[row][col] = num;

    }

    /**
     * A method for determining whether or not the puzzle is complete.
     *
     * @return whether or not the {@code Grid} is full (no zeros left)
     */
    public boolean isComplete() {

        for (int[] row: grid) {
            for (int j = 0; j < grid.length; j++) {
                if (row[j] == 0) {
                    return false;
                }
            }
        }
        return true;

    }

}
