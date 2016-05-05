package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class User_Home extends AppCompatActivity {

    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String messageFromServer;
    private BroadcastReceiver broadcastReceiver= null;
    private TextView points_presentation;
    private Thread t2;
    private boolean threadBufferControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user__home);

        //To get the intent, extract the username that perform the login operation and display it on the textView
        Intent data = getIntent();
        username = data.getStringExtra("Username");
        TextView username_presentation = (TextView) findViewById(R.id.username_presentation);
        username_presentation.setText(username);

        points_presentation = (TextView) findViewById(R.id.points_presentation);

        if(DataHolder.getInstance().getFlagSign_In()==0) {

            DataHolder.getInstance().setPoints(Integer.parseInt(data.getStringExtra("Points")));
            points_presentation.setText(""+DataHolder.getInstance().getPoints());
            DataHolder.getInstance().setPointsSender(Integer.parseInt(data.getStringExtra("Points Sender")));
            DataHolder.getInstance().setPointsReceiver(Integer.parseInt(data.getStringExtra("Points Receiver")));
            DataHolder.getInstance().setFlagSign_In();

        }
        else
            points_presentation.setText(""+DataHolder.getInstance().getPoints());

        if(!DataHolder.getInstance().getFlagBuffer()) {
            threadBufferControler=true;
            t2 = new Thread(wipeBuffer, "Send Requests");
            t2.start();
            DataHolder.getInstance().setFlagBuffer();
        }

        IntentFilter filter = new IntentFilter("Points Received");
        broadcastReceiver = broadcastReceiver_create;
        registerReceiver(broadcastReceiver, filter);

        //Button to go to Send Points activity
        Button send_points=(Button)findViewById(R.id.send_points);
        assert send_points != null;
        send_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goSend_points = new Intent(v.getContext(), Send_Points.class);
                goSend_points.putExtra("Username",username);
                startActivity(goSend_points);
            }
        });

        //Button to go to Trajectories activity
        final Button go_trajectories=(Button)findViewById(R.id.trajectory);
        assert go_trajectories != null;
        go_trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goTrajectories = new Intent(v.getContext(), Trajectories.class);
                goTrajectories.putExtra("Username",username);
                startActivity(goTrajectories);
            }
        });

        //Button to go to Stations activity
        Button go_Stations=(Button)findViewById(R.id.booking_bike);
        assert go_Stations != null;
        go_Stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goStations = new Intent(v.getContext(), Stations.class);
                goStations.putExtra("Username",username);
                startActivity(goStations);
            }
        });

        Button go_Messages=(Button)findViewById(R.id.send_message);
        assert go_Messages != null;
        go_Messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goMessages = new Intent(v.getContext(), Message_Home.class);
                goMessages.putExtra("Username",username);
                startActivity(goMessages);
            }
        });
    }

    private final BroadcastReceiver broadcastReceiver_create = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            points_presentation.setText(""+DataHolder.getInstance().getPoints());
        }
    };

    @Override
    public void onPause(){
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        }catch(IllegalArgumentException e){}
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DataHolder.getInstance().resetFalgBuffer();
        threadBufferControler=false;
        Intent backHome = new Intent(User_Home.this,Welcome_Screen.class);
        backHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        backHome.putExtra("Logout","Logout");
        startActivity(backHome);
    }

    Runnable wipeBuffer = new Runnable() {
        @Override
        public void run() {
            while (threadBufferControler) {
                try {
                    if (DataHolder.getInstance().bufferHaveSomeRequest()) {
                        ArrayList<String> buffer_aux = DataHolder.getInstance().getBuffer();
                        int counterRequest=0;
                        for (String aux : buffer_aux) {
                            message = aux;
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
                            Log.d("Thread Buffer", messageFromServer);
                            counterRequest++;
                        }
                        for(int i=0; i<counterRequest; i++){
                            buffer_aux.remove(0);
                        }
                        Log.d("Thread Buffer", "Buffer Clear. Waiting for more request!");
                        t2.sleep(60000);
                    } else {
                        Log.d("Thread Buffer", "No Requests. Waiting for more request!");
                        t2.sleep(60000);
                    }
                    //message=method+","+username+","+email+","+password;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("Thread Buffer","Server Unreachable!");
                    try {
                        t2.sleep(60000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
