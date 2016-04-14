public class User {

    private String username;
    private String email;
    private String password;
    private int totalPoints;
    private int totalPointsSend;
    private int totalPointsReceived;

    public User (String username, String email, String password){
        this.username=username;
        this.email=email;
        this.password=password;
        this.totalPoints=20;
        this.totalPointsReceived=0;
        this.totalPointsSend=0;
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