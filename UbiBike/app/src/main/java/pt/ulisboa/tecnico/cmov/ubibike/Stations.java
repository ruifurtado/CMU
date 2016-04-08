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

public class Stations extends AppCompatActivity {
    private ListView itemsReservations;
    private ArrayAdapter<String> adapterReservations;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stations);

        //Code to managed the lists and adapters
        items= new ArrayList<String>();
        itemsReservations = (ListView) findViewById(R.id.listBikes);
        adapterReservations = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsReservations.setAdapter(adapterReservations);

        //Code to allow the reading from the server of all available reservations
        adapterReservations.insert("Cascais", 0);

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

                //This values are used only for test proposes
                myIntent.putExtra("localidade", "Cascais");
                myIntent.putExtra("bikes", "6");
                myIntent.putExtra("feedback", "Excellent");
                myIntent.putExtra("coordinates", "38.694996 -9.422548");
                myIntent.putExtra("lat", 38.694996);
                myIntent.putExtra("long",-9.422548);
                startActivity(myIntent);
            }
        });
    }
}
