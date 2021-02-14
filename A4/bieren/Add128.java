import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;
import java.security.SecureRandom;

public class Add128 implements SymCipher 
{
  private byte [] byteKey;
  
  public Add128()
  {
    byteKey=new byte[128];
    Random r= new SecureRandom();
    r.nextBytes(byteKey);  //fills the entire array with random bytes
  }
  
  public Add128(byte [] key)
  {
    byteKey=key;
  }
  
  public byte [] getKey()
  {
    return byteKey;
  }
  
  public byte [] encode(String S)
  {
    byte [] message= S.getBytes(); //create an array of bytes from original message
    int mLength= message.length;
    byte[] encryption= new byte[mLength];
    int arrayIter=0;
    //iterate through the message and add the corresponding key value looping when necessary
    for(int i =0; i<mLength; i++)
    {
      if(arrayIter>=byteKey.length)
      {
        arrayIter=0;
      }
      else
      {
        encryption[i]=(byte) (message[i]+byteKey[i]);
        arrayIter++;
      } 
    }
    return encryption;
  }
  
  public String decode(byte [] bytes)
  {
    byte[] decryption= new byte[bytes.length];
    int arrayIter=0;
    String message;
    //iterate through the message and subtract the corresponding key value looping when necessary
    for(int i =0; i<bytes.length; i++)
    {
      if(arrayIter>=byteKey.length)
      {
        arrayIter=0;
      }
      else
      {
        decryption[i]=(byte) (bytes[i]-byteKey[i]);
        arrayIter++;
      } 
    }
    return message=new String(decryption);  
  }
}