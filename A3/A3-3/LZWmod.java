/*************************************************************************
 *  CS 1501 Summer 2020                                                          
 *                                                                               
 *  Author: Jinlang Wang                                                        
 *  Email: jiw159@pitt.edu  
 *  Describtion: improvement over the author's version of LZW
 *
 *  Compress or expand binary input from standard input using LZW.
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;                   // number of input chars
    private static int W = 9;                           // codeword width, start at 9
    private static int L = (int)Math.pow(2, W);         // number of codewords = 2^W

    // Compress without reset
    public static void compress() { 
        BinaryStdOut.write(0, 2);
        
        TrieSTMT<Integer> st = new TrieSTMT<Integer>();

        StringBuilder input = new StringBuilder();

        for (int i = 0; i < R; i++)
        {
            input.append((char) i);
            st.put(input,i);
            input.delete(0, input.length());
        }
        int code = R+1;
        while(!BinaryStdIn.isEmpty()) {
            char character = BinaryStdIn.readChar();
            input.append(character);
            
            if (st.contains(input)== false)
            {                
                input.delete(input.length()-1, input.length());
                BinaryStdOut.write(st.get(input), W);

                if (code == L && W < 16)  // update the data
                {
                    W += 1; 
                    L = (int) Math.pow(2,W);
                }


                if (code < L)     // add to the symbol table          
                    st.put(input.append(character),code++); 
                input.delete(0, input.length());
                input.append(character);
            }
        }

        if (!input.equals(""))
            BinaryStdOut.write(st.get(input), W);
 
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    // Compress with reset
    public static void compressR()
    {
        BinaryStdOut.write(1, 2);
        
        TrieSTMT<Integer> st = new TrieSTMT<Integer>();

        StringBuilder input = new StringBuilder();

        for (int i = 0; i < R; i++)
        {
            input.append((char) i);
            st.put(input,i);
            input.delete(0, input.length());
        }
        int code = R+1;
        while(!BinaryStdIn.isEmpty()) {
            char character = BinaryStdIn.readChar();
            input.append(character);
            
            if (st.contains(input)== false)
            {                
                input.delete(input.length()-1, input.length());
                BinaryStdOut.write(st.get(input), W);

                if (code == L && W < 16)  // update 
                {
                    W += 1; 
                    L = (int) Math.pow(2,W);
                }


                else if(code == L)
                {
                    // reset 
                    W = 9;
                    L = (int)Math.pow(2, W);

                    st = new TrieSTMT<Integer>();

                    for (int i = 0; i < R; i++)
                    {
                        st.put(new StringBuilder("" + (char) i), i);
                    }
                    code = R+1;  
                }


                if (code < L)          
                    st.put(input.append(character),code++); 
                input.delete(0, input.length());
                input.append(character);
            }
        }

        if (!input.equals(""))
            BinaryStdOut.write(st.get(input), W);
 
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }



    /**
     * method to expand
     */
    public static void expand()
    {
        int flag = BinaryStdIn.readInt(2);

        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value

        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                      

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;          
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            if(i == L && W < 16)
            {
                W += 1;
                L = (int)Math.pow(2, W);
            }
            else if(i == L && flag == 1 && W== 16)
            {
                // reset 
                W = 9;
                L = (int)Math.pow(2, W);               

                st = new String[(int)Math.pow(2, 16)];
                for (i = 0; i < R; i++)
                {
                    st[i] = "" + (char) i;
                }
                st[i++] = "";  
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case 
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    /**
    * main method
    * @param args user's input (whether compress or expand)
    */
    public static void main(String[] args) {
        if      (args[0].equals("-") && args[1].equals("n")) compress();    //Do Nothing Mode Compression
        else if (args[0].equals("-") && args[1].equals("r")) compressR();   //Reset Mode 
        
        // expand
        else if (args[0].equals("+"))
        {
            expand();
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}