package multithreadedserver;

//Imports
import java.net.*;
import java.io.*;

//Class for a multi-threaded server
public class MultithreadedSocketServer {
 public static void main(String[] args) throws Exception {
   try{
	 //Create multi-thread server
     ServerSocket server=new ServerSocket(4997);
     int counter=0;
     System.out.println("Server Started ....");
     //Run a continuous loop to accept new clients
     while(true){
       counter++;
       Socket serverClient=server.accept();  //server accept the client connection request
       System.out.println(" >> " + "Client No:" + counter + " started!");
       //Creates a new thread for each client and runs thread
       ServerClientThread sct = new ServerClientThread(serverClient,counter); //send  the request to a separate thread
       sct.start();
     }
   }catch(Exception e){
     System.out.println(e);
   }
 }
}