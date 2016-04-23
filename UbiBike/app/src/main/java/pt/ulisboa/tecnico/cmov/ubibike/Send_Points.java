package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class Send_Points extends AppCompatActivity {

    private TextView usernameTextView;
    private TextView points_sent;
    private String username;
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String messageFromServer;
    private Thread t;

    private String ipDevice;
    private SimWifiP2pSocket mCliSocket = null;
    private BroadcastReceiver broadcastReceiver= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_send__points);

        username=getIntent().getStringExtra("Username");

        //To display the selected username that will received some points
        usernameTextView= (TextView) findViewById(R.id.username_toSent2);
        usernameTextView.setText("");

        //update all text views about points
        updateAllTextViews();

        IntentFilter filter = new IntentFilter("Points Received");
        broadcastReceiver = broadcastReceiver_create;
        registerReceiver(broadcastReceiver, filter);

        //Button to go to Search User activity and expects a returned username
        Button search_user=(Button)findViewById(R.id.search);
        assert search_user != null;
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSearch_user = new Intent(v.getContext(), Search_User.class);
                startActivityForResult(goSearch_user, 0); //start new activity and excepts a result
            }
        });

        //Button to go to Search User activity and expects a returned username
        Button send_points=(Button)findViewById(R.id.send);
        assert send_points != null;
        send_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Write the points the will be send and after a click, update the amount of points send
                EditText points_toSend = (EditText) findViewById(R.id.points_toSent2);
                String points = points_toSend.getText().toString();
                Integer point = 0;
                try {
                    point = Integer.parseInt(points);
                    message="Send_Points"+","+username+ ","+points+","+usernameTextView.getText();
                    CommunicateWithServer();
                    Toast.makeText(Send_Points.this, messageFromServer, Toast.LENGTH_SHORT).show();

                    new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,message);

                } catch(NumberFormatException e) {
                    Toast.makeText(Send_Points.this, "Write a valid amount of points", Toast.LENGTH_SHORT).show();
                }
                updateAllTextViews();
                points_toSend.setText("");
                usernameTextView.setText("");
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver_create = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //itemsAdapter.clear();
            updateAllTextViews();
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //cycle to check if it's a result from the Search User activity
        if (resultCode == RESULT_OK && requestCode==0) {

            //Display the selected username on the textView available
            usernameTextView.setText(data.getStringExtra("Username"));
            ipDevice=data.getStringExtra("IP");
        }
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
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

            } catch (UnknownHostException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    };

    protected void CommunicateWithServer (){
        t=new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    public void updateAllTextViews(){
        message="Asking_AllPoints"+","+username;
        CommunicateWithServer();

        //[0] - total points ; [1] - total points send ; [2] - total points received
        String [] allpoints=messageFromServer.split(" ");

        //To display the total amount of points that the user sent to friends
        points_sent= (TextView) findViewById(R.id.points2);
        points_sent.setText(allpoints[0]);

        //To display the total amount of points of the user
        points_sent= (TextView) findViewById(R.id.points_sent2);
        points_sent.setText(allpoints[1]);

        //To display the total amount of points of the user
        points_sent= (TextView) findViewById(R.id.points_received2);
        points_sent.setText(allpoints[2]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unregisterReceiver(broadcastReceiver);
        Intent backHome = new Intent(getApplicationContext(),User_Home.class);
        backHome.putExtra("Username", username);
        startActivity(backHome);
    }
}
