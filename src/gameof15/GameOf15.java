package gameof15;

import java.util.Random;

public class GameOf15 {
	private int[][] gameBoard;

	public static final int NUM_ROWS = 4;
	public static final int NUM_COLS = 4;
	private static final int SCRAMBLE_MOVES = 50;


	/**
	 * Construct a new game of 15 with the board scrambled
	 * The puzzle is guaranteed to be solvable when generated in this way.
	 */
	public GameOf15() {
		gameBoard = new int[NUM_ROWS][NUM_COLS];
		initializeBoard();
		scramblePieces();
	}

	/**
	 * Create a new game of 15 using the provided board.
	 * You will not need to use this constructor.
	 * @param board the board to use
	 */
	public GameOf15(int[][] board) {
		gameBoard = board;
	}

	private void initializeBoard() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				gameBoard[row][col] = (row*4 + (col + 1)) % 16;
			}
		}
	}

	/**
	 * Check to see if the puzzle has been solved
	 * @return true is the puzzle is in a solved state, false otherwise
	 */
	public boolean hasWon() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				if (gameBoard[row][col] != (row*4 + (col + 1)) % 16) {
					return false;
				}
			}
		}
		return true;
	}

	private void scramblePieces() {
		Random r = new Random();
		for (int i = 0; i < SCRAMBLE_MOVES; i++) {
			switch (r.nextInt(4)) {
			case 0:
				moveUp();
				break;
			case 1:
				moveLeft();
				break;
			case 2:
				moveDown();
				break;
			case 3:
				moveRight();
				break;
			}
		}
	}

	/**
	 * Perform an upwards move if possible.
	 * @return true is the move was made, false otherwise
	 */
	public boolean moveUp() {
		// first, see if we can move up
		Point blank = findBlank();
		if (blank == null || blank.row == NUM_ROWS - 1) {
			return false;
		}
		// move up
		gameBoard[blank.row][blank.col] = gameBoard[blank.row + 1][blank.col];
		gameBoard[blank.row + 1][blank.col] = 0;
		return true;
	}

	/**
	 * Move left, if possible.
	 * @return true is the move was made, false otherwise
	 */
	public boolean moveLeft() {
		// first, see if we can move left
		Point blank = findBlank();
		if (blank == null || blank.col == NUM_COLS - 1) {
			return false;
		}
		// move left
		gameBoard[blank.row][blank.col] = gameBoard[blank.row][blank.col + 1];
		gameBoard[blank.row][blank.col + 1] = 0;
		return true;
	}

	/**
	 * Move downwards, if possible.
	 * @return true is the move was made, false otherwise
	 */
	public boolean moveDown() {
		// first, see if we can move down
		Point blank = findBlank();
		if (blank == null || blank.row == 0) {
			return false;
		}
		//move down
		gameBoard[blank.row][blank.col] = gameBoard[blank.row - 1][blank.col];
		gameBoard[blank.row - 1][blank.col] = 0;
		return true;
	}

	/**
	 * Move to the right, if possible.
	 * @return true is the move was made, false otherwise
	 */
	public boolean moveRight() {
		// first, see if we can move right
		Point blank = findBlank();
		if (blank == null || blank.col == 0)
			return false;
		//move right
		gameBoard[blank.row][blank.col] = gameBoard[blank.row][blank.col - 1];
		gameBoard[blank.row][blank.col - 1] = 0;
		return true;
	}

	public Point findBlank() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				if (gameBoard[row][col] == 0)
					return new Point(row, col);
			}
		}
		return null;
	}

	/**
	 * Gets the value at a particular row or column of the game board. A value of "0" represents
	 * the blank space.
	 * The number of a row and a column is zero based.S
	 * @param row the row index
	 * @param col the column index
	 * @return the value at the [row][col] position
	 */
	public int getValue(int row, int col) {
		return gameBoard[row][col];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int[] row : gameBoard) {
			for (int v : row) {
				sb.append((v == 0 ? " " : v));
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
