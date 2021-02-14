/*******************************************************************************
* CS 1501 Summer 2020                                                          * 
*                                                                              * 
* Author: Jinlang Wang                                                         *
* Email: jiw159@pitt.edu                                                       * 
* Description: DLB Trie Node implemented as an external class which			   *
* implements the TrieNodeInt<V> Interface                                      *  
********************************************************************************
*/

package TriePackage;

import java.util.*;

// de la Briandais tree node
public class DLBNode<V> implements TrieNodeInt<V>
{
    protected Nodelet front;
    protected int degree;
	protected V val;
	
	/**
	 * constructor for Nodelet
	 */
    protected class Nodelet
    {
    	protected char cval;
    	protected Nodelet rightSib;
    	protected TrieNodeInt<V> child;
    	
    	protected Nodelet(char c) {
    		cval = c;
    		rightSib = null;
    		child = null;
    	}
    }
    
    /**
     * constructor
     * @param val
     */
    public DLBNode() {
    	val = null;
    	degree = 0; 
    	val = null;
    }
    
    /**
     * constructor
     * @param val
     */
    public DLBNode(V val) {
    	front = null;
    	degree = 0;
    	val = val;
    }
    
	@Override
	//a traversal of the linked-list of nodelets 
	//until either the character is found or the end of the list is reached
	public TrieNodeInt<V> getNextNode(char c) {
		
		if(front == null) {
			return null;
		}
		
		Nodelet temp = front;
	
		while(temp != null) {
			if(temp.cval == c) {
				return temp.child;
			}
			temp = temp.rightSib;
		}
		return null;
	}

	@Override
	//make sure that the new nodelet is placed into 
	//the proper position within the linked list based on the character values
	public void setNextNode(char c, TrieNodeInt<V> node) {
		
		Nodelet nextNode = new Nodelet(c);
		Nodelet temp = front;
		
		if(front == null) {
			front = nextNode;
			front.child = node;
			degree++;
			return;
		}
		
		else if(front.cval == c) {
			if(front.child == null) {
				degree++;
			}
			front.child = node;
			return;
		}
		
		else if(front.cval > c) {
			nextNode.rightSib = temp;
			front = nextNode;
			front.child = node;
			degree++;
			return;
		}
		
		else if(front.cval < c) {
			while(temp!= null && temp.rightSib != null && temp.rightSib.cval < c) {
				temp = temp.rightSib;
			}
			
			if(temp.rightSib != null && temp.rightSib.cval == c) {
				if(temp.rightSib.child == null) {
					degree++;
				}
				temp.rightSib.child = node;
				return;
			}
			
			nextNode.rightSib = temp.rightSib;
			temp.rightSib = nextNode;
			degree++;
			nextNode.child = node;
		}
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
	public int getSize() {
		int frontRef = 4;
		int degreeRef = 4;
		int valRef = 4;
		
		int cvalRef = 2;
		int rightSibRef = 4;
		int childRef = 4;
		
		int childTotalRef = cvalRef + rightSibRef + childRef;
		
		int size = frontRef + degreeRef + valRef + childTotalRef * degree;
		
		return size;
	}

	@Override
	public Iterable<TrieNodeInt<V>> children() {
		Queue<TrieNodeInt<V>> children = new LinkedList<TrieNodeInt<V>>();
		Nodelet curr = front;
		while(curr != null) {
			if(curr.child != null)
				children.add(curr.child);
			curr = curr.rightSib;
		}
		return children;
	}
}
