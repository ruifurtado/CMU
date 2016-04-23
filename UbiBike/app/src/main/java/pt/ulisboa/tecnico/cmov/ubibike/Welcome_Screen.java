package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class Welcome_Screen extends AppCompatActivity {
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String password;
    private String email;
    private String method="Sign_Up";
    private String messageFromServer;
    private Thread t;

    private SimWifiP2pBroadcastReceiver mReceiver;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private boolean mBound = false;
    private Messenger mService = null;
    private SimWifiP2pSocketServer mSrvSocket = null;

    private DataHolder dataHolder;
    private ArrayList<String> msgList;
    private int flagWifi=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome__screen);

        Intent intent = getIntent();
        try {
            if(intent.getExtras()!=null) {
                Bundle extras=intent.getExtras();
                if(extras.containsKey("Logout"))
                    Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
            }
        }catch(NullPointerException e){}

        dataHolder=DataHolder.getInstance();

        if(dataHolder.getFlagWifi()==0) {
            // initialize the WDSim API
            SimWifiP2pSocketManager.Init(getApplicationContext());
            // register broadcast receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
            filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
            mReceiver = new SimWifiP2pBroadcastReceiver(this);
            registerReceiver(mReceiver, filter);
            Intent intent_aux = new Intent(this, SimWifiP2pService.class);
            //startService(intent);
            bindService(intent_aux, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
            // spawn the chat server background task
            new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            dataHolder.setFlagWifi();
        }


        //Button to go to Sign In activity
        Button sign_in=(Button)findViewById(R.id.sign_in);
        assert sign_in != null;
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataHolder.setmManager(mManager,mChannel);
                Intent goSign_In = new Intent(v.getContext(), Sign_In.class);
                startActivity(goSign_In);
            }
        });

        //Button to go to Sign Up activity
        Button sign_up=(Button)findViewById(R.id.sign_up);
        assert sign_up != null;
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSign_Up = new Intent(v.getContext(), Sign_Up.class);
                startActivityForResult(goSign_Up,0); //start new activity and excepts a result
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //cycle to check if it's a result from the Sign Up activity
        if (resultCode == RESULT_OK && requestCode==0) {
            username = data.getStringExtra("Username");
            email = data.getStringExtra("Email");
            password = data.getStringExtra("Password");
            //IMPORTANT: In this part of the code, the APP have to communicate with the server to pass the new user, with all the data received
            String registerResult=communicateWithServer();
            Toast.makeText(this, registerResult, Toast.LENGTH_SHORT).show();
        }
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
                message=method+","+username+","+email+","+password;
                client = new Socket(serverIp, 4444);  //connect to server
                printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(message);  //write the message to output stream
                printwriter.flush();
                printwriter.close();
                client.close();   //closing the connection

                client = new Socket(serverIp, 4444);  //connect to server
                // Get input buffer
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                messageFromServer = br.readLine();
                br.close();
                client.close();   //closing the connection

            } catch (UnknownHostException  e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver_create = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //itemsAdapter.clear();
            Toast.makeText(getBaseContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
        }
    };

    protected String communicateWithServer (){
        t=new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return messageFromServer;
    }

    /*
	 * Asynctasks implementing message exchange
	 */

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Message_Home: ", "IncommingCommTask started (" + this.hashCode() + ").");
            try {
                mSrvSocket = new SimWifiP2pSocketServer(Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Error socket:", e.getMessage());
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0]!=null) {
                if(values[0].contains("Send_Points")){
                    String [] message = values[0].split(",");
                    Toast.makeText(Welcome_Screen.this, "Received "+message[2]+" points from "+message[1], Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("Points Received");
                    sendBroadcast(intent);
                }
                else {
                    String sender = values[0].substring(0, values[0].indexOf(" "));
                    String message = values[0].substring(values[0].indexOf(" ") + 1);
                    Toast.makeText(Welcome_Screen.this, "New message from " + sender, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("New Message");
                    readItems(sender);
                    msgList.add(message);
                    writeItems(sender);
                    sendBroadcast(intent);
                }
            }
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

    //Code to persists items to a file (read and write to a file)
    private void readItems(String filename) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, filename+".txt");
        try {
            msgList = new ArrayList<>(FileUtils.readLines(file));
        } catch (IOException e) {
            msgList = new ArrayList<>();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    @Override
    public void onBackPressed(){
        unregisterReceiver(mReceiver);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        dataHolder.resetWifi();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        String[] SavedFiles = getApplicationContext().fileList();
        File dir = getFilesDir();
        for (int i = 0; i < SavedFiles.length; i++) {
            File file = new File(dir, SavedFiles[i]);
            file.delete();
        }
    }
}
