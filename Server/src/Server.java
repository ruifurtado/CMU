import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;

public class Server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    private static Map<String, User> structure = new HashMap<String, User>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(4444);  //Server socket
        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }
        System.out.println("Server started. Listening to the port 4444");
        while (true) {
            try {
                //Client -> Server
                clientSocket = serverSocket.accept();   //accept the client connection
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); //get the client message
                message = bufferedReader.readLine();
                String [] array = message.split(" ");
                System.out.println("Method: "+array[0]+" Username: "+array[1]+" Email: "+array[2]+" Password: "+array[3]);
                inputStreamReader.close();
                clientSocket.close();
                User user = new User(array[1],array[2],array[3]);
                switch(array[0]){
                    case "Sign_Up":
                    	if(!structure.containsKey(array[1])){
                    		structure.put(array[1], user);
                    		sendMessageToClient("Sign Up Successfully");
                    	}
                    	else
                    		sendMessageToClient("Username Already on use! Try another!");
                }
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
    }
    
    public static void sendMessageToClient(String message){
    	try {
			clientSocket = serverSocket.accept();
			//Server -> Client 
	        // Get output buffer
	        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	        // Write output
	        writer.write(message);
	        writer.flush();
	        writer.close();
	        clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   //accept the client connection       
    }
}
