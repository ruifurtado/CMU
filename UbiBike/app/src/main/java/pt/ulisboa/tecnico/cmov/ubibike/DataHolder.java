package pt.ulisboa.tecnico.cmov.ubibike;

import java.util.ArrayList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

/**
 * Created by admin on 02/05/16.
 */
public class DataHolder {

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private static final DataHolder holder = new DataHolder();
    private int flagWifi=0;
    private int flagSign_In=0;
    private int points;
    private int pointsSender;
    private int pointsReceiver;
    private ArrayList<String> bufferRequest = new ArrayList<>();
    private boolean isThreadBufferActive =false;

    public boolean getFlagBuffer(){return this.isThreadBufferActive;}

    public void setFlagBuffer(){this.isThreadBufferActive=true;}

    public void resetFalgBuffer(){this.isThreadBufferActive=false;}

    public int getFlagSign_In(){return this.flagSign_In;}

    public void setFlagSign_In(){this.flagSign_In=1;}

    public void resetFlagSign_In(){this.flagSign_In=0;}

    public void addRequest(String request){this.bufferRequest.add(request);}

    public ArrayList<String> getBuffer(){return this.bufferRequest;}

    public boolean bufferHaveSomeRequest(){
        if(this.bufferRequest.size()!=0)
            return true;
        else
            return false;
    }

    public int getPoints(){
        return this.points;
    }

    public int getPointsSender(){return this.pointsSender;}

    public int getPointsReceiver(){return this.pointsReceiver;}

    public void setPoints(int pointsServer){
        this.points=pointsServer;
    }

    public void setPointsSender(int pointsServer){
        this.pointsSender=pointsServer;}

    public void setPointsReceiver(int pointsServer){
        this.pointsReceiver=pointsServer;}

    public void updatePointsSender(int pointsServer){
        //int total_points= Integer.parseInt(this.points)-Integer.parseInt(pointsServer);
     //   int total_sender=Integer.parseInt(this.pointsSender)+Integer.parseInt(pointsServer);
        this.points-=pointsServer;
        this.pointsSender+=pointsServer;
    }

    public void updatePointsReceiver(int pointsServer){
        //int total_points= Integer.parseInt(this.points)+Integer.parseInt(pointsServer);
        //int total_receiver=Integer.parseInt(this.pointsReceiver)+Integer.parseInt(pointsServer);
        this.points+=pointsServer;
        this.pointsReceiver+=pointsServer;
    }

    public void setFlagWifi(){this.flagWifi=1;}

    public int getFlagWifi(){
        return this.flagWifi;
    }

    public void resetWifi(){this.flagWifi=0;}

    public SimWifiP2pManager  getmManager() {return this.mManager;}

    public SimWifiP2pManager.Channel  getmChannel() {return this.mChannel;}

    public void setmManager(SimWifiP2pManager manager, SimWifiP2pManager.Channel channel) {
        this.mManager = manager;
        this.mChannel = channel;
    }

    public static DataHolder getInstance() {return holder;}

    private DataHolder(){}
}