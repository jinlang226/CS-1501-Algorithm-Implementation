/*************************************************************************
 *  CS 1501 Summer 2020                                                          
 *                                                                               
 *  Author: Jinlang Wang                                                        
 *  Email: jiw159@pitt.edu  
 *  Describtion: improvement over the author's version of LZW
 *
 *  
* The purpose of this programming assignment is to implement a primitive 
* secure communications system utilizing the RSA cryptosystem 
* and two primitive symmetric ciphers – a substitution cipher and an additive cipher.
 *************************************************************************/

import java.util.*;
import java.io.*;
import java.math.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    // CHANGE THIS PORT to 8765 for SecureChatClient
 public static final int PORT = 8765;

 ObjectInputStream myReader;
 ObjectOutputStream myWriter;
 JTextArea outputArea;
 JLabel prompt;
 JTextField inputField;
 String myName, serverName;
 Socket connection;
 SymCipher cipher;
 BigInteger symmetricKey;
 
 private BigInteger E, D, N;
 
 
 public SecureChatClient()
 {
     try {

     myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
     serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
     InetAddress addr =
             InetAddress.getByName(serverName);
     connection = new Socket(addr, PORT);   // Connect to server with new
                                            // Socket
     
     //creates an ObjectOutputStream on the socket (for writing) 
     myWriter = new ObjectOutputStream(connection.getOutputStream());
     myWriter.flush();
     
     
     //creates on ObjectInputStream on the socket
     myReader = new ObjectInputStream( connection.getInputStream());   // Get Reader and Writer

     E = (BigInteger) myReader.readObject();
     N = (BigInteger) myReader.readObject();
     String encryptionType = (String) myReader.readObject();
     System.out.println("E: " + E);
     System.out.println("N: " + N);
     System.out.println("symmetric cipher type: "+ encryptionType);

     if(encryptionType.equals("Add")) cipher = new Add128();
     if(encryptionType.equals("Sub")) cipher = new Substitute();
    
//     // Send the encrypted key down the stream
//     myWriter.writeObject(new BigInteger(1, cipher.getKey()).modPow(E,N));
//     myWriter.flush();
     
     
     //gets the key from its cipher object using the getKey() method, 
     //and then converts the result into a BigInteger object.
     byte[] byteArray = cipher.getKey();
     symmetricKey = new BigInteger(1, byteArray);
     System.out.println("symmetric key: "+ symmetricKey);
     
     //start
     BigInteger keyEnc= symmetricKey.modPow(E,N);
     myWriter.writeObject(keyEnc);
     myWriter.flush();
     
     //encoding proceedure and printing
     System.out.println("Encryption");
     System.out.println("Original String: "+ myName);
     byte [] tempEnc= cipher.encode(myName);
     BigInteger tempArray = new BigInteger(1, myName.getBytes());
     System.out.println("BigInteger representation of array: "+tempArray);
     tempArray= new BigInteger(1, tempEnc);
     System.out.println("BigInteger representation of encrypted array: " + tempArray);
     myWriter.writeObject(tempEnc);
     myWriter.flush();
     //end
     
     
     this.setTitle(myName);      // Set title to identify chatter

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
     outputThread.start();                    // from Server

        addWindowListener(
             new WindowAdapter()
             {
                 public void windowClosing(WindowEvent e) {
                     try {
                         
                         String message = "CLIENT CLOSING"; 
                         
                         System.out.println("Encryption");
                         System.out.println("The original String message: " + message);
                         byte[] encode = cipher.encode(message);
                         byte[] byteArray = message.getBytes();
                         System.out.println("The corresponding array of bytes: ");
                         for(int i = 0; i < byteArray.length; i++) {
                                System.out.print(byteArray[i]+ ", ");
                            }   
                            System.out.println();
                         
                         System.out.println("The encrypted array of bytes: ");// + byteArray
                         for(int i = 0; i < encode.length; i++) {
                                System.out.print(encode[i]+ ", ");
                            }   
                            System.out.println();
                         myWriter.writeObject(cipher.encode(message));
                         myWriter.flush();
                         System.exit(0);
                     } 
                     
                     catch(Exception error) {
                         System.out.println("Error");
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
            //receieved bytes
            byte[] encryptionMessage=(byte[]) myReader.readObject();

            String currMsg= cipher.decode(encryptionMessage);

            outputArea.append(currMsg+"\n");

            System.out.println("Dencryption");
            System.out.println("The array of bytes received: "); //+tempArray
            for(int i = 0; i < encryptionMessage.length; i++) {
                System.out.print(encryptionMessage[i]+ ", ");
            }
            System.out.println();

            byte[] decArray= currMsg.getBytes();


            System.out.println("The decrypted array of bytes: "); //+ tempArray
            for(int i = 0; i < decArray.length; i++) {
                System.out.print(decArray[i]+ ", ");
            }
            System.out.println();


            System.out.println("The corresponding String: " + currMsg);
            
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
     
     String sendMsg= myName + ":" + currMsg;
     //encoding proceedure and printing
     System.out.println("Encryption");
     System.out.println("The original String messageg: "+ sendMsg);


     byte[] encodeArray= cipher.encode(sendMsg);
     byte[] correspondingArray = sendMsg.getBytes();
     System.out.println("The corresponding array of bytes: "); //+ correspondingArray)
     for(int i = 0; i<correspondingArray.length;i++)
            {
                System.out.print(correspondingArray[i] + ", ");
            }
             System.out.println();
     System.out.println("The encrypted array of bytes: " ); //+ encodeArray
     for(int i = 0; i<encodeArray.length;i++)
            {
                System.out.print(encodeArray[i] + ", ");
            }
             System.out.println();

     myWriter.writeObject(encodeArray);
     myWriter.flush();
   }
   catch(Exception error)
   {
     System.out.println("Problem sending message"); 
   }
 }           

 public static void main(String [] args)
 {
   SecureChatClient JR = new SecureChatClient();
   JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 }
}

