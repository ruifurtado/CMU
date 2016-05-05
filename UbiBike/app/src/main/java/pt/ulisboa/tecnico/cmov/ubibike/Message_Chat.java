package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.BroadcastReceiver;
import android.content.Context;
import org.apache.commons.io.FileUtils;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class Message_Chat extends AppCompatActivity {

    private static final String TAG = "Message_ChatActivity";
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    private String ipDevice;
    private SimWifiP2pSocket mCliSocket = null;
    private BroadcastReceiver broadcastReceiver= null;
    private String friendName;
    private ArrayList<String> msgList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message__chat);

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);


        friendName = getIntent().getStringExtra("ToUsername");
        readItems(friendName);

        //ip of the device that corresponds to this user
        ipDevice=getIntent().getStringExtra("IP");

        IntentFilter filter = new IntentFilter("New Message");
        broadcastReceiver = broadcastReceiver_create;
        registerReceiver(broadcastReceiver, filter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        //side = !side;
        new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,getIntent().getStringExtra("Username")+" "+chatText.getText().toString());
        msgList.add("R "+chatText.getText().toString());
        writeItems(friendName);
        chatText.setText("");
        return true;
    }

    private final BroadcastReceiver broadcastReceiver_create = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readItems(friendName);
        }
    };

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                if(mCliSocket==null)
                    mCliSocket = new SimWifiP2pSocket(ipDevice, Integer.parseInt(getString(R.string.port)));
                Log.d("Message", msg[0]);
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Nothing to do in this method
        }
    }

    //Code to persists items to a file (read and write to a file)
    private void readItems(String filename) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, filename+".txt");
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);
        try {
            msgList = new ArrayList<>(FileUtils.readLines(file));
            for(int i=0; i<msgList.size(); i++){
                if(msgList.get(i).contains("R")){
                    String message= msgList.get(i).substring(msgList.get(i).indexOf(" ") + 1);
                    chatArrayAdapter.add(new ChatMessage(false, message));
                }
                else{
                    String message= msgList.get(i).substring(msgList.get(i).indexOf(" ") + 1);
                    chatArrayAdapter.add(new ChatMessage(true, message));
                }
            }
        } catch (IOException e) {
            msgList = new ArrayList<>();
        }
    }

    //Code to persists items to a file (read and write to a file)
    private void writeItems(String filename) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, filename+".txt");
        try {
            FileUtils.writeLines(file, msgList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        unregisterReceiver(broadcastReceiver);
    }
}
