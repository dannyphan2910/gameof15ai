package gameof15;

import java.awt.Point;
import java.util.*;

public class ComputerPlayer {

	public final static GameOf15 SOLVED = new GameOf15();

	public final static int DIMENSION = 4;
	
	private int[][] board;
	private Point blankPos;

	public static void main(String[] args) {
		ComputerPlayer comp = new ComputerPlayer();
		System.out.println(comp.solveReal(new GameOf15()));
	}

	/**
	 * 
	 * @param game the original game of 15 board
	 * @return the solved game of 15 board
	 */

	public GameOf15 solveReal(GameOf15 game) {
		// reference source: https://gist.github.com/leopd/5992493 (Solving the fifteen puzzle in Java using A* and Dijkstra's algorithm)
		// assumption: all 'given' GameOf15 boards are solvable!

		show(solver(game));
		// findMoves(solver(game));
		return solver(game).get(solver(game).size()-1);
	}

	// This method finds the move (left/right/up/down) taken between each state of the board
	private void findMoves(List<GameOf15> sol) {
		if (sol != null) {
			// System.out.println("Solution with number of moves: " + (sol.size() - 1)); // this displays the number of moves taken to solve the game
			// the loop goes through the list of states
			for (int i = 1; i < sol.size(); i++) {
				GameOf15 current = sol.get(i);
				GameOf15 last = sol.get(i - 1);
				Point blankCurrent = getBlankPos(current);
				Point blankLast = getBlankPos(last);

				int dx = blankCurrent.x - blankLast.x; 									// the difference in x-coordinate
				int dy = blankCurrent.y - blankLast.y;							   		// the difference in y-coordinate

				// 4 cases for 4 directions
				if (dy == -1) {
					System.out.println('r');
				} else if (dy == 1) {
					System.out.println('l');
				} else if (dx == -1) {
					System.out.println('d');
				} else if (dx == 1) {
					System.out.println('u');
				}

			}
		} else {
			return; 																	// for empty list (unlikely)
		}
	}

	// This method solves the game using A* algorithm
	private List<GameOf15> solver(GameOf15 game) {
		HashMap<GameOf15, GameOf15> parent = new HashMap<GameOf15, GameOf15>(); 		// to look up the parent board of each children board
		HashMap<GameOf15, Integer> level = new HashMap<GameOf15, Integer>();    		// to look up the level/depth corresponding to that board
		final HashMap<GameOf15, Integer> h_score = new HashMap<GameOf15, Integer>();	// to look up the h_score (Hamming distance used as heuristic) corresponding to that board
		Comparator<GameOf15> comp = new Comparator<GameOf15>() {
			@Override
			public int compare(GameOf15 first, GameOf15 second) {
				return h_score.get(first) - h_score.get(second);						// priority is based on the h_score of the board
			}
		};
		PriorityQueue<GameOf15> visit = new PriorityQueue<GameOf15> (10000, comp);		// to visit the board with the moves performed, ranked using the candidates' h_scores

		// add the initial board's values to the data structures just created
		parent.put(game, null);
		level.put(game, 0);
		h_score.put(game, hamming(game));
		visit.add(game);

		// loop until there's no board to visit!
		while (visit.size() > 0) {
			GameOf15 possibleSolution = visit.poll();									// get the most promising board

			if (possibleSolution.hasWon()) {
				LinkedList<GameOf15> end = new LinkedList<GameOf15>();					// to store and then return a list of all the previous board states
				GameOf15 last = possibleSolution;
				while (last != null) {
					end.addFirst(last);
					last = parent.get(last);
				}
				return end;
			}

			// the loop considers all the board states with possible moves
			for (GameOf15 thisBoard: adjacentBoards(possibleSolution)) {
				// add the board's values into the previous data structures
				if (!parent.containsKey(thisBoard)) {
					parent.put(thisBoard, possibleSolution);
					level.put(thisBoard, level.get(possibleSolution) + 1);
					int estimation = hamming(thisBoard);
					h_score.put(thisBoard, level.get(possibleSolution) + 1 + estimation);

					visit.add(thisBoard);
				}
			}
		}

		return null;
	}

	// This method calculates the total Hamming distance (number of misplaced tiles) of the board
	public int hamming(GameOf15 game) {
		int countWrongPos = 0;

		for (int i = 0; i < DIMENSION; i++) {
			for (int j = 0; j < DIMENSION; j++) {
				if (game.getValue(i, j) != SOLVED.getValue(i, j)) { 					// if at any coordinates, the value of this board doesn't match that of the goal
					countWrongPos++;													// then increment the counter variable
				}
			}
		}

		return countWrongPos;
	}

	// This method creates a list of all board states with the moves performed
	private List<GameOf15> adjacentBoards(GameOf15 original) {
		ArrayList<GameOf15> out = new ArrayList<GameOf15>();							// to store the board states with different moves performed

		for(Point move: validMoves(original)) {
			out.add(clone(original, move));												// add the boards with the valid moves performed on the current one
		}

		return out;
	}

	// This method clones the board with the move
	private GameOf15 clone(GameOf15 game, Point p) {
		GameOf15 result = new GameOf15(board2Matrix(game));
		result = move(game, p);
		return result;
	}

	// This method creates a list of valid points that are to be moved
	private List<Point> validMoves(GameOf15 original) {
		blankPos = getBlankPos(original);
		ArrayList<Point> result = new ArrayList<Point>();

		for (int dx = -1; dx < 2; dx++) {												// valid values of dx = {-1, 0, 1}
			for (int dy = -1; dy < 2; dy++) {											// valid values of dy = {-1, 0, 1}
				Point temp = new Point(blankPos.x + dx, blankPos.y + dy);
				if (isValidMove(original, temp)) {
					result.add(temp);													// if the point can perform a valid move then add it to the list
				}
			}
		}

		return result;

	}

	// This method performs the valid moves on the board
	private GameOf15 move(GameOf15 game, Point p) {
		if (!isValidMove(game,p)) {
			throw new RuntimeException("Invalid Move.");
		}
		blankPos = getBlankPos(game);
		board = board2Matrix(game);

		assert board[blankPos.x][blankPos.y] == 0;										// check if the value of the blank tile == 0

		// swap positions between this tile and the blank tile
		board[blankPos.x][blankPos.y] = board[p.x][p.y];
		board[p.x][p.y] = 0;
		blankPos = p;

		return new GameOf15(board);
	}

	// This method checks whether the moves are valid
	public boolean isValidMove(GameOf15 game, Point p) {
		// out-of-bound coordinates are invalid!
		if (( p.x < 0) || (p.x >= DIMENSION)) {
			return false;
		}

		if (( p.y < 0) || (p.y >= DIMENSION)) {
			return false;
		}

		blankPos = getBlankPos(game);
		int dx = blankPos.x - p.x;
		int dy = blankPos.y - p.y;

		if ((Math.abs(dx) + Math.abs(dy) != 1 ) || (dx*dy != 0)) {
			return false;
		} else {
			return true;
		}
	}

	// This method finds the position of the blank tile in a board
	public Point getBlankPos(GameOf15 game) {
		Point blankHere = new Point();

		// loop through the board to find the coordinates of the blank tile
		for (int i = 0; i < DIMENSION; i++) {
			for (int j = 0; j < DIMENSION; j++) {
				if (game.getValue(i, j) == 0) {
					blankHere.x = i;
					blankHere.y = j;
				}
			}
		}

		return blankHere;
	}

	// This method turns the GameOf15 board into a 2D matrix for easier manipulation
	private int[][] board2Matrix(GameOf15 game) {
		int[][] result = new int[DIMENSION][DIMENSION];

		// loop through each position of the array and store the corresponding values in the board
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = game.getValue(i, j);
			}
		}

		return result;
	}

	// This method prints all the board states in the solution list (including the first and final state)
	private void show(List<GameOf15> sol) {
		if (sol != null) {
			System.out.println("Solution with number of moves: " + (sol.size() - 1));
			for (GameOf15 game: sol) {
				System.out.println(game);
			}
		} else {
			System.out.println("Cannot solve.");
		}
	}

}
