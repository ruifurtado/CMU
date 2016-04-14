
public class Station {
	private String location;
	private int availableBikes;
	private double latitude;
	private double longitude;
	private String feedback;
	
	public Station(String location, int availableBikes, double latitude, double longitude, String feedback ){
		this.location=location;
		this.availableBikes=availableBikes;
		this.latitude=latitude;
		this.longitude=longitude;
		this.feedback=feedback;
	}
	
	public String getLocation(){
		return this.location;
	}
	
	public int getAvailableBikes(){
		return this.availableBikes;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public String getFeedback(){
		return this.feedback;
	}
	
	public void updateAvailableBikes(){
		this.availableBikes--;
	}
}
