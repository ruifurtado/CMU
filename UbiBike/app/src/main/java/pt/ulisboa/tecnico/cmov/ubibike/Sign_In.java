package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sign_In extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign__in);

        //Button to go to User_Home activity
        Button attemp_login=(Button)findViewById(R.id.ok);
        assert attemp_login != null;
        attemp_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save the input for the username
                EditText username_input = (EditText) findViewById(R.id.usernameEnter);
                String username = username_input.getText().toString();
                username_input.setText("");

                //save the input for the username
                EditText password_input = (EditText) findViewById(R.id.passwdEnter);
                String password = password_input.getText().toString();
                password_input.setText("");

                //Code to ask to the server if the login input is correct and valid
                //boolean result=//IMPORTANT: In this part of the code, the APP have to communicate with the server to check the input data of the login
                //if(!result){
                    //feedBackSignUp();
                // }
                //else{
                    Intent goAttemp_login = new Intent(v.getContext(), User_Home.class);
                    goAttemp_login.putExtra("Username", username);
                    startActivity(goAttemp_login);
                //}
            }
        });
    }

    //Method to provide feedBack of the operations of the activity
    //NOTE: ".makeText" cannot be resolved in a onClick Method
    protected void feedBackSignUp(){
        Toast.makeText(this, "Login Incorrect", Toast.LENGTH_SHORT).show();
    }

}
