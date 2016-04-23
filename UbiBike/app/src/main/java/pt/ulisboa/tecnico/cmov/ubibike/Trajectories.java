package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class Trajectories extends AppCompatActivity {

    private ListView itemsTrajectories;
    private ArrayAdapter<String> adapterTrajectories;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trajectories);

        //Code to managed the lists and adapters
        items= new ArrayList<>();
        itemsTrajectories = (ListView) findViewById(R.id.listTrajectories);
        adapterTrajectories = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsTrajectories.setAdapter(adapterTrajectories);

        //Code to allow the reading from the server of all available trajectories
        adapterTrajectories.insert("Trajectoria1", 0); //Static value only for test proposes

        // Method to Select a users from the List and return
        setupListViewListener();
    }

    private void setupListViewListener(){
        itemsTrajectories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //Code to read from the server all the data related to the selected trajectory and go to the Read Trajectory activity
                Intent myIntent = new Intent(item.getContext(), Read_Trajectory.class);

                //This values are used only for test proposes
                myIntent.putExtra("begin", "Lisboa");
                myIntent.putExtra("end", "Tagus");
                myIntent.putExtra("dist", "30km");
                myIntent.putExtra("date", "08-04-2016 11:51:18");
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getApplicationContext(),User_Home.class);
        backHome.putExtra("Username", getIntent().getStringExtra("Username"));
        startActivity(backHome);
    }
}
