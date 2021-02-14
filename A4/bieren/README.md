#CS/COE 1501 Assignment4

Posted:  April 6, 2015

***Due:  April 24, 2015***

##Goal:
To gain insight into using cryptography to secure client/server communications.

##High-level description:
You will be developing a secure chat server for this assignment.  Users will use a client application to connect to a running server. Once connected, users should be able to send messages to the server. Any messages sent to the server should be relayed to all users connected to the server.

You will secure the communications between each client and server. You can assume that the server is trusted by all users. We will simply be using cryptography to "protect" the messages en route from client to server and vice versa.

You are provided a good amount of code to work with. First, you are give the working **unsecure** old_code/ChatClient.java and old_code/ChatServer.java. These can server as a model for how the client and server applications should run and you can feel free to base your project on this code. You are further given a working **secure** SecureChatServer.java, and a set of RSA keys to be used by the server (keys.txt). In order to complete the project, you will need to write a secure client to interact with this secure server (which you should name "SecureChatClient.java"), and two symmetric ciphers ("Add128.java" and "Substitute.java") that implement the provided SymCipher.java interface.

##Specifications:
1. The client should be implemented such that it interacts with the server as follows:
	1. It opens a connection to the server via a Socket at the server's IP address and port. Use 8765 for the port.  Have your client prompt the user for the server name.  More than likely you will always be using "localhost" for the server name, since you will be running the server on your own machine.
	1. It creates an ObjectOutputStream on the socket (for writing) and immediately calls the flush() method (this technicality prevents deadlock).
	1. It creates on ObjectInputStream on the socket (be sure you create this AFTER creating the ObjectOutputStream).
	1. It receives the server's public key, e, as a BigInteger object.
	1. It receives the server's public mod value, n, as a BigInteger object.
	1. It receives the server's preferred symmetric cipher (either "Sub" or "Add"), as a String object, then outputs the type of symmetric encryption (Add128 or Substitute) to the console.
	1. Based on the value of the cipher preference, it creates either a Substitute object or an Add128 object, storing the resulting object in a SymCipher variable. See details below on the requirements for the Substitute and Add128 classes.
	1. It gets the key from its cipher object using the getKey() method, and then converts the result into a BigInteger object. To ensure that the BigInteger is positive (a requirement for RSA), use the BigInteger constructor that takes a sign-magnitude representation of a BigInteger – see the API for details. It then outputs a BigInteger representation of the symmetric key that is generated to the console.
	1. It RSA-encrypts the BigInteger version of the key using e and n, and sends the resulting BigInteger to the server (so the server can also determine the key – the server already knows which cipher will be used).
	1. It prompts the user for his/her name, then encrypts it using the cipher and sends it to the server. The encryption will be done using the encode() method of the SymCipher interface, and the resulting array of bytes will be sent to the server as a single object using the ObjectOutputStream.
	1. At this point the "handshaking" is complete and the client begins its regular execution. The client should have a nice user interface and should allow for deadlock-free reading of messages from the user and posting of messages received from the server. All messages typed in by the user should be sent to the server and should only be posted after they are received back from the server.
	1. All messages typed in from the user must be encrypted using the encode() method of chosen cipher object, then sent to the server for distribution using the ObjectOutputStream. For details on encode(), see below.
	1. All messages received by the client should be read from the ObjectInputStream as byte[] objects.  They should then be decrypted using the decode() method of the chosen cipher and posted in the client application.
	1. When the client quits the message "CLIENT CLOSING" should be sent to the server. This message should be encrypted like all other messages, but should not have any prefix (ex: no client name). The server will use this special message as a sentinel to close the connection with the client.

1. For each encryption performed by the client, output the following to the console:
	1. The original String message.
	1. A BigInteger representation of the corresponding array of bytes.
	1. A BigInteger representation of the encrypted array of bytes.
	  
1. For each dencryption performed by the client, output the following to the console:
	1. A BigInteger representation of the array of bytes received.
	1. A BigInteger representation of the decrypted array of bytes.
	1. The corresponding String.

1. You should write the Add128 class to implement SymCipher as follows:
	* It will have two constructors, one without any parameters and one that takes a byte array. The parameterless constructor will create a random 128 byte additive key and store it in an array of bytes. The other constructor will use the byte array parameter as its key. The SecureChatClient will call the parameterless constructor and the SecureChatServer calls the version with a parameter.
	* To implement the encode() method, convert the String parameter to an array of bytes and simply add the corresponding byte of the key to each index in the array of bytes. If the message is shorter than the key, simply ignore the remaining bytes in the key. If the message is longer than the key, cycle through the key as many times as necessary. The encrypted array of bytes should be returned as a result of this method call.
	* To decrypt the array of bytes, simply subtract the corresponding byte of the key from each index of the array of bytes. If the message is shorter than the key, simply ignore the remaining bytes in the key. If the message is longer than the key, cycle through the key as many times as necessary. Convert the resulting byte array back to a String and return it.

1. You should write the Substitute class to implement SymCipher as follows:
	* It will have two constructors, one without any parameters and one that takes a byte array. The parameterless constructor will create a random 256 byte array which is a permutation of the 256 possible byte values and will serve as a map from bytes to their substitution values. For example, if location 65 of the key array has the value 92, it means that byte value 65 will map into byte value 92. Note that you will also need an inverse mapping array for this cipher, which can be easily derived from the substitution array (so you only need to send the original substitution array to the server). Be careful with this class since byte values can be negative, but array indices cannot be negative – this issue can be resolved with some thought. The other constructor will use the byte array parameter as its key. The SecureChatClient will call the parameterless constructor and the SecureChatServer calls the version with a parameter.
	* To implement the encode() method, convert the String parameter to an array of bytes, then iterate through all of the bytes, substituting the appropriate bytes from the key. Again, be careful with negative byte values
	* To decode, simply reverse the substitution (using your decode byte array) and convert the resulting bytes back to a String.

	
##Submission Guidelines:
* **DO NOT SUBMIT** any IDE package files.
* You must name your client application SecureChatClient.java.
* You must be able to compile your client application by "javac SecureChatClient.java".
* You must be able to run your client application by "java SecureChatClient".
* You must name your server application SecureChatServer.java.
* You must be able to compile your server application by "javac SecureChatServer.java".
* You must be able to run your server application by "java SecureChatServer".

##Additional Notes/Hints:
* Be sure to refer to ChatClient.java and ChatServer.java for a look at how to implement the basic functionality.
* Note that the server provided will not compile or run without the SymCipher interface and the Add128 and Substitute classes.  You must implement Add128 and Substitute before using the server.
