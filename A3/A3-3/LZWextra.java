/*************************************************************************
 *  CS 1501 Summer 2020                                                          
 *                                                                               
 *  Author: Jinlang Wang                                                        
 *  Email: jiw159@pitt.edu  
 *  Describtion: extra credit for LZW
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
import java.lang.StringBuilder;
import java.util.Arrays;


public class LZWextra {

    private static final int R = 256;        // number of input chars
    private static int W = 9;         // codeword width
    private static int L = (int) Math.pow(2,W);       // number of codewords = 2^W
    private static int numByte = 0;
    private static int numCompress = 0; 
    private static final double RATIO = 0.9;

     /**
     * main method
     * @param args user's input (whether compress or expand)
     */
    public static void main(String[] args) 
    {
    	if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");            
    }


    /**
     * method to compress 
     * @param flag whether reset the dictionary or not 
     */
    public static void compress()
    {
        TrieSTMT<Integer> st = new TrieSTMT<Integer>();
        for (int i = 0; i < R; i++) // Fill the Trie up
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        StringBuilder input = new StringBuilder(); 
        char lastChar =' '; //the last character 
        

        while(!BinaryStdIn.isEmpty())
        {
            lastChar =  BinaryStdIn.readChar();
            numByte+=1;
            input.append(lastChar);
            if(st.contains(input)== false)
            {
                StringBuilder original = new StringBuilder(input.toString());
                BinaryStdOut.write(st.get(input.deleteCharAt(input.length()-1)),W);
                numCompress += W;
                if(code+2 < L) st.put(original,code++);

                if(code+1 == L && ((double) numByte/ (double) numCompress) < RATIO) 
                {
                	TrieSTMT<Integer> temp = new TrieSTMT<Integer>();
                    for (int i = 0; i < R; i++)
                    	temp.put(new StringBuilder("" + (char) i), i);
                    st = temp; // Reset the trie
                    code = 257;
                }

                else if(code+1 ==L && W < 12)
                {
                    W+=1; // update values
                    L = (int) Math.pow(2,W);
                }

                //reset
                if(W == 16 && code+1 == L)
                {
                    TrieSTMT<Integer> temp = new TrieSTMT<Integer>();
                    for (int i = 0; i < R; i++)
                    	temp.put(new StringBuilder("" + (char) i), i);
                    st = temp; // Reset the trie
                    code = 257; 
                  
                }
                input = new StringBuilder(""+lastChar);
            }
        }
        BinaryStdOut.write(st.get(input),W);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }

    /**
     * method to expand
     */
    public static void expand() {
    	
        String[] st = new String[L];
        int i; // next available codeword value

        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";    
        
        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
        
        
        while (true) {
            BinaryStdOut.write(val);
            numByte+=(val.length() * 8);
            numCompress +=W;
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R)  break; 
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   
            if (i+2 < L) st[i++] = val + s.charAt(0);

            if(i+1 == L && ((double) (numByte/numCompress)) < RATIO) 
            { //reset
            	String[] newST = new String[L];
                int j;
                for (j = 0; j < R; j++)
                {    
                    newST[j] = "" + (char) j;
                }
                newST[j++] = "";
                st = newST;
                i = 257;
            }

            if(i+1 == L && W < 17)
            {
            	W+=1; 
                L = (int) Math.pow(2,W);
                st = Arrays.copyOf(st, L);
            }
            
            if(W == 16 && i+1 == L)
            {
            	String[] newST = new String[L];
                    int j;
                    for (j = 0; j < R; j++)
                    {    
                        newST[j] = "" + (char) j;
                    }
                    newST[j++] = "";

                    st = newST;

                    codeword = BinaryStdIn.readInt(W);
					s = st[codeword];

                    i = 257;
                    W = 9;
                    L = (int) Math.pow(2,W);
            }
            val = s;
        }
        BinaryStdOut.close();
    }
}
