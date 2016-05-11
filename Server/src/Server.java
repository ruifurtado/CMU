import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.*;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;


public class Server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    private static Map<String, User> usermap = new HashMap<String, User>();
    private static Map<String, Station> stationsmap = new HashMap<String, Station>();
    private static ArrayList<Trajectory> trajectorymap= new ArrayList<Trajectory>();

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
                if(message.contains("PublicKey") || message.contains("Send_Points")){
                	while(bufferedReader.ready()){
                		message+=bufferedReader.readLine();
                	}
                }
                String [] array = message.split(",");
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
                    			sendMessageToClient("1 "+usermap.get(array[1]).getAllPoints());
                    		}
                    		else
                        		sendMessageToClient("Invalid Password!");
                    	}
                    	else
                    		sendMessageToClient("User doesn't exist!");
                    	break;
                    case "Send_Points":
                        System.out.println("Method: "+array[0]+" Sender: "+array[1]+" Points: "+array[2]+" Receiver: "+array[3]);
                        String msgOriginal=array[0]+","+array[1]+","+array[2]+","+array[3]+","+array[4];
                        if(checkMessageIDController(array[0],array[3],array[4])){
	                        if(checkSignature(msgOriginal, array[5], array[1])){
	                        	usermap.get(array[1]).sendPoints(Integer.parseInt(array[2]));
	                        	usermap.get(array[3]).receivePoints(Integer.parseInt(array[2]));
	                        	sendMessageToClient("Points successfully updated");
	                        }
	                        else
	                        	sendMessageToClient("Message Corrupted!!!Worning!!!!");
                        }
                        else
                        	sendMessageToClient("Message Corrupted!!!Worning!!!!");

                        break;
                    case "PublicKey":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]);
                        usermap.get(array[1]).setPublicKey(array[2]);
                        sendMessageToClient("PublicKey Saved!");
                        break;
                    /*case "Asking_AllPoints":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]);
                        sendMessageToClient(""+usermap.get(array[1]).getAllPoints());
                        break;
                       */
                    case "Asking_Stations":
                        System.out.println("Method: "+array[0]);
                        String aux="";
                        for(String key : stationsmap.keySet()){
                        	if(stationsmap.get(key).getAvailableBikes()>0)
                        		aux=aux+key+",";
                        }
                        sendMessageToClient(aux);
                        break;
                    case "Station_Coord":
                        String stc = "";
                        for(String key: stationsmap.keySet()) {
                            if(stationsmap.get(key).getAvailableBikes()>0)
                                stc += stationsmap.get(key).getLocation()+";"+stationsmap.get(key).getLatitude() +";"+ stationsmap.get(key).getLongitude()+",";
                        }
                        sendMessageToClient(stc);
                        break;
                    case "Station_Description":
                        System.out.println("Method: "+array[0]+" Station: "+array[1]);
                        sendMessageToClient(""+stationsmap.get(array[1]).getAvailableBikes()+","+stationsmap.get(array[1]).getLatitude()+","+stationsmap.get(array[1]).getLongitude()+","+stationsmap.get(array[1]).getFeedback());
                        break;
                    case "Book_Bike":
                    	System.out.println("Method: "+array[0]+" Station: "+array[1]);
                    	if(stationsmap.get(array[1]).getAvailableBikes()>0){
                    		stationsmap.get(array[1]).updateAvailableBikes();
                            sendMessageToClient("Bike successfully booked!");
                    	}
                    	else
                            sendMessageToClient("This station doesn't have any bike available!");
                    	break;
                    case "Asking_Trajectories":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]);
                        String aux2="";
                        for(int i=0;i<trajectorymap.size();i++){                     
                            if(trajectorymap.get(i).getUsername().equals(array[1])){ 
                                aux2=aux2+trajectorymap.get(i).getDate()+",";
                            }
                            if (aux2.equals("")){
                                aux2="Not available trajectories";
                            }
                        }
                        sendMessageToClient(aux2);
                        break;
                    case "Asking_Trajectory_Info":
                        System.out.println("Method: "+array[0]+" Trajectory: "+array[1]+" Username: "+array[2]);
                        for(int i=0;i<trajectorymap.size();i++){
                            if(trajectorymap.get(i).getDate().equals(array[1]) && trajectorymap.get(i).getUsername().equals(array[2])){
                                sendMessageToClient(""+trajectorymap.get(i).getDistance()+","+trajectorymap.get(i).getDate()+","+trajectorymap.get(i).getEarnedPoints()+","+trajectorymap.get(i).getTrajectoryPoints().toString().substring(1,trajectorymap.get(i).getTrajectoryPoints().toString().length()-1));
                            }
                        }            
                        break;
                    case "Trajectory_Sender":
                        System.out.println("Method: "+array[0]+" Username: "+array[1]+ " Date: "+array[2]+" Distance: "+array[3]+" Points: "+array[4]);
                        ArrayList <String> trajectoryPoints= new ArrayList<String>();
                        for(int i=5;i<array.length;i++){
                            trajectoryPoints.add(array[i]);
                        }
                        trajectorymap.add(new Trajectory(array[1],array[3],array[2],trajectoryPoints,Integer.parseInt(array[4])));
                        usermap.get(array[1]).receivePointsTrajectory(Integer.parseInt(array[4]));
                        sendMessageToClient("Trajectory Received!");
                        break;
                }
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
    }
    
    public static boolean checkMessageIDController(String sender, String receiver, String id){
    	if(usermap.get(receiver).checkIDSender(sender,Integer.parseInt(id)))
    		return true;
    	else
    		return false;
    }
    
    public static boolean checkSignature(String original, String sign, String user){
    	try{
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(usermap.get(user).getPublicKey());
            signature.update(original.getBytes());
            if(signature.verify(Base64.decode(sign.getBytes())))
                return true;
            return false;
        }
        catch (NoSuchAlgorithmException | Base64DecodingException | InvalidKeyException | SignatureException e){
            System.out.println("DEU ERRO NO VERIFY");
            e.printStackTrace();
        }
        return false;
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
        stationsmap.put("Cascais", new Station("Cascais",15,38.70061,-9.41764, "Excellent"));
        stationsmap.put("São João do Estoril", new Station("São João do Estoril",15,38.70275, -9.39199, "Excellent"));
        stationsmap.put("Parede", new Station("Parede",12,38.68845, -9.36009, "Very Good"));
        stationsmap.put("Carcavelos", new Station("Carcavelos",10,38.68085, -9.33621, "Very Good"));
        stationsmap.put("Oeiras", new Station("Oeiras",10,38.68499, -9.31297, "Very Good"));
        stationsmap.put("Paço de Arcos", new Station("Paço de Arcos",8 , 38.69118, -9.29657, "Good"));
        
        usermap.put("bernardo", new User("bernardo", "bernardo@gmail.com","13"));
        usermap.put("vasco", new User("vasco", "bernardo@gmail.com","13"));
        
        //trajectorymap.add(new Trajectory("Cascais Grand Prix","bernardo","Cascais","Parede","20km","08-04-2016 11:51:18","00h:50m:33s"));
        //trajectorymap.add(new Trajectory("Cascais Grand Prix2","bernardo","Carcavelos","Parede","17km","09-04-2016 11:51:18","00h:42m:27s"));
    }
}
