package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class User_Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user__home);

        //Feedback positive for the login
        Toast.makeText(this, "Login Correct", Toast.LENGTH_SHORT).show();

        //To get the intent, extract the username that perform the login operation and display it on the textView
        Intent data = getIntent();
        String username = data.getStringExtra("Username");
        TextView username_presentation = (TextView) findViewById(R.id.username_presentation);
        username_presentation.setText(username);

        //Button to go to Send Points activity
        Button send_points=(Button)findViewById(R.id.send_points);
        assert send_points != null;
        send_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSend_points = new Intent(v.getContext(), Send_Points.class);
                startActivity(goSend_points);
            }
        });

        //Button to go to Trajectories activity
        Button go_trajectories=(Button)findViewById(R.id.trajectory);
        assert go_trajectories != null;
        go_trajectories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goTrajectories = new Intent(v.getContext(), Trajectories.class);
                startActivity(goTrajectories);
            }
        });

        //Button to go to Stations activity
        Button go_Stations=(Button)findViewById(R.id.booking_bike);
        assert go_Stations != null;
        go_Stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goStations = new Intent(v.getContext(), Stations.class);
                startActivity(goStations);
            }
        });
    }
}
