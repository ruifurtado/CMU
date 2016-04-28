package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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

        TextView startingPoint = (TextView) findViewById(R.id.sPoint);
        TextView endPoint = (TextView) findViewById(R.id.ePoint);
        TextView distance = (TextView) findViewById(R.id.dist);
        TextView dateText = (TextView) findViewById(R.id.date_front);
        TextView time = (TextView) findViewById(R.id.cTime);

        message="Asking_Trajectory_Info"+","+trajectory;
        CommunicateWithServer();
        info=messageFromServer.split(",");
        startingPoint.setText(info[0]);
        endPoint.setText(info[1]);
        distance.setText(info[2]);
        dateText.setText(info[3]);
        time.setText(info[4]);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng location = new LatLng(38.694996,-9.422548);
        float zoomLevel = 14; //This goes up to 21
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
        map.addMarker(new MarkerOptions().position(location).title("Marker in "+info[1]));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getBaseContext(), Trajectories.class);
        backHome.putExtra("Username", username);
        startActivity(backHome);
    }
}
