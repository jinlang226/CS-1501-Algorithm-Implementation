//package assignment4;

import java.util.*;
import java.math.*;

public class Add128 implements SymCipher{
	
	private byte[] byteArray;

//	public static void main(String[] args)
//	{
//	 	Add128 test = new Add128();
//	 	byte[] jumble = test.encode("Why are we being graded for GUI in an Algorithms Class ???");
//	 	String plsWork = test.decode(jumble);
//	 	System.out.println(plsWork);
//	}
	
	public Add128() {
		// create a random 128 byte additive key and store it in an array of bytes
		byteArray = new byte[128];
		Random randomNum = new Random();
		randomNum.nextBytes(byteArray);
		
	}
	
	public Add128(byte[] key) {
		// use the byte array parameter as its key
		byteArray = key;
	}

	@Override
	public byte[] getKey() {
		return byteArray;
	}

	@Override
	public byte[] encode(String S) {
		// convert the string parameter to an array of bytes
		byte[] info = S.getBytes();
		
		// simply add the corresponding byte of the key to each index in the array of bytes
		//If the message is shorter than the key, simply ignore the remaining bytes in the key.
		//If the message is longer than the key, cycle through the key as many times as necessary
		byte[] encode = new byte[info.length];
		
		int counter = 0;
		
		for(int i = 0; i < info.length; i++) {
			if(counter >= byteArray.length) {
				counter = 0;
			}
			encode[i] = (byte) (info[i] + byteArray[counter]);
			counter++;
		}
		
		
		//The encrypted array of bytes should be returned as a result of this method call.
		return encode;
	}

	@Override
	public String decode(byte[] bytes) {
		// subtract the corresponding byte of the key from each index of the array of bytes
		byte[] decode = new byte[bytes.length];
		
		String info;
		int counter = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			if(counter >= byteArray.length) {
				counter = 0;
			}
			decode[i] = (byte) (bytes[i] - byteArray[counter]);
			counter++;
		}
		return info = new String(decode);
	}
}
