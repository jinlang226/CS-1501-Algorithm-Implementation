/*******************************************************************************
* CS 1501 Summer 2020                                                          * 
*                                                                              * 
* Author: Jinlang Wang                                                         *
* Email: jiw159@pitt.edu                                                       * 
* Description: Hybrid trie using TrieNodeInt<V> interface  					   *
********************************************************************************
*/

package TriePackage;

import java.util.*;

import java.io.*;

public class HybridTrieST<V> {

    private TrieNodeInt<V> root;
    int treeType = 0;
    // treeType = 0 --> multiway trie
    // treeType = 1 --> DLB
    // treeType = 2 --> hybrid

	// You must supply the methods for this class.  See test program
	// HybridTrieTest.java for details on the methods and their
	// functionality.  Also see handout TrieSTMT.java for a partial
	// implementation.
    
    /**
     * constructor for hybrid trie
     */
    public HybridTrieST()
	{
		root = null;
	}
    
    /**
     * constructor for hybrid trie
     * @param t tree type
     */
    public HybridTrieST(int t) {
    	treeType = t;
    	if(t == 0) {
    		root = new MTAlphaNode<V>();
    	} else {
    		root = new DLBNode<V>();
    	}
    }
    
     /**
      * Is the key in the symbol table
      * @param key
      * @return boolean value that is the key in the seymbol table
      */
     public boolean contains(String key) {
         return get(key) != null;
     }

     /**
      * get the data
      * @param key
      * @return the data
      */
     public V get(String key) {
         TrieNodeInt<V> x = get(root, key, 0);
         if (x == null) return null;
         return x.getData();
     }

     /**
      * get the data
      * @param x TrieNodeInt
      * @param key string
      * @param d the position of char
      * @return TrieNodeInt
      */
     private TrieNodeInt<V> get(TrieNodeInt<V> x, String key, int d) {
         if (x == null) return null;
         if (d == key.length()) return x;
         char c = key.charAt(d);
         return get(x.getNextNode(c), key, d+1);
     }
 	
    /**
     * Compare to searchPrefix in TrieSTNew.  Note that in this class all
     * of the accesses to a node are via the interface methods.
     * @param key the string
     * @return the prefix
     */
 	public int searchPrefix(String key)
 	{
 		int ans = 0;
 		TrieNodeInt<V> curr = root;
 		boolean done = false;
 		int loc = 0;
 		while (curr != null && !done)
 		{
 			if (loc == key.length())
 			{
 				if (curr.getData() != null)
 				{
 					ans += 2;
 				}
 				if (curr.getDegree() > 0)
 				{
 					ans += 1;
 				}
 				done = true;
 			}
 			else
 			{
 				curr = curr.getNextNode(key.charAt(loc));
 				loc++;
 			}
 		}
 		return ans;
 	}
 		
    /**
     * Insert key-value pair into the symbol table.
     * @param key string
     * @param val the value
     */
 	public void put(String key, V val) {
         root = put(root, key, val, 0);
     }

 
 	/**
 	 * This method requires us to create a new node -- which in turn requires
 	 * a class.  This is the only place where a MTNode<V> object is explicitly used.
 	 * @param x TrieNodeInt 
 	 * @param key the string
 	 * @param val the value
 	 * @param d the position of char
 	 * @return
 	 */
     private TrieNodeInt<V> put(TrieNodeInt<V> x, String key, V val, int d) {
         if (x == null && treeType == 0) {
        	 x = new MTAlphaNode<V>();
         }
         if(x == null && treeType == 1) {
        	 x = new DLBNode<V>();
         }
         if(x == null && treeType == 2) {
        	 x = new DLBNode<V>();
         }
         
         if (d == key.length()) {
             x.setData(val);
             return x;
         }
         char c = key.charAt(d);
         x.setNextNode(c, put(x.getNextNode(c), key, val, d+1));
         
         if (x != null && treeType == 2 && x.getDegree() >10 && x instanceof DLBNode){
        	 x = new MTAlphaNode((DLBNode) x); 
         }
         
         return x;
     }
    
    //MTAlphaNode<?> nodes below should be equal to the sum of the 
	// distribution value from 11 to 26, while the number of DLBNode<?> nodes below
	// should be equal to the sum of the distribution value from 0 to 10.
     
   //============================
   /**
    * Count the number of nodes of a given type
    * value 1 to indicate MTAlphaNode<?> nodes and 
    * the value 2 to indicated DLBNode<?> nodes
    * traverse all of the nodes of the trie and use
	* the instanceof operator to test the types of the nodes
    * @param type type of node
    * @return number of nodes
    */
	public int countNodes(int type) {
		
		if(type == 1) { //mtalpha
			return countNodesHelper(root, 1);
		} 
		
		if(type == 2) { //DLBNode
			return countNodesHelper(root, 2);
		}
		return 0;
	}
	
	/**
	 * helper method 
	 * @param node TrieNodeInt
	 * @param type type of node
	 * @return number of nodes
	 */
	public int countNodesHelper(TrieNodeInt<V> node, int type) {
		if(type == 1) {
			if(node instanceof MTAlphaNode) {
				int count = 1;
				for(TrieNodeInt<V> child : node.children()) {
					count += countNodesHelper(child, type);
				}
				return count;
			}
			if(node instanceof DLBNode) {
				int count = 0;
				for(TrieNodeInt<V> child : node.children()) {
					count += countNodesHelper(child, type);
				}
				return count;
			}
		}
		
		if(type == 2) {
			if(node instanceof DLBNode) {
				int count = 1;
				for(TrieNodeInt<V> child : node.children()) {
					count += countNodesHelper(child, type);
				}
				return count;
			}
			if(node instanceof MTAlphaNode) {
				int count = 0;
				for(TrieNodeInt<V> child : node.children()) {
					count += countNodesHelper(child, type);
				}
				return count;
			}
		}
		return 0;
	}

	/**
	 * Save the trie in order back to args[1].  This method will traverse
	 * through all of the values in the trie in alpha order, saving all of them
	 * to the file name provided in args[1].
	 * @param file file name
	 * @throws FileNotFoundException 
	 */
	 public void save(String file) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(file);
		saveHelper(out, root);
		out.close();
	 }
	 
	 public void saveHelper(PrintWriter out, TrieNodeInt<V> node) {
		 if(node.getData() != null) {
				out.println(node.getData());
			}
		 for(TrieNodeInt<V> child : node.children()) {
			saveHelper(out, child);
		 }
	 }
	 
	 /**
	 * The value of each location dist[i] will
	 * be equal to the number of nodes with degree i in the trie. 
	 * @return return an int [] of size K+1
	 * 	where K is the maximum possible degree of a node in
	 *  the trie), indexed from 0 to K. 
	 */
	 public int[] degreeDistribution() {
	 	 int[] degreeDis = new int[27];
	 	 for(int i = 0; i < 27; i++) {
	 		degreeDis[i] = degreeDistributionHelper(root, i);
	 	 }
	 	 return degreeDis;
	 }
	 
	 /**
	  * helper method
	  * @param node TrieNodeInt
	  * @param para the degree
	  * @return
	  */
	 public int degreeDistributionHelper(TrieNodeInt<V> node, int para) {
		 int degree = 0;
		 if(node.getDegree() == para) {
			 degree++;
		 }
		 for(TrieNodeInt<V> child : node.children()) {
			 degree += degreeDistributionHelper(child, para);
		 }
		 return degree;
	 }

	 /**
	 * get the size in byte for the trie
	 * @return the number of byte
	 */
	 public int getSize() {
	 	 return getSizeHelper(root);
	 }
	
	/**
	 * helper method
	 * @param node
	 * @return the number of byte
	 */
	 public int getSizeHelper(TrieNodeInt<V> node) {
		 int sum = 0;
		 sum += node.getSize();
		 for(TrieNodeInt<V> child : node.children()) {
			 sum += getSizeHelper(child);
		 }
		 return sum;
	 }
}

