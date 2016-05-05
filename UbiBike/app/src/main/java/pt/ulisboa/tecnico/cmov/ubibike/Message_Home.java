package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;


public class Message_Home extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener{

    private ListView list;
    private ArrayAdapter<String> adapterItems;
    private ArrayList<String> items;
    private DataHolder dataHolder;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Map<String, String> peersInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message__home);

        items = new ArrayList<>();
        list = (ListView) findViewById(R.id.messagesContainer);
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(adapterItems);

        dataHolder=DataHolder.getInstance();

        mChannel=dataHolder.getmChannel();
        mManager=dataHolder.getmManager();

        //Button to go to Search User to send a message activity and expects a returned username
        Button search_user=(Button)findViewById(R.id.searchButton);
        assert search_user != null;
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search all available friends neer me
                adapterItems.clear();
                mManager.requestPeers(mChannel, Message_Home.this);
            }

        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Intent myIntent = new Intent(item.getContext(), Message_Chat.class);
                String user=(String) adapter.getItemAtPosition(pos);
                myIntent.putExtra("ToUsername", user );
                myIntent.putExtra("Username", getIntent().getStringExtra("Username"));
                myIntent.putExtra("IP",peersInfo.get(user));
                startActivity(myIntent);
            }
        });
    }

    /*
    * Listeners associated to Termite
    */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            adapterItems.insert(device.deviceName, 0);
            peersInfo.put(device.deviceName, device.getVirtIp());
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        //NOTHING TO DO WITH THIS METHOD
    }
}

