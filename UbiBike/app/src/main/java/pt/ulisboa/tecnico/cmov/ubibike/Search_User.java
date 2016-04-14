package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class Search_User extends AppCompatActivity {

    private ListView itemsUsernames;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search__user);

        // List and Adapter Code Here
        items = new ArrayList<>();
        itemsUsernames = (ListView) findViewById(R.id.list_recentlyUsed);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsUsernames.setAdapter(itemsAdapter);

        //Code to allow the reading from the coordinates to check the users near to me
        itemsAdapter.insert("vasco",0); //Static user for test propose
        itemsAdapter.insert("bernardo",0); //Static user for test propose
        itemsAdapter.insert("rui",0); //Static user for test propose


        // Method to Select a users from the List and return
        setupListViewListener();
    }

    //Code of the method to allow the removing items from the list
    private void setupListViewListener() {
        itemsUsernames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                //Save the selected username and returned to the Send Points activity
                String username = (String) adapter.getItemAtPosition(pos);
                Intent search_result = new Intent();
                search_result.putExtra("Username", username);
                setResult(Activity.RESULT_OK, search_result);
                finish();
            }
        });
    }
}
