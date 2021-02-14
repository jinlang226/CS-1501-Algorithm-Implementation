//package assignment1;

/*******************************************************************************
* CS 1501 Summer 2020                                                          * 
*                                                                              * 
* Author: Jinlang Wang                                                         *
* Email: jiw159@pitt.edu                                                       * 
* Description: Solve cross word game given a dictionary and different boards.  *  
* Execution from command line w/ specified input and output files              * 
* as well as * specified data structure.                                       *  
******************************************************************************
 */


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CrossWordNew class is to solve cross word game given a dictionary and different boards
 */
public class CrossWordNew {
	
	public char[][] board;
	public int dim;
	

	// Make TrieSTNew object of String, then read strings from file and
	// put each one into the trie.
	TrieSTNew<String> D = new TrieSTNew<String>();
	int sols = 0;
	
	/* List of all plus positions in the board */
	ArrayList<int[]> PlusLocations = new ArrayList<int[]>();
	
	/* Array to do the memoization in the recursive process */
	String[][] prefixXMemo;
	String[][] prefixYMemo;
	
	/* Array to store existing prefix in the board */
	// include the character itself in initPrefix
	String[][] initPrefixX;
	String[][] initPrefixY;
	
	/**
	 * Entry of the program
	 *
	 */
	public static void main(String[] args) throws Exception {
		if (args.length >= 2) {
			CrossWordNew run = new CrossWordNew(args[0], args[1]);
		} else {
			System.out.println("Wrong input");
		}
	}


	/**
	 * Initialize initPrefix variables based on the board
	 *
	 */
	private void initializePrefix() {
		initPrefixX = new String[dim][dim];
		initPrefixY = new String[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				if (Character.isLetter(board[i][j])) {
					if (i == 0) {
						initPrefixX[i][j] = "" + board[i][j];
					} else {
						initPrefixX[i][j] = initPrefixX[i-1][j] + board[i][j];
					}
					if (j == 0) {
						initPrefixY[i][j] = "" + board[i][j];
					} else {
						initPrefixY[i][j] = initPrefixY[i][j-1] + board[i][j];
					}
				} else {
					initPrefixX[i][j] = "";
					initPrefixY[i][j] = "";
				}
			}
		}
	}

	/**
	 * Generate the Cross Word puzzle
	 */
	public CrossWordNew(String dictionary, String test) throws Exception  {
		
		//read in the test file
		Scanner testFile = new Scanner(new FileInputStream(test));
				
		dim = testFile.nextInt();
		prefixXMemo = new String[dim][dim];
		prefixYMemo = new String[dim][dim];
		
		board = new char[dim][dim];
		
		for(int i = 0; i < dim; i++) {
			String readIn = testFile.next();
			char[] line = readIn.toCharArray();
			for(int j = 0; j < dim; j++) {
				board[i][j] = line[j];
				if(board[i][j] == '+') {
					//store all plus locations in to arraylist
					allPlusLocations(i, j);
				}
			}
		}

		
		//read in the dictionary
		Scanner fileScan = new Scanner(new FileInputStream(dictionary));
				
		String st;
		
		if(dim == 7 && PlusLocations.size() == 49) {
			while (fileScan.hasNext()) {
				st = fileScan.nextLine();
				if(st.length() == 7 ) {
					D.put(st, st);
				}
			}
		}
		
		else if (dim == 8 && PlusLocations.size() == 64) {
			while (fileScan.hasNext()) {
				st = fileScan.nextLine();
				if(st.length() == 8 ) {
					D.put(st, st);
				}
			}
		}
		
		else {
			while (fileScan.hasNext()) {
				st = fileScan.nextLine();
				D.put(st, st);
			}
		}
		
		
		initializePrefix(); // Initialize initPrefix variables based on the board
		long preTime = System.currentTimeMillis();
		
		rec(0); //call the recursive method
		
		System.out.println("There are " + sols + " solutions");
		long delta = (System.currentTimeMillis() - preTime) / 1000;
		System.out.println("Time elapse " + delta + " seconds");
	}
	
	/**
	 * Put all plus locations into an ArrayList.
	 * @param i the row index of the location
	 * @param j the column index of the location
	 */
	private void allPlusLocations(int i, int j) {
		PlusLocations.add(new int[]{i, j});
	}
	
	/**
	 * DirectionResult class is to check the vertical and horizontal condition
	 * for the puzzle
	 */
	class DirectionResult {
		int largestX;
		int largestY;
		String prefixStrX;
		String suffixStrX;
		String prefixStrY;
		String suffixStrY;
		DirectionResult(int largestX, int largestY, String prefixStrX, String suffixStrX,
			String prefixStrY, String suffixStrY) {
			this.largestX = largestX;
			this.largestY = largestY;
			this.prefixStrX = prefixStrX;
			this.suffixStrX = suffixStrX;
			this.prefixStrY = prefixStrY;
			this.suffixStrY = suffixStrY;
		}
		void printSelf() {
			System.out.println(prefixStrX+"@"+suffixStrX+"@"+prefixStrY+"@"+suffixStrY+"@");
			System.out.println(largestX+"@"+largestY);
		}
	}

	/**
	 * Get the current vertical and horizontal string based on the location
	 * and memorize the related prefix and suffix in helper class
	 *
	 * @param x the row index of the location
	 * @param y the column index of the location
	 * @return the result stored in DirectionResult object
	 */
	@SuppressWarnings("unchecked")
	private DirectionResult getDirectionStrs(int x, int y) {
		StringBuilder sbX = new StringBuilder(); 
		StringBuilder sbY = new StringBuilder();
		String prefixStrX, suffixStrX;
		String prefixStrY, suffixStrY;
		int tmpX = x-1;
		int tmpY = y-1;

		//check for vertical
		/*
		while(tmpX >= 0 && Character.isLetter(board[tmpX][y])) {
			sbX.insert(0, board[tmpX][y]);
			tmpX--;
		}
		*/
		//same as the while loop above, traverse the previous letter for vertical
		if (tmpX < 0 || board[tmpX][y] == '-') {
			prefixXMemo[x][y] = "";
		} else {
			if (initPrefixX[tmpX][y].length() != 0) {
				int p = tmpX - initPrefixX[tmpX][y].length();
				if (p >= 0 && prefixXMemo[p][y] != null) {
					prefixXMemo[x][y] = prefixXMemo[p][y] + board[p][y] + initPrefixX[tmpX][y];
					
				} else {
					prefixXMemo[x][y] = initPrefixX[tmpX][y];
				}
			} else {
				prefixXMemo[x][y] = prefixXMemo[tmpX][y] + board[tmpX][y];
			}
			
		}
		
		prefixStrX = prefixXMemo[x][y];
		//prefixStrX = sbX.toString();
		sbX.setLength(0);
		tmpX = x + 1;

		//traverse the suffix letter for vertical
		while(tmpX < dim && Character.isLetter(board[tmpX][y])) {
			sbX.append(board[tmpX][y]);
			tmpX++;
		}
		suffixStrX = sbX.toString();

		//check for horizontal
		/*
		while(tmpY >= 0 && Character.isLetter(board[x][tmpY])) {
			sbY.insert(0, board[x][tmpY]);
			tmpY--;
		}
		*/
		//same as the while loop above, traverse the previous letter for horizontal
		if (tmpY < 0 || board[x][tmpY] == '-') {
			prefixYMemo[x][y] = "";
		} else {
			if (initPrefixY[x][tmpY].length() != 0) {
				int p = tmpY - initPrefixY[x][tmpY].length();
				if (p >= 0 && prefixYMemo[x][p] != null) {
					prefixYMemo[x][y] = prefixYMemo[x][p] + board[x][p] + initPrefixY[x][tmpY];
					
				} else {
					prefixYMemo[x][y] = initPrefixY[x][tmpY];
				}
			} else {
				prefixYMemo[x][y] = prefixYMemo[x][tmpY] + board[x][tmpY];
			}
			
		}
		prefixStrY = prefixYMemo[x][y];
		
		//traverse the suffix letter 
		sbY.setLength(0);
		tmpY = y + 1;
		while(tmpY < dim && Character.isLetter(board[x][tmpY])) {
			sbY.append(board[x][tmpY]);
			tmpY++;
		}
		suffixStrY = sbY.toString();
		return new DirectionResult(tmpX-1, tmpY-1, prefixStrX, suffixStrX, prefixStrY, suffixStrY);
	}
	

	/**
	 * Recursive method to search the solution in the search space.
	 *
	 * @param index the index of the current plus location in the entire list
	 */
	public void rec(int index) {
		//if x, y is the last '+'
		if (index == PlusLocations.size()) {
			sols++;
			if(sols % 10000 == 0) {
				System.out.println(sols + ": ");
				printBoard();
				System.out.println();
			}
			return;
		}

		//get the plus location (x, y)
		int x = PlusLocations.get(index)[0];
		int y = PlusLocations.get(index)[1];

		//get the string to check (prefix and suffix letters of char c)
		DirectionResult directionRes = getDirectionStrs(x, y);

		char c = ' ';
		for(c = 'a'; c <= 'z'; ++c)  {
			//check if c is valid
			if(valid(c, x, y, directionRes)) {
				board[x][y] = c;
				rec(index+1);
				board[x][y] = '+';
			}
		}
	}


	/**
	 * Method to check if the placement of characer in (x, y) is valid
	 * the string is either prefix and the next cell is not blanked, or 
	 * word and the next cell is blanked
	 * 
	 * @param character the character for the location
	 * @param x the row index
	 * @param y the column index
	 * @param directionRes related information
	 * @return result for judgment
	 */
	public boolean valid(char character, int x, int y, DirectionResult directionRes) {
		String strX = directionRes.prefixStrX + character + directionRes.suffixStrX;
		String strY = directionRes.prefixStrY + character + directionRes.suffixStrY;

		int lx = directionRes.largestX;
		int ly = directionRes.largestY;

		int strXRes = D.searchPrefix(strX);
		int strYRes = D.searchPrefix(strY);

		//check if the string is found in the dictionary
		if(strXRes == 0 || strYRes == 0) {
			return false;
		}

		//check for vertical 
		if (lx + 1 < dim) {
			if (board[lx+1][y] == '-') {
				if (strXRes != 2 && strXRes != 3) {
					return false;
				}
			} else {
				if (strXRes != 1 && strXRes != 3) {
					return false;
				}
			}
		} else {
			if (strXRes != 2 && strXRes != 3) {
				return false;
			}
		}

		//check for horizontal
		if (ly + 1 < dim) {
			if (board[x][ly+1] == '-') {
				if (strYRes != 2 && strYRes != 3) {
					return false;
				}
			} else {
				if (strYRes != 1 && strYRes != 3) {
					return false;
				}
			}
		} else {
			if (strYRes != 2 && strYRes != 3) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Method to print the board
	 */
	public void printBoard() {
		char[] arr = new char[dim * dim + dim];
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				if(board[i][j] == '-') {
					arr[i*dim+i+j] = '-';
				} else {
					if('a' <= board[i][j] && board[i][j] <= 'z') {
						arr[i*dim+i+j] = board[i][j];
					}
				}
			}
			arr[i*dim+i+dim] = '\n';
		}
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i] + "");
		}
	}
}
