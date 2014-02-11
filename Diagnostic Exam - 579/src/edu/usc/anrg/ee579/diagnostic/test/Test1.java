package edu.usc.anrg.ee579.diagnostic.test;
import java.io.IOException;

import edu.usc.anrg.ee579.diagnostic.*;
public class Test1 extends Thread{

  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
    // TODO Auto-generated method stub
    if (args.length > 2) {
      System.err.println(
          "Usage: java Test1 <host name> <port number>");
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
    
    final String[] clientArguments = {hostName, Integer.toString(portNumber)};
    final String[] serverArguments = {Integer.toString(portNumber)};
    //Server servObject = new Server();
    //Client cliObject = new Client();
    Thread thread1 = new Thread() {
      public void run() {
        try {
          String[] commands = {"/bin/bash", "cd /Users/nitish/Documents/workspace/Diagnostic Exam - 579/src", "java edu.usc.anrg.ee579.diagnostic.Server.runServer(serverArguments)"};
          Process p = Runtime.getRuntime().exec(commands);
          
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } 
      }
    };

    Thread thread2 = new Thread() {
      public void run() {
        try {
          String[] commands = {"/bin/bash", "cd /Users/nitish/Documents/workspace/Diagnostic Exam - 579/src", "java edu.usc.anrg.ee579.diagnostic.Client.runClient(clientArguments)"};
          Process p = Runtime.getRuntime().exec(commands);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } 
      }
    };
    
    
    thread1.start();
    thread2.start();
    
    thread1.join();
    thread2.join();
  }

}
