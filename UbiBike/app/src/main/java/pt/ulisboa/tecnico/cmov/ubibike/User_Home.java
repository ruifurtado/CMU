package pt.ulisboa.tecnico.cmov.ubibike;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;

public class User_Home extends AppCompatActivity  implements SimWifiP2pManager.PeerListListener, LocationListener {

    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp = "10.0.2.2";
    private String username;
    private String messageFromServer;
    private BroadcastReceiver broadcastReceiver = null;
    private BroadcastReceiver broadcastReceiver2 = null;

    private TextView points_presentation;
    private Thread t2;
    private boolean threadBufferController;
    //private ArrayList<String> peersAtualState = new ArrayList<>();
    //private ArrayList<String> peersPastState = new ArrayList<>();
    private ArrayList<String> coordinates=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user__home);

        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);

        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);


        //To get the intent, extract the username that perform the login operation and display it on the textView
        Intent data = getIntent();
        username = data.getStringExtra("Username");
        TextView username_presentation = (TextView) findViewById(R.id.username_presentation);
        username_presentation.setText(username);
        DataHolder.getInstance().setUsernameLogin(username);

        DataHolder.getInstance().generateKeys();
        communicateWithServer();

        points_presentation = (TextView) findViewById(R.id.points_presentation);

        if (DataHolder.getInstance().getFlagSign_In() == 0) {

            DataHolder.getInstance().setPoints(Integer.parseInt(data.getStringExtra("Points")));
            points_presentation.setText("" + DataHolder.getInstance().getPoints());
            DataHolder.getInstance().setPointsSender(Integer.parseInt(data.getStringExtra("Points Sender")));
            DataHolder.getInstance().setPointsReceiver(Integer.parseInt(data.getStringExtra("Points Receiver")));
            DataHolder.getInstance().setFlagSign_In();

        } else
            points_presentation.setText("" + DataHolder.getInstance().getPoints());

        if (!DataHolder.getInstance().getFlagBuffer()) {
            threadBufferController = true;
            t2 = new Thread(wipeBuffer, "Send Requests");
            t2.start();
            DataHolder.getInstance().setFlagBuffer();
        }

        IntentFilter filter = new IntentFilter("Points Received");
        broadcastReceiver = broadcastReceiver_create;
        registerReceiver(broadcastReceiver, filter);

        if (DataHolder.getInstance().getPeersChanged()==0) {
            IntentFilter filter2 = new IntentFilter("Peers Changed");
            broadcastReceiver2 = broadcastReceiver_create2;
            registerReceiver(broadcastReceiver2, filter2);
            DataHolder.getInstance().setPeersChanged();
        }

        //Button to go to Send Points activity
        Button send_points = (Button) findViewById(R.id.send_points);
        assert send_points != null;
        send_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goSend_points = new Intent(v.getContext(), Send_Points.class);
                goSend_points.putExtra("Username", username);
                startActivity(goSend_points);
            }
        });

        //Button to go to Trajectories activity
        final Button go_trajectories = (Button) findViewById(R.id.trajectory);
        assert go_trajectories != null;
        go_trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goTrajectories = new Intent(v.getContext(), Trajectories.class);
                goTrajectories.putExtra("Username", username);
                startActivity(goTrajectories);
            }
        });

        //Button to go to Stations activity
        Button go_Stations = (Button) findViewById(R.id.booking_bike);
        assert go_Stations != null;
        go_Stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goStations = new Intent(v.getContext(), Stations.class);
                goStations.putExtra("Username", username);
                startActivity(goStations);
            }
        });

        Button go_Messages = (Button) findViewById(R.id.send_message);
        assert go_Messages != null;
        go_Messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goMessages = new Intent(v.getContext(), Message_Home.class);
                goMessages.putExtra("Username", username);
                startActivity(goMessages);
            }
        });
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
                byte[] myPublicKeyBytes = DataHolder.getInstance().getPublickey().getEncoded();
                String myPublicKey = Base64.encodeToString(myPublicKeyBytes, Base64.DEFAULT);
                message="PublicKey"+","+username+","+myPublicKey;
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
                Log.d("PublicKey ACK", messageFromServer);
                br.close();
                client.close();   //closing the connection

            } catch (UnknownHostException  e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    };


    protected String communicateWithServer (){
        Thread t=new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return messageFromServer;
    }

    private final BroadcastReceiver broadcastReceiver_create = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            points_presentation.setText("" + DataHolder.getInstance().getPoints());
        }
    };

    private final BroadcastReceiver broadcastReceiver_create2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataHolder.getInstance().getmManager().requestPeers(DataHolder.getInstance().getmChannel(), User_Home.this);
        }
    };

    /*
    * Listeners associated to Termite
    */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        boolean stationAtualState = false;
        boolean bikeAtualState = false;
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            if (device.deviceName.contains("Estação"))
                stationAtualState = true;
            if (device.deviceName.contains("Bike"))
                bikeAtualState = true;

        }
        if (DataHolder.getInstance().isStationPastState() == false && DataHolder.getInstance().isBikePastState() == false && stationAtualState == true && bikeAtualState == false) {
            Toast.makeText(User_Home.this, "Arrived station", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setStationPastState(true);
        }
        if (DataHolder.getInstance().isStationPastState() == false &&  DataHolder.getInstance().isBikePastState() == false && stationAtualState == true && bikeAtualState == true) {
            Toast.makeText(User_Home.this, "Arrived station and bike picked-up", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setStationPastState(true);
            DataHolder.getInstance().setBikePastState(true);
        }
        if (DataHolder.getInstance().isStationPastState() == true &&  DataHolder.getInstance().isBikePastState() == false && stationAtualState == true && bikeAtualState == true) {
            Toast.makeText(User_Home.this, "Bike picked-up", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setBikePastState(true);
        }
        if (DataHolder.getInstance().isStationPastState() == true &&  DataHolder.getInstance().isBikePastState() == true && stationAtualState == false && bikeAtualState == true) {
            Toast.makeText(User_Home.this, "Ready to start the trajectory. Enjoy your ride!", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setStationPastState(false);
            //LANÇAR METODO PARA INICIAR A TRAJECTORY
        }
        if (DataHolder.getInstance().isStationPastState() == false &&  DataHolder.getInstance().isBikePastState() == true && stationAtualState == false && bikeAtualState == false) {
            Toast.makeText(User_Home.this, "Bike dropped anywhere!", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setBikePastState(false);
            //adicionar trajectory ao buffer p mandar ao server
            Calendar c=Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = format.format(c.getTime());
            double firstLatitude = Double.parseDouble(coordinates.get(1));  //Esta em string
            double firstLongitude = Double.parseDouble(coordinates.get(0));  //Esta em string
            double lastLongitude = Double.parseDouble(coordinates.get(coordinates.size()-2));  //esta em string
            double lastLatitude = Double.parseDouble(coordinates.get(coordinates.size()-1));  //esta em string
            String distance = String.valueOf(haversine(firstLatitude,firstLongitude,lastLatitude,lastLongitude));
            String points = assignPointsToDistance(haversine(firstLatitude,firstLongitude,lastLatitude,lastLongitude));
            String messageToBuffer="Trajectory_Sender"+","+username+","+date+","+distance+","+points+","+coordinates.toString().substring(1,coordinates.toString().length()-1);
            Log.d("Message To Add Buffer", messageToBuffer);
            DataHolder.getInstance().addRequest(messageToBuffer);
            coordinates.clear();
        }

        if (DataHolder.getInstance().isStationPastState() == false &&  DataHolder.getInstance().isBikePastState() == false && stationAtualState == false && bikeAtualState == true) {
            Toast.makeText(User_Home.this, "Bike picked-up anywhere!", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setBikePastState(true);
            //Apanhar bike rua e nova trajectorua
            //LANÇAR METODO PARA INICIAR A TRAJECTORY
        }

        if (DataHolder.getInstance().isStationPastState() == false &&  DataHolder.getInstance().isBikePastState() == true && stationAtualState == true && bikeAtualState == true) {
            Toast.makeText(User_Home.this, "Arrived station", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setStationPastState(true);
        }
        if (DataHolder.getInstance().isStationPastState() == true &&  DataHolder.getInstance().isBikePastState() == true && stationAtualState == true && bikeAtualState == false) {
            Toast.makeText(User_Home.this, "Bike dropped. Thank you for using our service!", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setBikePastState(false);
            //adicionar trajectory ao buffer p mandar ao server
            Calendar c=Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = format.format(c.getTime());
            double firstLatitude = Double.parseDouble(coordinates.get(1));  //Esta em string
            double firstLongitude = Double.parseDouble(coordinates.get(0));  //Esta em string
            double lastLongitude = Double.parseDouble(coordinates.get(coordinates.size()-2));  //esta em string
            double lastLatitude = Double.parseDouble(coordinates.get(coordinates.size()-1));  //esta em string
            String distance = String.valueOf(haversine(firstLatitude,firstLongitude,lastLatitude,lastLongitude));
            String points = assignPointsToDistance(haversine(firstLatitude,firstLongitude,lastLatitude,lastLongitude));
            String messageToBuffer="Trajectory_Sender"+","+username+","+date+","+distance+","+points+","+coordinates.toString().substring(1,coordinates.toString().length()-1);
            Log.d("Message To Add Buffer", messageToBuffer);
            DataHolder.getInstance().addRequest(messageToBuffer);
            coordinates.clear();
        }
        if (DataHolder.getInstance().isStationPastState() == true &&  DataHolder.getInstance().isBikePastState() == false && stationAtualState == false && bikeAtualState == false) {
            Toast.makeText(User_Home.this, "Bye Bye!", Toast.LENGTH_SHORT).show();
            DataHolder.getInstance().setStationPastState(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    public String assignPointsToDistance (double dist){
        int receivedPoints=(int) Math.round(dist)*10;
        DataHolder.getInstance().updatePoints(receivedPoints);
        Intent broadcastIntent = new Intent("Points Received");
        sendBroadcast(broadcastIntent);
        points_presentation.setText(""+DataHolder.getInstance().getPoints());
        return ""+receivedPoints;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver2);
        } catch (IllegalArgumentException e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DataHolder.getInstance().resetFalgBuffer();
        threadBufferController = false;
        DataHolder.getInstance().resetPeersChenged();
        Intent backHome = new Intent(User_Home.this, Welcome_Screen.class);
        backHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        backHome.putExtra("Logout", "Logout");
        startActivity(backHome);
    }

    Runnable wipeBuffer = new Runnable() {
        @Override
        public void run() {
            while (threadBufferController) {
                try {
                    if (DataHolder.getInstance().bufferHaveSomeRequest()) {
                        ArrayList<String> buffer_aux = DataHolder.getInstance().getBuffer();
                        int counterRequest = 0;
                        for (String aux : buffer_aux) {
                            message = aux;
                            Log.d("Message Server",message);
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
                        for (int i = 0; i < counterRequest; i++) {
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
                    Log.d("Thread Buffer", "Server Unreachable!");
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GPS", "Location Changed " + location.toString());
        coordinates.add(Math.round(location.getLongitude() * 100000.0) / 100000.0 + "");
        coordinates.add(Math.round(location.getLatitude() * 100000.0) / 100000.0 + "");
        //stations = DataHolder.getInstance().getStationsArray();
        //trajectoryManager(latitude, longitude);
        //Log.d("coordenadas", coordinates.toString());
        //Log.d("estacoes", stations.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        d=Math.round(d * 100.0) / 100.0;
        return d;
    }
}
