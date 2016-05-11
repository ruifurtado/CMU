package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Read_Trajectory extends AppCompatActivity implements OnMapReadyCallback {

    private String trajectory;
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String messageFromServer;
    private Thread t;
    private GoogleMap map;
    private String info[];
    private ArrayList<Double> trajectoryPoints =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read__trajectory);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);

        username=getIntent().getStringExtra("Username");
        //Display all the information about the trajectory, that was passed on the received intent
        Intent intent = getIntent();
        TextView title=(TextView) findViewById(R.id.trajectoryName);
        trajectory=intent.getStringExtra("Trajectory");
        title.setText(trajectory);

        TextView earnedPoints = (TextView) findViewById(R.id.ePoint);
        TextView distance = (TextView) findViewById(R.id.dist);


        message="Asking_Trajectory_Info"+","+trajectory+","+username;
        CommunicateWithServer();
        info=messageFromServer.split(",");
        //startingPoint.setText(info[0]);
        //endPoint.setText(info[1]);
        distance.setText(info[0]+" KM");
        earnedPoints.setText(info[2]);
        String points="";
        for (int i=3;i<info.length;i++) {
            points =points+info[i]+",";
        }
        arrayCreator(points);
        Log.d("ARRAY:",trajectoryPoints.toString());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        PolylineOptions rectOptions = new PolylineOptions();
        for (int i=0;i<trajectoryPoints.size()-1;i++){
            rectOptions.add(new LatLng(trajectoryPoints.get(i),trajectoryPoints.get(i+1)));
            i++;
        }
        Polyline polyline=map.addPolyline(rectOptions);
        //LatLng location = new LatLng(38.694996,-9.422548);
        float zoomLevel = 13; //This goes up to 21
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(trajectoryPoints.get(0),trajectoryPoints.get(1)), zoomLevel));
        map.addMarker(new MarkerOptions().position(new LatLng(trajectoryPoints.get(0),trajectoryPoints.get(1))).title("Starting Point"));
        map.addMarker(new MarkerOptions().position(new LatLng(trajectoryPoints.get(trajectoryPoints.size()-2),trajectoryPoints.get(trajectoryPoints.size()-1))).title("End Point"));
        //map.moveCamera(CameraUpdateFactory.newLatLng(location));
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

    public void arrayCreator (String array){
        String [] splitter=array.split(",");
        for (int i=0;i<splitter.length;i++){
            trajectoryPoints.add(Double.parseDouble(splitter[i]));
        }



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getBaseContext(), Trajectories.class);
        backHome.putExtra("Username", username);
        startActivity(backHome);
    }
}