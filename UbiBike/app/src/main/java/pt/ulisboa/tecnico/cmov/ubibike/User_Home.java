package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class User_Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user__home);

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

    }


}
