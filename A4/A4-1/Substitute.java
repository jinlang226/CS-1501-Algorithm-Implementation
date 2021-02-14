/*************************************************************************
 *  CS 1501 Summer 2020                                                          
 *                                                                               
 *  Author: Jinlang Wang                                                        
 *  Email: jiw159@pitt.edu  
 *  Describtion: utilize the SymCipher interface and your 
 * 					Substitute and Add128 ciphers within a simple secure chat program.
 *************************************************************************/

import java.util.*;
import java.io.*;
import java.net.*;
import java.math.*;

public class Substitute implements SymCipher{

	private byte[] byteArray;
	
	public Substitute() {
		// create a 256 byte array, random permutation of the 256 possible byte values
		//serve as a map from bytes to their substitution values
		byteArray = new byte[256];
		List<Byte> shuffle = new ArrayList<>(256);
		
		for(short i = 0; i < 256; i++)
		{
			shuffle.add((byte)i);
		}
		Collections.shuffle(shuffle);

		for(int i = 0; i < shuffle.size(); i++)
		{
			byteArray[i] = shuffle.get(i);
		}
	}
	
	public Substitute(byte[] key) {
		byteArray = key;
	}

	@Override
	public byte[] getKey() {
		return byteArray;
	}

	@Override
	public byte[] encode(String S) {
		// convert the String parameter to an array of bytes
		byte[] info = S.getBytes();
	    byte[] encode= new byte[info.length];
		// iterate through all of the bytes, substituting the appropriate bytes from the key
	    for(int i = 0; i < info.length; i++) {
	    	encode[i] = byteArray[info[i] & 0xff];
	    }
		return encode;
	}
	
	@Override
	public String decode(byte[] bytes) {
		byte[] decode = new byte[bytes.length];
		
		// simply reverse the substitution (using your decode byte array) 
		byte[] inverseByteArray = new byte[256];
		for(int i = 0; i < byteArray.length; i++) {
			inverseByteArray[byteArray[i] & 0xff] = (byte) i;
		}
		
		// and convert the resulting bytes back to a String.
		for(int i = 0; i < bytes.length; i++) {
			decode[i] = inverseByteArray[bytes[i] & 0xff];
		}
		
		return new String(decode);
	}
	
}
