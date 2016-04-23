package pt.ulisboa.tecnico.cmov.ubibike;

import java.util.HashMap;
import java.util.Map;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class DataHolder {

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private static final DataHolder holder = new DataHolder();
    private int flagWifi=0;


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
}
