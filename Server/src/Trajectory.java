import java.util.ArrayList;

public class Trajectory {

    private String username;
    private ArrayList<String> trajectoryPoints = new ArrayList<String>();
    private String distance;
    private String date;
    private int earnedPoints;
   

    public Trajectory(String username, String distance, String date, ArrayList<String> trajectoryPoints, int points){
        this.username=username;
        this.distance=distance;
        this.date=date;
        this.trajectoryPoints=trajectoryPoints;
        this.earnedPoints=points;
    }

    public void setUsername (String username){
        this.username=username; 
    }
    public void setDistance (String distance){
        this.distance=distance;
    }
    public void setDate (String date){
        this.date=date;
    }
    public void setTrajectoryPoints (ArrayList<String> trajectoryPoints){
        this.trajectoryPoints=trajectoryPoints;
    }
    public ArrayList<String> getTrajectoryPoints (){
         return trajectoryPoints;
    }
    public String getUsername(){
        return username;
    }
    public String getDate(){
        return date;
    }
    public String getDistance(){
        return distance;
    }
    
    public int getEarnedPoints(){
    	return this.earnedPoints;
    }
}