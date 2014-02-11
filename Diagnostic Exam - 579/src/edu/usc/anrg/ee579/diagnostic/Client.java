/*
* Diagnostic Exam - EE 579
* Client - Client class with send receive functions
*
* Written by Nitish Krishna
* 
*/
package edu.usc.anrg.ee579.diagnostic;
import java.io.*;
import java.net.*;
import java.nio.*;
import edu.usc.anrg.ee579.diagnostic.protocol.*;

/**
* The Class Contains the client side methods for DataGram Socket connection
* Methods are described below:
* 
*  sendPacket - sends a packet on a socket connection given the string message
*  and the receiver IP address
*  
*  processPacket - receives a packet on a socket connection given the port
*  number to listen on and returns message type
*  
*  main - processes user input arguments - makes a call to client data gram 
*  protocol implementation function
*  
*  clientDataGramImpl - this method does the packet encoding and also accepts 
*  user input to determine which file to display
*  
*  NOTE : HELLO messages (handshake) and listing (LIST) is done automatically
*  when client connects to server.
* 
**/


public class Client{
  
  public static int sendPacket(int msgType, DatagramSocket dgramSocket,
      String message, int length, InetAddress iaddr, int portNumber)
      throws IOException, ClassNotFoundException{  
    
    int API_TYPE = 12;
    int TOTAL_LEN;
    int MSG_LEN = length;
    TOTAL_LEN = 12 + MSG_LEN;
    byte[] buffer = new byte[TOTAL_LEN];
    byte[] b1 = new byte[4];
    b1 = ByteBuffer.allocate(4).putInt(API_TYPE).array();
    byte[] b2 = new byte[4];
    b2 = ByteBuffer.allocate(4).putInt(TOTAL_LEN).array();
    byte[] b3 = new byte[4];
    b3 = ByteBuffer.allocate(4).putInt(MSG_LEN).array();
    byte[] msg = new byte[MSG_LEN];
    msg = message.getBytes("UTF-8");
      
    System.arraycopy(b1, 0, buffer, 0, b1.length);
    System.arraycopy(b2, 0, buffer, 4, b2.length);
    System.arraycopy(b3, 0, buffer, 8, b3.length);
    System.arraycopy(msg, 0, buffer, 12, msg.length);
      
    try {  
      DatagramPacket dgramPacket = new DatagramPacket(buffer, TOTAL_LEN,
          iaddr, portNumber);
      dgramSocket.send(dgramPacket);
    }
    catch (UnknownHostException e) {
      System.err.println("Don't know about host " + iaddr);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("send packet : Couldn't get I/O for the connection to "
          + iaddr);
      System.err.println(e.getMessage());
      System.err.println(e.getStackTrace());
      System.exit(1);
    } 

    return 0; 
  }
  
  public static String processPacket(DatagramSocket dgramRecvSocket) 
      throws IOException{
    byte[] buffer = new byte[10012];
    byte[] msg = new byte[10000];
    int API_TYPE = 12;
    int TOTAL_LEN;
    int MSG_LEN;
    
    DatagramPacket dgramRecvPacket = new DatagramPacket(buffer, buffer.length); 
    dgramRecvSocket.receive(dgramRecvPacket);
    
    byte[] b1 = new byte[4];
    byte[] b2 = new byte[4];
    byte[] b3 = new byte[4];
    
    System.arraycopy(buffer, 0, b1, 0, b1.length);
    System.arraycopy(buffer, 4, b2, 0, b2.length);
    System.arraycopy(buffer, 8, b3, 0, b3.length);
    ByteBuffer wrapped = ByteBuffer.wrap(b3);
    MSG_LEN = wrapped.getInt();
    msg = new byte[MSG_LEN];
    
    System.arraycopy(buffer, 12, msg, 0, MSG_LEN);
    wrapped = ByteBuffer.wrap(b1);
    API_TYPE = wrapped.getInt();
    wrapped = ByteBuffer.wrap(b2);
    TOTAL_LEN = wrapped.getInt();
    
    String message = new String(msg);
    
    
    if(message.startsWith("HELLO") )
    {
      return "1 " + message;
    }
    else if(message.startsWith("LIST") ){
      return "2 " + message;
    }
    else if(message.startsWith("GET") ){
      return "3 " + message;
    }
    else {
      System.out.println("Invalid message type");
      return "0";
    }
  }
  
  public static void main(String[] args) 
      throws IOException, ClassNotFoundException{
    // TODO Auto-generated method stub
    if (args.length > 2) {
      System.err.println(
          "Usage: java Client <host name> <port number>");
      System.exit(1);
    }
    String hostName;
    int portNumber;
    if (args.length == 2) {
      hostName = args[0];
      portNumber = Integer.parseInt(args[1]);
    }
    else if (args.length == 1){
      hostName = args[0];
      portNumber = 9777;
    }
    else {
      hostName = "localhost";
      portNumber = 9777;
    }
    
    PtclImplementation.clientDataGramImpl(hostName, portNumber);
    
   }
    
 }
  

