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
    private static Map<String, User> usermap = new HashMap<String, User>();
    private static Map<String, Station> stationsmap = new HashMap<String, Station>();


    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(4444);  //Server socket
        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }
        System.out.println("Server started. Listening to the port 4444");
        //populate the Stations with all the information
        populate();
        while (true) {
            try {
                //Client -> Server
                clientSocket = serverSocket.accept();   //accept the client connection
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); //get the client message
                message = bufferedReader.readLine();
                String [] array = message.split(" ");
                inputStreamReader.close();
                clientSocket.close();
                switch(array[0]){
                    case "Sign_Up":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]+" Email: "+array[2]+" Password: "+array[3]);
                        User user = new User(array[1],array[2],array[3]);
                    	if(!usermap.containsKey(array[1])){
                    		usermap.put(array[1], user);
                    		sendMessageToClient("Sign Up Successfully");
                    	}
                    	else
                    		sendMessageToClient("Username Already on use! Try another!");
                    break;
                    case "Sign_In":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]+" Password: "+array[2]);
                    	if(usermap.containsKey(array[1])){
                    		if(usermap.get(array[1]).getPassword().equals(array[2])){
                    			sendMessageToClient("1");
                    		}
                    		else
                        		sendMessageToClient("Invalid Password!");
                    	}
                    	else
                    		sendMessageToClient("User doesn't exist!");
                	break;
                    case "Send_Points":
                        System.out.println("Method: "+array[0]+" Sender: "+array[1]+" Points: "+array[2]+" Receiver: "+array[3]);
                        if (usermap.get(array[1]).canISendPoints(Integer.parseInt(array[2]))){
                        	usermap.get(array[1]).sendPoints(Integer.parseInt(array[2]));
                        	usermap.get(array[3]).receivePoints(Integer.parseInt(array[2]));
                            sendMessageToClient("Points successfully sent to"+" "+array[3]);
                        }
                        else{
                            sendMessageToClient("Not enough points to send!");
                        } 
                        break;
                    case "Asking_Points":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]);
                        sendMessageToClient(""+usermap.get(array[1]).getPoints());
                        break;
                    case "Asking_AllPoints":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]);
                        sendMessageToClient(""+usermap.get(array[1]).getAllPoints());
                        break;
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
    
    public static void populate(){
    	stationsmap.put("Cascais", new Station("Cascais",15,38.694996,-9.422548, "Excellent"));
    	stationsmap.put("Parede", new Station("Parede",12,38.693449, -9.355405, "Very Good"));
    	stationsmap.put("São João do Estoril", new Station("São João do Estoril",15,38.701810, -9.385989, "Excellent"));
    	stationsmap.put("Carcavelos", new Station("Carcavelos",10,38.687955, -9.337342, "Very Good"));
    	stationsmap.put("Oeiras", new Station("Oeiras",10,38.697165, -9.314658, "Very Good"));
    	stationsmap.put("Paço de Arcos", new Station("Paço de Arcos",8 , 38.697081, 9.291610, "Good"));
    }
}
