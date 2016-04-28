package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Station_Description extends AppCompatActivity {

    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String messageFromServer;
    private Thread t;
    private Intent intent;
    private String[] stationInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_station__description);

        //Get the intent
        intent = getIntent();

        //Display all the information about the Station, that was passed on the received intent
        TextView location = (TextView) findViewById(R.id.location_front);
        location.setText(intent.getStringExtra("Station"));

        updateStationInformation();

        //Button to Book a Bike
        Button book_bike=(Button)findViewById(R.id.book_bike);
        assert book_bike != null;
        book_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message="Book_Bike"+","+intent.getStringExtra("Station");
                CommunicateWithServer();
                Toast.makeText(Station_Description.this, messageFromServer, Toast.LENGTH_SHORT).show();
                updateStationInformation();
            }
        });
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

    public void updateStationInformation(){
        message="Station_Description"+","+intent.getStringExtra("Station");
        CommunicateWithServer();
        //[0] -> number of bikes available ; [1] -> latitude ; [2] -> longitude ; [3] -> feedback
        stationInformation=messageFromServer.split(",");

        TextView bikes = (TextView) findViewById(R.id.bikes_front);
        bikes.setText(stationInformation[0]);
        TextView coordinates = (TextView) findViewById(R.id.coordinates_front);
        coordinates.setText(stationInformation[1]+" "+stationInformation[2]);
        TextView feedback = (TextView) findViewById(R.id.feedback_front);
        feedback.setText(stationInformation[3]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getBaseContext(),Stations.class);
        backHome.putExtra("Username", getIntent().getStringExtra("Username"));
        startActivity(backHome);
    }
}
