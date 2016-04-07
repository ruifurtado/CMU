package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Send_Points extends AppCompatActivity {
    private TextView username;
    private TextView points_sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__points);

        //IMPORTANT: The App have to communicate with the server to check all the data related to this user, that is need in this activity

        //To display the selected username that will received some points
        username= (TextView) findViewById(R.id.username_toSent2);
        username.setText("");

        //To display the total amount of points that the user sent to friends
        points_sent= (TextView) findViewById(R.id.points_sent2);
        points_sent.setText("0"); //Static value only for test proposes

        //Button to go to Search User activity and expects a returned username
        Button search_user=(Button)findViewById(R.id.search);
        assert search_user != null;
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSearch_user = new Intent(v.getContext(), Search_User.class);
                startActivityForResult(goSearch_user, 0); //start new activity and excepts a result
            }
        });

        //Button to go to Search User activity and expects a returned username
        Button send_points=(Button)findViewById(R.id.send);
        assert send_points != null;
        send_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Write the points the will be send and after a click, update the amount of points send
                EditText points_toSend = (EditText) findViewById(R.id.points_toSent2);
                String points = points_toSend.getText().toString();
                points_toSend.setText("");
                username.setText("");
                Integer point = 0;
                try {
                    point = Integer.parseInt(points_sent.getText().toString()) + Integer.parseInt(points);
                } catch(NumberFormatException e) {
                    Toast.makeText(Send_Points.this, "Write a valid amount of points", Toast.LENGTH_SHORT).show();
                }
                points_sent.setText(""+point);
                //IMPORTANT: We have to update the total of points of the user and communicate to the server to update all data of the 2 users, total points, points sent and received
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //cycle to check if it's a result from the Search User activity
        if (resultCode == RESULT_OK && requestCode==0) {
            String user = data.getStringExtra("Username");

            //Display the selected username on the textView available
            username.setText(user);
        }
    }
}
