package pt.ulisboa.tecnico.cmov.ubibike;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private int peersChanged=0;
    private boolean stationPastState = false;
    private boolean bikePastState = false;

    public boolean isStationPastState() {
        return stationPastState;
    }

    public void setStationPastState(boolean stationPastState) {
        this.stationPastState = stationPastState;
    }

    public boolean isBikePastState() {
        return bikePastState;
    }

    public void setBikePastState(boolean bikePastState) {
        this.bikePastState = bikePastState;
    }


    public void generateKeys(){
        try {
            //initialize key generator
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            //generate key pair
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public PublicKey  getPublickey(){return this.publicKey;}
    public PrivateKey getPrivateKey(){return this.privateKey;}


    private Map<String, Integer> messagePointsIDGenerator = new HashMap<>();
    private Map<String, Integer> messagePointsIDControler = new HashMap<>();
    private String usernameLogin;

    public void setUsernameLogin(String username){this.usernameLogin=username;}

    public String getUsernameLogin(){return this.usernameLogin;}

    public Integer getMessagePointsIDGenerator(String key){
        if(this.messagePointsIDGenerator.containsKey(key))
            return this.messagePointsIDGenerator.get(key);
        else{
            this.messagePointsIDGenerator.put(key,1);
            return this.messagePointsIDGenerator.get(key);
        }
    }

    public void incrementMessagePointsIDGenerator(String key){
        this.messagePointsIDGenerator.put(key,this.messagePointsIDGenerator.get(key)+1);
    }

    public Integer getMessagePointsIDController(String key){
        if(this.messagePointsIDControler.containsKey(key))
            return this.messagePointsIDControler.get(key);
        else{
            this.messagePointsIDControler.put(key,1);
            return this.messagePointsIDControler.get(key);
        }
    }

    public void incrementMessagePointsIDController(String key){
        this.messagePointsIDControler.put(key,this.messagePointsIDControler.get(key)+1);
    }

    public void setPeersChanged (){
        this.peersChanged=1;
    }

    public void resetPeersChenged (){
        this.peersChanged=0;
    }

    public int getPeersChanged(){
        return this.peersChanged;
    }



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

    public void updatePoints(int points){
        this.points+=points;
    }

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