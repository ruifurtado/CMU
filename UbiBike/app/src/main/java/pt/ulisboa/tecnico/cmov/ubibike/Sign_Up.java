package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sign_Up extends AppCompatActivity {

    private String username;
    private String mail;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign__up);

        //Button to validate the input data and, if yes, return to the welcome screen
        Button attemp_register=(Button)findViewById(R.id.ok_register);
        assert attemp_register != null;
        attemp_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save the input for the username
                EditText username_input = (EditText) findViewById(R.id.input_name);
                username = username_input.getText().toString();
                username_input.setText("");

                //save the input for the email
                EditText mail_input = (EditText) findViewById(R.id.input_mail);
                mail = mail_input.getText().toString();
                mail_input.setText("");

                //save the input for the password
                EditText password_input = (EditText) findViewById(R.id.input_password);
                password = password_input.getText().toString();
                password_input.setText("");

                //Code to check if the input data is valid
                if (username.contains(" ") || username.equals("") || !mail.contains("@") || !mail.contains(".") || password.equals("") || password.contains(" ")) {
                    feedBackSignUp();
                }

                //Code to return to the welcome screen and pass all the input data
                else {
                    Intent sign_upFinish = new Intent();
                    sign_upFinish.putExtra("Username", username);
                    sign_upFinish.putExtra("Email", mail);
                    sign_upFinish.putExtra("Password", password);
                    setResult(Activity.RESULT_OK, sign_upFinish);
                    finish();
                }
            }
        });
    }

    //Method to provide feedBack of the operations of the activity
    //NOTE: ".makeText" cannot be resolved in a onClick Method
    protected void feedBackSignUp(){
        Toast.makeText(this, "The input data is not valid. Try again", Toast.LENGTH_SHORT).show();
    }
}
