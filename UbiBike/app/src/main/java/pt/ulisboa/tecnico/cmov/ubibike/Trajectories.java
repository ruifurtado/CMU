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
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Trajectories extends AppCompatActivity {

    private ListView itemsTrajectories;
    private ArrayAdapter<String> adapterTrajectories;
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
        setContentView(R.layout.activity_trajectories);

        username=getIntent().getStringExtra("Username");

        //Code to managed the lists and adapters
        items= new ArrayList<>();
        itemsTrajectories = (ListView) findViewById(R.id.listTrajectories);
        adapterTrajectories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsTrajectories.setAdapter(adapterTrajectories);

        //Code to allow the reading from the server of all available trajectories
        Button go_Trajectories=(Button)findViewById(R.id.updateTrajectoryButton);
        assert go_Trajectories != null;
        go_Trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message="Asking_Trajectories"+","+username;
                CommunicateWithServer();
                String msg=messageFromServer;
                if(msg.equals("Not_available_trajectories")){
                    Toast.makeText(Trajectories.this, "You don't have any trajectory! Do more exercise!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    String[] trajectories = messageFromServer.split(",");
                    adapterTrajectories.clear();
                    for (int i = 0; i < trajectories.length; i++) {
                        adapterTrajectories.insert(trajectories[i], 0);
                    }
                }
            }
        });
        // Method to Select a users from the List and return
        setupListViewListener();
    }

    private void setupListViewListener(){
        itemsTrajectories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //Code to read from the server all the data related to the selected trajectory and go to the Read Trajectory activity
                Intent myIntent = new Intent(item.getContext(), Read_Trajectory.class);
                myIntent.putExtra("Trajectory", (String) adapter.getItemAtPosition(pos));
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
        Intent backHome = new Intent(getApplicationContext(),User_Home.class);
        backHome.putExtra("Username",username);
        startActivity(backHome);
    }
}