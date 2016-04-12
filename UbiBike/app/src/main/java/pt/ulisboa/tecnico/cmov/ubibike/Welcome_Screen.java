package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Welcome_Screen extends AppCompatActivity {
    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String password;
    private String email;
    private String method="Sign_Up";
    private String messageFromServer;
    private Thread t;




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
            username = data.getStringExtra("Username");
            email = data.getStringExtra("Email");
            password = data.getStringExtra("Password");
            //IMPORTANT: In this part of the code, the APP have to communicate with the server to pass the new user, with all the data received
            String registerResult=CommunicateWithServer();
            Toast.makeText(this, registerResult, Toast.LENGTH_SHORT).show();
        }
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
                message=method+" "+username+" "+email+" "+password;
                client = new Socket(serverIp, 4444);  //connect to server
                printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(message);  //write the message to output stream
                printwriter.flush();
                printwriter.close();
                client.close();   //closing the connection

                client = new Socket(serverIp, 4444);  //connect to server
                // Get input buffer
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                messageFromServer = br.readLine();
                br.close();
                client.close();   //closing the connection

            } catch (UnknownHostException  e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    };

    protected String CommunicateWithServer (){
        t=new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return messageFromServer;
    }
}
