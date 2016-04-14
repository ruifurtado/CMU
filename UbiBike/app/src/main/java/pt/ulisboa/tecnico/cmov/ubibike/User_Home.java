package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class User_Home extends AppCompatActivity {
    private String[] SavedFiles;
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String method="Asking_Points";
    private String messageFromServer;
    private Thread t;

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

        CommunicateWithServer();
        TextView points_presentation = (TextView) findViewById(R.id.points_presentation);
        points_presentation.setText(messageFromServer);

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
        Button go_trajectories=(Button)findViewById(R.id.trajectory);
        assert go_trajectories != null;
        go_trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTrajectories = new Intent(v.getContext(), Trajectories.class);
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
                startActivity(goStations);
            }
        });

        Button go_Messages=(Button)findViewById(R.id.send_message);
        assert go_Messages != null;
        go_Messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMessages = new Intent(v.getContext(), Message_Home.class);
                startActivity(goMessages);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SavedFiles = getApplicationContext().fileList();
        File dir = getFilesDir();
        for(int i=0;i<SavedFiles.length;i++) {
            File file = new File(dir, SavedFiles[i]);
            file.delete();
        }
        Intent logout = new Intent(getApplicationContext(),Welcome_Screen.class);
        logout.putExtra("Logout", "Logout");
        startActivity(logout);
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
                message=method+" "+username;
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
}
