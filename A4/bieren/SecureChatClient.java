import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {
  public static final int PORT = 8765;

 ObjectInputStream myReader;
 ObjectOutputStream myWriter;
 JTextArea outputArea;
 JLabel prompt;
 JTextField inputField;
 String myName, serverName;
 Socket connection;
 String encType;
 BigInteger bigKey;
 
 //private String StringE, StringD, StringN;
 private BigInteger E, D, N;
 Random R;
 SymCipher cipher;

 public SecureChatClient ()
 {
  try {

  myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
  serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
  InetAddress addr =
    InetAddress.getByName(serverName);
  connection = new Socket(addr, PORT);   // Connect to server with new
              // Socket
  myWriter =
      new ObjectOutputStream(connection.getOutputStream());
  
  myWriter.flush();
  
  myReader =
     new ObjectInputStream(
      connection.getInputStream());   // Get Reader and Writer

  E= (BigInteger) myReader.readObject();
  N= (BigInteger) myReader.readObject();
  encType = (String) myReader.readObject();
  System.out.println("E: "+E);
  System.out.println("N: "+N);
  System.out.println("encoding type: "+ encType);
  
  if(encType.equals("Sub"))
  {
    cipher= new Substitute();
  }
  if(encType.equals("Add"))
  {
    cipher= new Add128();
  }
  
  byte[] byteKey= cipher.getKey();
  bigKey= new BigInteger(1,byteKey);
  System.out.println("BigKey: "+ bigKey);
  
  BigInteger keyEnc= bigKey.modPow(E,N);
  myWriter.writeObject(keyEnc);
  myWriter.flush();
  
  //encoding proceedure and printing
  System.out.println("Encryption");
  System.out.println("Original String: "+ myName);
  byte [] tempEnc= cipher.encode(myName);
  BigInteger arrayRep = new BigInteger(1, myName.getBytes());
  System.out.println("BigInteger representation of array: "+arrayRep);
  arrayRep= new BigInteger(1, tempEnc);
  System.out.println("BigInteger representation of encrypted array: " + arrayRep);
  myWriter.writeObject(tempEnc);
  myWriter.flush();
  ///////// Send name to Server.  Server will need
         // this to announce sign-on and sign-off
         // of clients

  this.setTitle(myName);   // Set title to identify chatter

  Box b = Box.createHorizontalBox();  // Set up graphical environment for
  outputArea = new JTextArea(8, 30);  // user
  outputArea.setEditable(false);
  b.add(new JScrollPane(outputArea));

  outputArea.append("Welcome to the Chat Group, " + myName + "\n");

  inputField = new JTextField("");  // This is where user will type input
  inputField.addActionListener(this);

  prompt = new JLabel("Type your messages below:");
  Container c = getContentPane();

  c.add(b, BorderLayout.NORTH);
  c.add(prompt, BorderLayout.CENTER);
  c.add(inputField, BorderLayout.SOUTH);

  Thread outputThread = new Thread(this);  // Thread is to receive strings
  outputThread.start();     // from Server

  addWindowListener(
    new WindowAdapter()
    {
     public void windowClosing(WindowEvent e)
     { 
       try{
         //encoding proceedure and printing
         String closingM="CLIENT CLOSING"; 
         
         System.out.println("Encryption");
         System.out.println("Original String: "+ closingM);
         byte [] tempEnc= cipher.encode(closingM);
         BigInteger arrayRep = new BigInteger(1, closingM.getBytes());
         System.out.println("BigInteger representation of array: "+arrayRep);
         arrayRep= new BigInteger(1, tempEnc);
         System.out.println("BigInteger representation of encrypted array: " + arrayRep);
         myWriter.writeObject(tempEnc);
         myWriter.flush();
       
         System.exit(0);
       }
       catch(Exception error)
       {
         System.out.println("Error disconnecting from server");
       }
      }
    }
   );

  setSize(500, 200);
  setVisible(true);

  }
  catch (Exception e)
  {
   System.out.println("Problem starting client!");
  }
 }

 public void run()
 {
  while (true)
  {
    try {
    
    //decoding proceedure and printing 
    byte[] encMsg=(byte[]) myReader.readObject();
    String currMsg= cipher.decode(encMsg);
    System.out.println("Dencryption");
    BigInteger arrayRep = new BigInteger(1, encMsg);
    System.out.println("BigInteger representation of recieved array: "+arrayRep);
    arrayRep= new BigInteger(1, currMsg.getBytes());
    System.out.println("BigInteger representation of dencrypted array: " + arrayRep);
    System.out.println("Dencrypted message: " + currMsg);
    //////
    outputArea.append(currMsg+"\n");
    }
    catch (Exception e)
    {
    System.out.println(e +  ", closing client!");
    break;
    }
  }
  System.exit(0);
 }

 public void actionPerformed(ActionEvent e)
 {
   try{
     String currMsg = e.getActionCommand();   // Get input value
     inputField.setText("");
     
     String send= myName + ":" + currMsg;
     //encoding proceedure and printing
     System.out.println("Encryption");
     System.out.println("Original String: "+ send);
     byte [] tempEnc= cipher.encode(send);
     BigInteger arrayRep = new BigInteger(1, send.getBytes());
     System.out.println("BigInteger representation of array: "+arrayRep);
     arrayRep= new BigInteger(1, tempEnc);
     System.out.println("BigInteger representation of encrypted array: " + arrayRep);
     myWriter.writeObject(tempEnc);
     myWriter.flush();
   }
   catch(Exception error)
   {
     System.out.println("Problem sending message"); 
   }
// Add name and send it
 }             // to Server

 public static void main(String [] args)
 {
   SecureChatClient JR = new SecureChatClient();
   JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 }
}