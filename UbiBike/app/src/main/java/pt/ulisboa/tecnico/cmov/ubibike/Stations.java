package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Stations extends AppCompatActivity implements OnMapReadyCallback {

    private Socket client;
    private PrintWriter printwriter;
    ArrayList<String> array = new ArrayList<String>();
    private String message;
    private String serverIp = "10.0.2.2";
    private String messageFromServer;
    private Thread t;
    private GoogleMap mMap;
    private double lat;
    private double longi;
    private String[] latlong;
    private String username;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_stations);
        username=getIntent().getStringExtra("Username");

        Button refresh = (Button)this.findViewById(R.id.refresh);
        assert refresh != null;
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message="Station_Coord";
                CommunicateWithServer();
                update();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void CommunicateWithServer() {
        t = new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update() {

        message="Station_Coord";
        CommunicateWithServer();
        //[0] -> number of bikes available ; [1] -> latitude ; [2] -> longitude ; [3] -> feedback
        if(!messageFromServer.equals("IOException")) {
            String[] stations = messageFromServer.split(",");
            array.clear();
            final LatLng parede = new LatLng(38.693449, -9.355405);
            for (int i = 0; i < stations.length; i++) {
                array.add(stations[i]);
                latlong = array.get(i).split(";");
                lat = Double.parseDouble(latlong[1]);
                longi = Double.parseDouble(latlong[2]);
                final LatLng location = new LatLng(lat, longi);
                float zoomLevel = 12; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parede, zoomLevel));
                mMap.addMarker(new MarkerOptions().position(location).title("Estação de " + latlong[0]));
                mMap.setOnInfoWindowClickListener(getInfoWindowClickListener());
            }
        }
        else
            Toast.makeText(this, "Server Unreachable! Please Try Later!", Toast.LENGTH_SHORT).show();

    }

    public GoogleMap.OnInfoWindowClickListener getInfoWindowClickListener()
    {
        return new GoogleMap.OnInfoWindowClickListener()
        {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                Intent intent1= new Intent(getApplicationContext(), Station_Description.class);
                String[] name=marker.getTitle().split(" ",3);
                intent1.putExtra("Station", name[2]);
                intent1.putExtra("Username",username);
                startActivity(intent1);
            }
        };
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
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Communicating Server","Server Unreachable! Please Try Later!");
                messageFromServer="IOException";
                e.printStackTrace();
            }
        }
    };

    // LEVEL: 0; physical button to the previous activity. not related with the programming.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getApplicationContext(), User_Home.class);
        backHome.putExtra("Username", username);
        startActivity(backHome);
    }

    // LEVEL: 0; Android studio shit when the activity starts!
    @Override
    public void onStart() {
        super.onStart();
        // LEVEL: 0; Android studio shit when the activity starts!
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Stations Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pt.ulisboa.tecnico.cmov.ubibike/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    // LEVEL: 0; physical button to killing a program. not related with the programming.
    @Override
    public void onStop() {
        super.onStop();
        // LEVEL: 0; physical button to killing a program. not related with the programming.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Stations Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pt.ulisboa.tecnico.cmov.ubibike/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    // LEVEL: 1; what the maps part of the application is goin to go when the map is shown
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
