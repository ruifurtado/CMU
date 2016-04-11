package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Message_Home extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    //private ArrayList<Boolean> values;
    private String name;
    private String name2;
    //private Button cleanMessages;
    //private CheckBox check;


    private static final Boolean BOOLEAN=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message__home);

        items = new ArrayList<String>();
        list = (ListView) findViewById(R.id.messagesContainer);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(adapter);

        name = "Tobias José Morgado";
        name2 = "Antonio Manuel Estevao";
        adapter.add(name);
        adapter.add(name2);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                switch (pos) {
                    case 0:
                        Intent myIntent = new Intent(item.getContext(), Message_Chat.class);
                        myIntent.putExtra("friend", name);
                        startActivity(myIntent);
                        break;
                    case 1:
                        Intent myIntent2 = new Intent(item.getContext(), Message_Chat.class);
                        myIntent2.putExtra("friend", name2);
                        startActivity(myIntent2);
                        break;
                }
            }
        });

//        cleanMessages = (Button) findViewById(R.id.cleanMsg);
//        cleanMessages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i=1;
//                int numberOfRequiredCheckBox=list.getAdapter().getCount();
//                check = (CheckBox) findViewById(R.id.checkMsg);
//                check.setVisibility(v.VISIBLE);
//
//
//                values = new ArrayList<Boolean>();
//                values.add(check.isChecked());
//                for (int j = 0; j < values.size(); j++) {
//                    if (values.get(j).TRUE.equals(BOOLEAN)) {      //Null safe
//                        try {
//                            FileOutputStream outputStream = openFileOutput(name, Context.MODE_PRIVATE);
//                            new File(name).delete();
//                            outputStream.close();
//                        } catch (IOException e) {}
//                    }
//                }
//            }
//        });
    }
}

