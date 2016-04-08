package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class Read_Trajectory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read__trajectory);

        //Get the intent
        Intent intent = getIntent();

        //Display all the information about the trajectory, that was passed on the received intent
        TextView startingPoint = (TextView) findViewById(R.id.sPoint);
        startingPoint.setText(intent.getStringExtra("begin"));
        TextView endPoint = (TextView) findViewById(R.id.ePoint);
        endPoint.setText(intent.getStringExtra("end"));
        TextView distance = (TextView) findViewById(R.id.dist);
        distance.setText(intent.getStringExtra("dist"));
        TextView dateText = (TextView) findViewById(R.id.date_front);
        dateText.setText(intent.getStringExtra("date"));
    }
}
