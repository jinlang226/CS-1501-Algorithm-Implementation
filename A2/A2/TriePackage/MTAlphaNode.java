/*******************************************************************************
* CS 1501 Summer 2020                                                          * 
*                                                                              * 
* Author: Jinlang Wang                                                         *
* Email: jiw159@pitt.edu                                                       * 
* Description: MultiWay Trie Node implemented as an external class which	   *
* implements the TrieNodeInt InterfaceAddress.  For this				       *
* class it is assumed that all characters in any key will 					   *
* be letters between 'a' and 'z'.                                     		   *  
********************************************************************************
*/

package TriePackage;

import java.util.*;

public class MTAlphaNode<V> implements TrieNodeInt<V>
{
	private static final int R = 26;	// 26 letters in alphabet

    protected V val;
    protected TrieNodeInt<V> [] next;
	protected int degree;

	// You must supply the methods for this class.  See TrieNodeInt.java
	// for the specifications.  See also handout MTNode.java for a
	// partial implementation.  Don't forget to include the conversion
	// constructor (passing a DLBNode<V> as an argument).

	/**
	 * convert DLB node to MTAlphaNode
	 * @param oldNode the old DLB node
	 */
	public MTAlphaNode(DLBNode<V> oldNode) {
		
		degree = oldNode.degree;
		val = oldNode.val;
		
		next = (TrieNodeInt<V>[]) new TrieNodeInt<?>[R];
		
		if(oldNode.front != null) {
			DLBNode<V>.Nodelet curr = oldNode.front;
			char c;
			while(curr != null) {
				c = curr.cval;
				next[c - 97] = curr.child;
				curr = curr.rightSib;
			}
		}
	}
	
	/**
	 * constructor for MTAlphaNode
	 */
	public MTAlphaNode()
	{
		val = null;
		degree = 0;
		next = (TrieNodeInt<V> []) new TrieNodeInt<?>[R];
	}
	
	/**
	 * constructor for MTAlphaNode
	 * @param data value for the node
	 */
	public MTAlphaNode(V data)
	{
		val = data;
		degree = 0;
		next = (TrieNodeInt<V> []) new TrieNodeInt<?>[R];
	}

	@Override
	public TrieNodeInt<V> getNextNode(char c) {
		if(next == null) {
			return null;
		}
		return next[c-97];
	}

	@Override
	public void setNextNode(char c, TrieNodeInt<V> node) {
		if (next[c-97] == null)
			degree++;
		next[c-97] = node;
	}

	@Override
	public V getData() {
		return val;
	}

	@Override
	public void setData(V data) {
		val = data;
	}

	@Override
	public int getDegree() {
		return degree;
	}

	@Override
	public int getSize() { //checked
		int valReference = 4;
		int degreeReference = 4;
		int ArrayNode = 4;
		int nextReference = 4;
		int size = nextReference + valReference + degreeReference + ArrayNode * 26;
		return size;
	}

	@Override
	public Iterable<TrieNodeInt<V>> children() {
		Queue<TrieNodeInt<V>> children = new LinkedList<TrieNodeInt<V>>();
		for(char c = 'a'; c <= 'z'; c++) {
			if(getNextNode(c) != null) {
				children.add(getNextNode(c));
			}
		}
		return children;
	}
}
