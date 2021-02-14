import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

public class Substitute implements SymCipher
{
  private byte[] byteKey;
  
  public Substitute()
  {
    byteKey= new byte[256];
    //initialize the byte array with index numbers
    for(int i=0; i<256;i++)
    {
      byteKey[i]=(byte) i;
    }
    //create a psudo-random permutation of the ordering--change i for more iterations
    Random r=new Random();
    for(int i=0; i<700;i++)
    {
      int index1=r.nextInt(256);
      int index2=r.nextInt(256);
      //swap based on the random indicies
      byte temp= byteKey[index1];
      byteKey[index1]=byteKey[index2];
      byteKey[index2]=temp;
    }
  }
  
  public Substitute(byte[] key)
  {
    byteKey=key;
  }
  
  public byte [] getKey()
  {
    return byteKey;
  }
  
  public byte [] encode(String S)
  {
    byte[] message= S.getBytes();
    byte[] encryption= new byte[message.length];
    
    for(int i=0; i<message.length;i++)
    {
      //ensure that the index being looked at is a positive number in our range and substitute
      encryption[i]= byteKey[message[i] & 0xff];
    }
    return encryption;
  }
  
  public String decode(byte[] bytes)
  {
    byte[] inverseMap= new byte[256];
    byte[] decryption=new byte[bytes.length];
    String message;
    //create the inverse mapping
    for(int i=0; i<byteKey.length; i++)
    {
      byte temp= (byte) i;
      inverseMap[byteKey[i] & 0xff]= temp;
    }
    //use inverse mapping to reverse substitution
    for(int i=0; i<bytes.length; i++)
    {
      decryption[i]=inverseMap[bytes[i] & 0xff];
    }
    //convert back to a string and return original message
    return message= new String(decryption);
  }
}