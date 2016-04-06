package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Sign_In extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign__in);

        Button attemp_login=(Button)findViewById(R.id.ok);
        assert attemp_login != null;
        attemp_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username_input = (EditText) findViewById(R.id.usernameEnter);
                String username = username_input.getText().toString();
                username_input.setText("");

                Intent goAttemp_login = new Intent(v.getContext(), User_Home.class);
                goAttemp_login.putExtra("Username",username);
                startActivity(goAttemp_login);
            }
        });

    }


}
