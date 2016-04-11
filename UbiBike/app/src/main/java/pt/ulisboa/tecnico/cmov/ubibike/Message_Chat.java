package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Message_Chat extends AppCompatActivity {

    private ListView itemsList;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> msgList ;
    private ArrayList<String> msgLater;
    private String friendName;

    private static final String FRIENDNAME="friend";

    private Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message__chat);

        TextView friend = (TextView) findViewById(R.id.friendLabel);  //Para atualizar o nome de com que estamos a falar por msg
        friendName = getIntent().getStringExtra(FRIENDNAME);
        friend.setText(friendName);
        itemsList = (ListView) findViewById(R.id.messagesContainer);

        try {                                               // Para ler do ficheiro
            msgList = new ArrayList<String>();              // coloca em array para inicializar o adapter
            FileInputStream inputStream = openFileInput(friendName);
            Log.v("FILENAME",friendName);
            int content;
            String temp = "";
            while ((content = inputStream.read()) != -1) {
                temp = temp + Character.toString((char) content);

            }
            String[] parts = temp.split("\t");
            for (int i = 0; i < parts.length; i++){
                msgList.add(parts[i]);
            }
            inputStream.close();
        } catch (IOException e) {
            msgList = new ArrayList<String>();
        }


        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);    //inicializa sempre o Adapter com os antigos valores
        itemsList.setAdapter(itemsAdapter);


        Button sendMsg = (Button) findViewById(R.id.chatSendButton);
        assert sendMsg != null;

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText newItem = (EditText) findViewById(R.id.messageEdit);
                String msg = newItem.getText().toString();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                String newMsg=formattedDate + System.lineSeparator()+ msg +"\t";

                try {               //Para escrever no ficheiro
                    FileOutputStream outputStream = openFileOutput(friendName, Context.MODE_APPEND);
                    outputStream.write(newMsg.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                itemsAdapter.add(newMsg);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Para fazer hide ao keyboard apos escrita
                imm.hideSoftInputFromWindow(newItem.getWindowToken(), 0);

                newItem.getText().clear(); //para apagar o texto que fica no edit text quando se escreve

            }
        });

    }
}
