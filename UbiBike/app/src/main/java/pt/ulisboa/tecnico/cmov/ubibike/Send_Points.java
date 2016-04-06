package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Send_Points extends AppCompatActivity {

    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__points);

        username= (TextView) findViewById(R.id.username_toSent2);

        Button recently_user=(Button)findViewById(R.id.search);
        assert recently_user != null;
        recently_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRecently_used = new Intent(v.getContext(), Search_User.class);
                startActivityForResult(goRecently_used, 0); //start new activity and excepts a result
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode==0) {
            String user = data.getStringExtra("Username");
            username.setText(user);

        }

    }
}
