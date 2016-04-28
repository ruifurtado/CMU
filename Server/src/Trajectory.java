
public class Trajectory {

    private String username;
    private String startingPoint;
    private String endPoint;
    private String distance;
    private String date;
    private String name;
    private String time;

    public Trajectory(String name, String username, String startingPoint, String endPoint, String distance, String date, String time){
        this.name=name;
        this.username=username;
        this.startingPoint=startingPoint;
        this.endPoint=endPoint;
        this.distance=distance;
        this.date=date;
        this.time=time;
    }
    public void setName (String name){
        this.name=name;
    }
    public void setTime (String time){
        this.time=time;
    }
    public void setUsername (String username){
        this.username=username; 
    }
    public void setStartingPoint (String startingPoint){
        this.startingPoint=startingPoint;
    }
    public void setEndPoint (String endPoint) {
        this.endPoint = endPoint;
    }
    public void setDistance (String distance){
        this.distance=distance;
    }
    public void setDate (String date){
        this.date=date;
    }
    public String getStartingPoint(){
        return startingPoint;
    }
    public String getUsername(){
        return username;
    }
    public String getEndPoint(){
        return endPoint;
    }
    public String getDate(){
        return date;
    }
    public String getDistance(){
        return distance;
    }
    public String getName(){
        return name;
    }
    public String getTime(){
        return time;
    }
}
