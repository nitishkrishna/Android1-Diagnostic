/*
 * Diagnostic Exam - EE 579
* Server - Server class with send receive functions
*
* Written by Nitish Krishna
* 
*/

package edu.usc.anrg.ee579.diagnostic;
import java.net.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import edu.usc.anrg.ee579.diagnostic.protocol.PtclImplementation;


/**
* The Class Contains the server side methods for DataGram Socket connection
* Methods are described below:
* 
*  sendPacket - sends a packet on a socket connection given the string message
*  and the receiver IP address
*  
*  processPacket - receives a packet on a socket connection given the port
*  number to listen on and returns message type
*  
*  main - processes user input arguments - makes a call to server data gram 
*  protocol implementation function
*  
*  serverDataGramImpl - this method does the packet encoding
* 
**/


public class Server {
  
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
      DatagramPacket dgramPacket = new DatagramPacket(buffer, TOTAL_LEN, iaddr,
          portNumber);
      dgramSocket.send(dgramPacket);
      System.out.println("SENT : ");
      System.out.println(message);
    }
    catch (UnknownHostException e) {
      System.err.println("Don't know about host " + iaddr);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to " +
          iaddr);
      System.err.println(e.getMessage());
      System.err.println(e.getStackTrace());
      System.exit(1);
    } 

    
    return 0; 
  }
  public static String processPacket(DatagramSocket dgramSocket)
      throws IOException, ClassNotFoundException {
    
    
    byte[] buffer = new byte[10012];
    byte[] b1 = new byte[4];
    byte[] b2 = new byte[4];
    byte[] b3 = new byte[4];
    
    DatagramPacket dgramRecvPacket = new DatagramPacket(buffer, buffer.length); 
    dgramSocket.receive(dgramRecvPacket);
    InetAddress iaddr = dgramRecvPacket.getAddress();
    
    System.out.println("RECEIVED : ");
    
    System.arraycopy(buffer, 0, b1, 0, b1.length);
    System.arraycopy(buffer, 4, b2, 0, b2.length);
    System.arraycopy(buffer, 8, b3, 0, b3.length);
    ByteBuffer wrapped = ByteBuffer.wrap(b3);
    int MSG_LEN = wrapped.getInt();
    byte [] msg = new byte[MSG_LEN];
    System.arraycopy(buffer, 12, msg, 0, MSG_LEN);
    wrapped = ByteBuffer.wrap(b1);
    int API_TYPE = wrapped.getInt();
    wrapped = ByteBuffer.wrap(b2);
    int TOTAL_LEN = wrapped.getInt();
    
    String message = new String(msg);
    System.out.println(message);
    
    
    //String compare section
    if(Arrays.equals(msg, "HELLO".getBytes()))
    {
      return "1 " + iaddr.toString();
    }
    else if(Arrays.equals(msg, "LIST".getBytes()))
    {
      return "2 " + iaddr.toString();
    }
    else if(message.startsWith("GET"))
    {
      return "3 " + iaddr.toString() + ", " + message;
    }
    else if(Arrays.equals(msg, "END".getBytes()))
    {
      return "4 " + iaddr.toString();
    }
    else {
      System.out.println("Invalid message");
      return "0 " + iaddr.toString();
    }
      
     
    
  }
  @SuppressWarnings("unused")
  public static void main(String[] args)
      throws IOException, ClassNotFoundException {
    // TODO Auto-generated method stub
    
    if (args.length > 1) {
      System.err.println("Usage: java Server <port number>");
      System.exit(1);
    }
    int portNumber;
    if (args.length == 1) {
      portNumber = Integer.parseInt(args[0]);
    }
    else
    {
      portNumber = 9777;
    }  
    
    PtclImplementation.serverDataGramImpl(portNumber);
    
  }
    
}

