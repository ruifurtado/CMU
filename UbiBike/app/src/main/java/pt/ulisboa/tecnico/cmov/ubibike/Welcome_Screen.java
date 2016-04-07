package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Welcome_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome__screen);

        //Button to go to Sign In activity
        Button sign_in=(Button)findViewById(R.id.sign_in);
        assert sign_in != null;
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSign_In = new Intent(v.getContext(), Sign_In.class);
                startActivity(goSign_In);
            }
        });

        //Button to go to Sign Up activity
        Button sign_up=(Button)findViewById(R.id.sign_up);
        assert sign_up != null;
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSign_Up = new Intent(v.getContext(), Sign_Up.class);
                startActivityForResult(goSign_Up,0); //start new activity and excepts a result
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //cycle to check if it's a result from the Sign Up activity
        if (resultCode == RESULT_OK && requestCode==0) {
            String username = data.getStringExtra("Username");
            String mail = data.getStringExtra("Email");
            String password = data.getStringExtra("Password");
            Toast.makeText(this, "Sign Up Successfully ", Toast.LENGTH_SHORT).show();
            //IMPORTANT: In this part of the code, the APP have to communicate with the server to pass the new user, with all the data received
        }

    }
}
