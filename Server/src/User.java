import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;


public class User {

    private String username;
    private String email;
    private String password;
    private int totalPoints;
    private int totalPointsSend;
    private int totalPointsReceived;
    private PublicKey publicKey;
    private HashMap<String, Integer> messageIDController;

    public User (String username, String email, String password){
        this.username=username;
        this.email=email;
        this.password=password;
        this.totalPoints=20;
        this.totalPointsReceived=0;
        this.totalPointsSend=0;
        com.sun.org.apache.xml.internal.security.Init.init();
        this.messageIDController=new HashMap<String, Integer>();
    }
    
    public boolean checkIDSender(String sender, Integer id){
    	if(this.messageIDController.containsKey(sender)){
    		if(this.messageIDController.get(sender)>=id)
    			return false;
    		else{
    			this.messageIDController.put(sender, id);
    			return true;
    		}
    	}
    	else{
    		this.messageIDController.put(sender, id);
    		return true;
    	}
    }
    
    public void setPublicKey(String key)
    {
    	try{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] publicKeyBytes = Base64.decode(key.getBytes());
	        KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
	        PublicKey publicKey = keyFactory.generatePublic(keySpec);
	    	this.publicKey=publicKey;
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException | Base64DecodingException e){
			e.printStackTrace();
		}
    }
    
    public PublicKey getPublicKey(){
    	return this.publicKey;
    }
    
    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }
    
    public void receivePointsTrajectory(int points){
    	this.totalPoints+=points;
    }
    
    public int getPoints(){
        return this.totalPoints;
    }

    public void sendPoints(int points){
        this.totalPoints-=points;
        this.totalPointsSend+=points;
    }
    
    public void receivePoints(int points){
        this.totalPoints+=points;
        this.totalPointsReceived+=points;
    }
    
    public int getSendPoints(){
        return this.totalPointsSend;
    }
    
    public int getReceivedPoints(){
        return this.totalPointsReceived;
    }
    
    public String getAllPoints(){
    	return this.totalPoints+" "+this.totalPointsSend + " "+ this.totalPointsReceived;
    }
    
    public boolean canISendPoints(int points){
    	if(this.totalPoints-points>=0){
    		return true;
    	}
    	else
    		return false;
    }
}