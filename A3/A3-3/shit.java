/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class shit {
    private static final int R = 256;                   // number of input chars
    private static int W = 9;                           // codeword width, start at 9
    private static int L = (int)Math.pow(2, W);         // number of codewords = 2^W

    // Compress in Do Nothing Mode
    public static void compress() { 
        // write to the first line of the compressed file, so that the expand methods run the right version of expand
        BinaryStdOut.write(0, 2);
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            // if the current code is equal to the maximum amount of codewords that there can be, and the word width is less than 16
            if(code == L && W < 16)
            {
                // incriment word width and recalculate maximum amount of codewords
                W += 1;
                L = (int)Math.pow(2, W);
            }
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W); // writes EOF to the compressed file
        BinaryStdOut.close();
    } 

    // compress in Reset Mode
    public static void compressR()
    {
        // write to the first line of the compressed file, so that the expand methods run the right version of expand
        BinaryStdOut.write(1, 2);
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            // if the maximum amount of codes have been used, and the width of the codes is less than 16
            if(code == L && W < 16)
            {
                // incriment code width and recalculate maximum amount of codewords
                W += 1;
                L = (int)Math.pow(2, W);
            }
            // if the width is already 16 and the current code is at the maximum amount of codes
            else if(code == L)
            {
                // reset the codebook
                W = 9;
                L = (int)Math.pow(2, W);

                st = new TST<Integer>();
                // readd ASCII characters
                for (int i = 0; i < R; i++)
                {
                    st.put("" + (char) i, i);
                }
                code = R+1;  // R is codeword for EO
            }
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W); // writes EOF to the compressed file
        BinaryStdOut.close();
    }

    // compress in Monitored Mode
    public static void compressM()
    {
        // write to the first line of the compressed file, so that the expand methods run the right version of expand
        BinaryStdOut.write(2, 2);
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        // initialize compressed data, uncompressed data, and the old ratio of the datas
        double cData = 0;
        double uData = 0;
        double oldRatio = 0;

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            //incriment uncompressed and compressed data
            uData += t * 8;
            cData += W;
            // if the next code is the final code that can be added
            if(code+1 == L && W == 16)
            {
                // calculate the oold ratio, uncompressed/compressed
                oldRatio = uData/cData;
            }
            // if the maximum codes have been used, and the code width is less than 16
            if(code == L && W < 16)
            {
                // incriment code width and recalculate the maximum code possible
                W += 1;
                L = (int)Math.pow(2, W);
            }
            // else if the code width is already 16 and the code is at the maximum code possible, check if compression needs to be reset
            else if(code == L)
            {
                // new ratio is calculated at each cycle, and is the same math as old ratio
                double newRatio = uData/cData;
                // reset if ratio of ratios exceeds 1.1
                if(oldRatio/newRatio > 1.1)
                {
                    // reset the codebook
                    W = 9;
                    L = (int)Math.pow(2, W);

                    st = new TST<Integer>();
                    // read ASCII characters
                    for (int i = 0; i < R; i++)
                    {
                        st.put("" + (char) i, i);
                    }
                    code = R+1;  // R is codeword for EO
                }
            }
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W); // writes EOF to the compressed file
        BinaryStdOut.close(); 
    }

    // expand in Do Nothing Mode
    public static void expand() {
        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            // if the current iteration is at the maximum code length, and the width is less than 16
            if(i == L && W < 16)
            {
                // incriment code width and recalculate maximum length
                W += 1;
                L = (int)Math.pow(2, W);
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack, ex. STAR STARS
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    // expand in Reset Mode
    public static void expandR()
    {
        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            // if the current iteration is the maximum codeword and the code width is less than 16
            if(i == L && W < 16)
            {
                // incriment code width and recalculate maximum length
                W += 1;
                L = (int)Math.pow(2, W);
            }
            // else if the current iteration is the max length and the width is already 16
            else if(i == L)
            {
                // reset the codebook
                W = 9;
                L = (int)Math.pow(2, W);

                st = new String[(int)Math.pow(2, 16)];
                for (i = 0; i < R; i++)
                {
                    st[i] = "" + (char) i;
                }
                st[i++] = "";                        // (unused) lookahead for EOF

            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack, ex. STAR STARS
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    // expand in Monitored Mode
    public static void expandM()
    {
        // initialize compressed data, uncompressed data, and old ratio of data
        double cData = 0;
        double uData = 0;
        double oldRatio = 0;

        String[] st = new String[(int)Math.pow(2, 16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            // incriment the uncompressed and compressed data
            uData += val.length() * 8;
            cData += W;

            BinaryStdOut.write(val);
            // if the next iteration will be fill the maximum code length, and the width is already 16
            if(i+1 == L && W == 16)
            {
                // calculate the old ratio, uncompressed data/compressed data
                oldRatio = uData/cData;
            }
            // if the current iteration is the max length code, and the code width is less than 16
            if(i == L && W < 16)
            {
                // incriment the code width and recalculate the max length
                W += 1;
                L = (int)Math.pow(2, W);
            }
            // else if the current iteration is the final codeword and the width is already 16, check if it needs reset
            else if(i == L)
            {
                // calculate the new ratio during each iteration, same math as old ratio
                double newRatio = uData/cData;
                // reset if ration of ratios exceeds 1.1
                if(oldRatio/newRatio > 1.1)
                {
                    // reset the codebook
                    W = 9;
                    L = (int)Math.pow(2, W);

                    st = new String[(int)Math.pow(2, 16)];
                    for (i = 0; i < R; i++)
                    {
                        st[i] = "" + (char) i;
                    }
                    st[i++] = "";                        // (unused) lookahead for EOF
                }
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack, ex. STAR STARS
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if      (args[0].equals("-") && args[1].equals("n")) compress();    // compression with n = Do Nothing Mode Compression
        else if (args[0].equals("-") && args[1].equals("r")) compressR();   // compression with r = Reset Mode Compression
        else if (args[0].equals("-") && args[1].equals("m")) compressM();   // compression with m = Monitored Mode Compression
        // if the user indicates expansion mode
        else if (args[0].equals("+"))
        {
            // flag is the first 2 bits of the file converted to an int; 0 for Do Nothing Mode, 1 for Reset Mode, 2 for Monitored Mode
            int flag = BinaryStdIn.readInt(2);
            // if flag is 0, call expand(), expand in Do Nothing Mode
            if(flag == 0)
            {
                expand();
            }
            // if flag is 1, call expandR(), expand in Reset Mode
            else if(flag == 1)
            {
                expandR();
            }
            // if flag is 2, call expandM(), expand in Monitored Mode
            else if(flag == 2)
            {
                expandM();
            }
        }
        // if user did not enter file incorrect format, then throw exception
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}