package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Stations extends AppCompatActivity {

    private ListView itemsReservations;
    private ArrayAdapter<String> adapterReservations;
    private ArrayList<String> items;
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String messageFromServer;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stations);

        username=getIntent().getStringExtra("Username");

        //Code to managed the lists and adapters
        items= new ArrayList<String>();
        itemsReservations = (ListView) findViewById(R.id.listBikes);
        adapterReservations = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsReservations.setAdapter(adapterReservations);

        //Code to allow the reading from the server of all available reservations (with bikes available)
        Button go_Messages=(Button)findViewById(R.id.search_reservations);
        assert go_Messages != null;
        go_Messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message="Asking_Stations";
                CommunicateWithServer();
                String [] stations = messageFromServer.split(",");
                adapterReservations.clear();
                for(int i=0; i< stations.length; i++){
                    adapterReservations.insert(stations[i], 0);
                }
            }
        });
        //IMPORTANT: When we click on the update button, the app have to communicate with the server to check all available reservations

        // Method to Select a users from the List and return
        setupListViewListener();
    }

    private void setupListViewListener(){
        itemsReservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //Code to read from the server all the data related to the selected reservation and go to the Confirm Reservation activity
                Intent myIntent = new Intent(item.getContext(), Station_Description.class);
                myIntent.putExtra("Station", (String) adapter.getItemAtPosition(pos));
                myIntent.putExtra("Username", getIntent().getStringExtra("Username"));
                startActivity(myIntent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getBaseContext(),User_Home.class);
        backHome.putExtra("Username", username);
        startActivity(backHome);
    }
}
