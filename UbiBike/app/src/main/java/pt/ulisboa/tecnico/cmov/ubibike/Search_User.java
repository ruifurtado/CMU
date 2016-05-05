package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class Search_User extends AppCompatActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private ListView itemsUsernames;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;
    private DataHolder dataHolder;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Map<String, String> peersInfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search__user);

        // List and Adapter Code Here
        items = new ArrayList<>();
        itemsUsernames = (ListView) findViewById(R.id.list_recentlyUsed);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsUsernames.setAdapter(itemsAdapter);

        dataHolder=DataHolder.getInstance();
        mChannel=dataHolder.getmChannel();
        mManager=dataHolder.getmManager();

        mManager.requestPeers(mChannel, this);

        // Method to Select a users from the List and return
        setupListViewListener();
    }

    /*
    * Listeners associated to Termite
    */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            itemsAdapter.insert(device.deviceName, 0);
            peersInfo.put(device.deviceName, device.getVirtIp());
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo) {
        //NOTHING TO DO WITH THIS METHOD
    }

    //Code of the method to allow the removing items from the list
    private void setupListViewListener() {
        itemsUsernames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //Save the selected username and returned to the Send Points activity
                String username = (String) adapter.getItemAtPosition(pos);

                Intent search_result = new Intent();
                search_result.putExtra("Username", username);
                search_result.putExtra("IP", peersInfo.get(username));
                setResult(Activity.RESULT_OK, search_result);
                finish();
            }
        });
    }
}
