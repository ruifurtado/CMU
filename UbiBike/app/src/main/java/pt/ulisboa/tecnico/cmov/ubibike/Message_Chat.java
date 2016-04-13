package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Context;
import org.apache.commons.io.FileUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Message_Chat extends AppCompatActivity {

    private ListView itemsList;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> msgList ;
    private String friendName;
    private Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message__chat);

        final TextView friend = (TextView) findViewById(R.id.friendLabel);  //Para atualizar o nome de com que estamos a falar por msg
        friendName = getIntent().getStringExtra("Username");
        friend.setText(friendName);
        itemsList = (ListView) findViewById(R.id.messagesContainer);

        //read the file related with this user, and load the respective messages
        readItems(friendName);

        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, msgList);    //inicializa sempre o Adapter com os antigos valores
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
                String newMsg=formattedDate + ": " + msg;
                itemsAdapter.insert(newMsg,0);

                //write the new message on the file related with this user
                writeItems(friendName);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Hide the Keyboard after send a messgae
                imm.hideSoftInputFromWindow(newItem.getWindowToken(), 0);
                newItem.getText().clear(); //CLean the text on the editText
            }
        });
    }

    //Code to persists items to a file (read and write to a file)
    private void readItems(String filename) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, filename+".txt");
        try {
            msgList = new ArrayList<>(FileUtils.readLines(file));
        } catch (IOException e) {
            msgList = new ArrayList<>();
        }
    }

    //Code to persists items to a file (read and write to a file)
    private void writeItems(String filename) {
        File filesDir = getFilesDir();
        File file = new File(filesDir, filename+".txt");
        try {
            FileUtils.writeLines(file, msgList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
