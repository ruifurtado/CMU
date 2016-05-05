package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sign_In extends AppCompatActivity {

    private Socket client;
    private PrintWriter printwriter;
    private String message;
    private String serverIp="10.0.2.2";
    private String username;
    private String password;
    private String method="Sign_In";
    private String messageFromServer;
    private Thread t;

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
                username = username_input.getText().toString();
                username_input.setText("");

                //save the input for the username
                EditText password_input = (EditText) findViewById(R.id.passwdEnter);
                password = password_input.getText().toString();
                password_input.setText("");

                messageFromServer = communicateWithServer();
                if (!messageFromServer.equals("IOException")) {

                    if (messageFromServer.contains("1")) {
                        String[] pointsUser = messageFromServer.split(" ");
                        feedBackSignUp("Login Successfully");
                        Intent goAttemp_login = new Intent(v.getContext(), User_Home.class);
                        goAttemp_login.putExtra("Username", username);
                        goAttemp_login.putExtra("Points", pointsUser[1]);
                        goAttemp_login.putExtra("Points Sender", pointsUser[2]);
                        goAttemp_login.putExtra("Points Receiver", pointsUser[3]);
                        startActivity(goAttemp_login);
                    } else
                        feedBackSignUp(messageFromServer);
                } else
                    feedBackSignUp("Server Unreachable! Please Try Later!");
            }
        });
    }

    Runnable server = new Runnable() {
        @Override
        public void run() {
            try {
                message=method+","+username+","+password;
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

            } catch (UnknownHostException e){
                e.printStackTrace();
            }catch(IOException e){
                Log.d("Communicating Server","Server Unreachable! Please Try Later!");
                messageFromServer="IOException";
                e.printStackTrace();
            }
        }
    };

    protected String communicateWithServer (){
        t=new Thread(server, "My Server");
        t.start();
        try {
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return messageFromServer;
    }

    //Method to provide feedBack of the operations of the activity
    //NOTE: ".makeText" cannot be resolved in a onClick Method
    protected void feedBackSignUp(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        DataHolder.getInstance().resetFlagSign_In();
        Intent backHome = new Intent(Sign_In.this,Welcome_Screen.class);
        startActivity(backHome);
    }
}
