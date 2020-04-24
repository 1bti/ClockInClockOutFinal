package multithreadedserver;

import java.net.*;
import java.io.*;
public class TCPClient {
  public static void main(String[] args) throws Exception {
  try{
    //Socket socket=new Socket("127.0.0.1",4997);
	Socket socket=new Socket("52.14.143.239",4997);
	//I/O
    DataInputStream inStream=new DataInputStream(socket.getInputStream());
    DataOutputStream outStream=new DataOutputStream(socket.getOutputStream());
    BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
    String clientMessage="",serverMessage="";
    
    //Login Info & Action Request
    System.out.println("Enter username :");
    clientMessage=br.readLine();
    outStream.writeUTF(clientMessage);
    outStream.flush();
    System.out.println("Enter password :");
    clientMessage=br.readLine();
    outStream.writeUTF(clientMessage);
    outStream.flush();
    System.out.println("Clock In(1) or Clock Out(2) :");
    clientMessage=br.readLine();
    outStream.writeUTF(clientMessage);
    outStream.flush();
    
    //Action
    serverMessage=inStream.readUTF();
    System.out.println(serverMessage);
    if(clientMessage.equals("1")) { //Clock In
    	serverMessage=inStream.readUTF();
        if(serverMessage.equals("Which Job?")) { //Multiple Jobs
        	System.out.println(serverMessage);
        	serverMessage=inStream.readUTF();
        	int numJobs = Integer.parseInt(serverMessage);
        	int i = 0;
        	while(i < numJobs) { //Print Jobs
        		serverMessage=inStream.readUTF();
        		System.out.println(serverMessage);
        		i++;
        	}
        	clientMessage=br.readLine(); //Choose Job
            outStream.writeUTF(clientMessage);
            outStream.flush();
        }
        serverMessage=inStream.readUTF();
        System.out.println(serverMessage);
    }
    else { //Clock Out
    	serverMessage=inStream.readUTF();
        System.out.println(serverMessage);
    }
    //outStream.close();
    //socket.close();
  }catch(Exception e){
    System.out.println(e);
  }
  }
}