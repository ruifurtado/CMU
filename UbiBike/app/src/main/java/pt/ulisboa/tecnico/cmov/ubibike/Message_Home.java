package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class Message_Home extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapterItems;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_message__home);

        items = new ArrayList<String>();
        list = (ListView) findViewById(R.id.messagesContainer);
        adapterItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(adapterItems);

        //Button to go to Search User to send a message activity and expects a returned username
        Button search_user=(Button)findViewById(R.id.searchButton);
        assert search_user != null;
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search all available friends neer me
                adapterItems.clear();
                adapterItems.insert("vasco",0);
                adapterItems.insert("bernardo",0);
                adapterItems.insert("rui",0);

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Intent myIntent = new Intent(item.getContext(), Message_Chat.class);
                String user=(String) adapter.getItemAtPosition(pos);
                myIntent.putExtra("ToUsername", user );
                myIntent.putExtra("Username", getIntent().getStringExtra("Username"));
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backHome = new Intent(getApplicationContext(),User_Home.class);
        backHome.putExtra("Username", getIntent().getStringExtra("Username"));
        startActivity(backHome);
    }
}

