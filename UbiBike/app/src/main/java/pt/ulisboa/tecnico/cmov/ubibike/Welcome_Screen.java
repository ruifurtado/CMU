package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Welcome_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome__screen);

        Button sign_in=(Button)findViewById(R.id.sign_in);
        assert sign_in != null;
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSign_In = new Intent(v.getContext(), Sign_In.class);
                startActivityForResult(goSign_In, 0); //start new activity and excepts a result
            }
        });
    }
}
