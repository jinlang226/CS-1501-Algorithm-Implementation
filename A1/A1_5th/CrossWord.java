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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CrossWordNew class is to solve cross word game given a dictionary and different boards
 */
public class CrossWord {
	
	public char[][] board;
	public int dim;

	// Make TrieSTNew object of String, then read strings from file and
	// put each one into the trie.
	TrieSTNew<String> D = new TrieSTNew<String>();
	int sols = 0;

	/* List of all plus positions in the board */
	ArrayList<int[]> PlusLocations = new ArrayList<int[]>();
	

	/**
	 * Entry of the program
	 *
	 */
	public static void main(String[] args) throws Exception {
		if (args.length >= 2) {
			CrossWord run = new CrossWord(args[0], args[1]);
		} else {
			System.out.println("Wrong input");
		}
	}
	
	
	/**
	 * Initialize initPrefix variables based on the board
	 *
	 */
	public CrossWord(String dictionary, String test) throws Exception  {
		
		Scanner fileScan = new Scanner(new FileInputStream(dictionary));
		
		// Make TrieSTNew object of String, then read strings from file and
		// put each one into the trie.
		//TrieSTNew<String> D = new TrieSTNew<String>();
		String st;
		while (fileScan.hasNext()) {
			st = fileScan.nextLine();
		        //if (st.length() == 4 && st.charAt(0) == 's' && st.charAt(1) == 'u' && st.charAt(3) == 'k') System.out.println(st);
			D.put(st, st);
		}
				
		Scanner testFile = new Scanner(new FileInputStream(test));
				
		dim = testFile.nextInt(); 
		
		board = new char[dim][dim];
		
		for(int i = 0; i < dim; i++) {
			String readIn = testFile.next();
			char[] line = readIn.toCharArray();
			for(int j = 0; j < dim; j++) {
				board[i][j] = line[j];
				if(board[i][j] == '+') {
					allPlusLocations(i, j);
				}
			}
		}
		//printBoard() ;
		rec(0);
		System.out.println("There are " + sols + " solutions");
	}
	
	/**
	 * Put all plus locations into an ArrayList.
	 * @param i the row index of the location
	 * @param j the column index of the location
	 */
	public void allPlusLocations(int i, int j) {
		PlusLocations.add(new int[]{i, j});
		//System.out.println("Add " +i+ " and " + j);
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
	private DirectionResult getDirectionStrs(int x, int y) {
		StringBuilder sbX = new StringBuilder(); 
		StringBuilder sbY = new StringBuilder();
		String prefixStrX, suffixStrX;
		String prefixStrY, suffixStrY;
		int tmpX = x-1;
		int tmpY = y-1;
		//check for vertical
		while(tmpX >= 0 && Character.isLetter(board[tmpX][y])) {
			sbX.insert(0, board[tmpX][y]);
			tmpX--;
		}
		prefixStrX = sbX.toString();
		sbX.setLength(0);
		tmpX = x + 1;
		while(tmpX < dim && Character.isLetter(board[tmpX][y])) {
			sbX.append(board[tmpX][y]);
			tmpX++;
		}
		suffixStrX = sbX.toString();

		//check for horizontal
		while(tmpY >= 0 && Character.isLetter(board[x][tmpY])) {
			sbY.insert(0, board[x][tmpY]);
			tmpY--;
		}
		prefixStrY = sbY.toString();
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
	public void rec(int index) throws IOException {
		if (index == PlusLocations.size()) {
			sols++;
			//if(sols % 1000 == 0)
			System.out.println(sols + ": ");
			printBoard();
			return;
		}
		int x = PlusLocations.get(index)[0];
		int y = PlusLocations.get(index)[1];

		DirectionResult directionRes = getDirectionStrs(x, y);

		char c = ' ';
		for(c = 'a'; c <= 'z'; ++c)  {
			
			//check if c is valid
		    //System.out.println("Check for " +x+ " and " + y + " with " + c);
			if(valid(c, x, y, directionRes)) {
				//if x, y is the last '+'
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
		//System.out.println("Get strX " + strX + "with x " + x);
		if (lx + 1 < dim) {
			if (board[lx+1][y] == '-') {
				//System.out.println("!!is lock");
				if (D.searchPrefix(strX) != 2 && D.searchPrefix(strX) != 3) {
					//System.out.println("!!159");
					return false;
				}
			} else {
				if (D.searchPrefix(strX) != 1 && D.searchPrefix(strX) != 3) {
					//System.out.println("!!164");
					return false;
				}
			}
		} else {
			int rt = D.searchPrefix(strX);
			//System.out.println("Get strX " + strX +9 "with " + rt);
			if (D.searchPrefix(strX) != 2 && D.searchPrefix(strX) != 3) {
				//System.out.println("!!173");
				return false;
			}
		}

		//System.out.println("Get strY " + strY + "with y" + y);
		if (ly + 1 < dim) {
			if (board[x][ly+1] == '-') {
				if (D.searchPrefix(strY) != 2 && D.searchPrefix(strY) != 3) {
					//System.out.println("!!181");
					return false;
				}
			} else {
				if (D.searchPrefix(strY) != 1 && D.searchPrefix(strY) != 3) {
					//System.out.println("!!186");
					return false;
				}
			}
		} else {
			int rt = D.searchPrefix(strY);
                        //System.out.println("Get strY " + strY + "with " + rt);
			if (D.searchPrefix(strY) != 2 && D.searchPrefix(strY) != 3) {
				//System.out.println("!!194");
				return false;
			}
		}
		
		//System.out.println("Get strX " + strX + " and strY "+strY+" with x " + x + " and y " + y);
		//System.out.println("END valid=============");
		return true;
	}
	
	/**
	 * Method to print the board
	 */
	public void printBoard() throws IOException {
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
		String message = "";
		for(int i = 0; i < arr.length; i++) {
			System.out.print(arr[i]);
		}
		System.out.println("\n");
	}
	
}