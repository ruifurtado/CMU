package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Station_Description extends AppCompatActivity {
    private int bikes_nr;
    private TextView bikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_station__description);

        //Get the intent
        Intent intent = getIntent();

        //Display all the information about the Station, that was passed on the received intent
        TextView location = (TextView) findViewById(R.id.location_front);
        location.setText(intent.getStringExtra("localidade"));

        bikes = (TextView) findViewById(R.id.bikes_front);
        bikes.setText(intent.getStringExtra("bikes"));
        //Static value only for test proposes
        String nrBikes=intent.getStringExtra("bikes");
        bikes_nr=Integer.parseInt(nrBikes);

        TextView coordinates = (TextView) findViewById(R.id.coordinates_front);
        coordinates.setText(intent.getStringExtra("coordinates"));
        TextView feedback = (TextView) findViewById(R.id.feedback_front);
        feedback.setText(intent.getStringExtra("feedback"));

        //Button to Book a Bike
        Button book_bike=(Button)findViewById(R.id.book_bike);
        assert book_bike != null;
        book_bike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IMPORTANT: The number of bikes available has to be updated on the server every time
                if (bikes_nr != 0) {
                    bikes_nr--;
                    Toast.makeText(Station_Description.this, "Successful Booking", Toast.LENGTH_SHORT).show();
                    bikes.setText("" + bikes_nr);
                } else
                    Toast.makeText(Station_Description.this, "No more bikes available! Please check in another Station", Toast.LENGTH_SHORT).show();
            }
        });

        //Button to go to GoogleMaps activity to see the Station on the map
        Button see_onMap=(Button)findViewById(R.id.see_onMap);
        assert see_onMap != null;
        see_onMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goMaps_Station = new Intent(v.getContext(), GoogleMaps_Station.class);
                goMaps_Station.putExtra("lat",getIntent().getDoubleExtra("lat",0));
                goMaps_Station.putExtra("long",getIntent().getDoubleExtra("long",0));
                goMaps_Station.putExtra("location",getIntent().getStringExtra("localidade"));
                startActivity(goMaps_Station);
            }
        });
    }
}
