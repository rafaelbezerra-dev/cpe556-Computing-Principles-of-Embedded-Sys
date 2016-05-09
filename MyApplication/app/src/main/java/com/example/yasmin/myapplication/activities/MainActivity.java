package com.example.yasmin.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

//import org.json.*;
import com.example.yasmin.myapplication.adapters.MessageRowAdapter;
import com.example.yasmin.myapplication.entities.Client;
import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.R;
import com.example.yasmin.myapplication.services.HttpServiceWrapper;
import com.example.yasmin.myapplication.utils.App;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final int PREFERENCES_REQUEST = 1;

    private ArrayList<Client> clientArray;
    private ArrayList<Message> messageArray;
    private int userId;
    private String userName;
    private int lastMessageId;

    private MessageRowAdapter messageRowAdapter;
    private ListView messageListView;
    private EditText newMessageEditText;
    private ImageButton sendButton;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layt__main);
        messageListView = (ListView) findViewById(R.id.layt_main_lst_messages);
        newMessageEditText = (EditText) findViewById(R.id.layt_main_message);
        sendButton = (ImageButton) findViewById(R.id.layt_main_btn_send);

        clientArray = new ArrayList<>();
        messageArray = new ArrayList<>();

        messageRowAdapter = new MessageRowAdapter(this, messageArray);
        messageListView.setAdapter(messageRowAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
        userId = sharedPreferences.getInt(App.PREF_KEY_USERID, -1);
        if (sharedPreferences.contains(App.PREF_KEY_USERNAME))
            userName = sharedPreferences.getString(App.PREF_KEY_USERNAME, "anonymous");
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            userName = "anonymous";
            editor.putString(App.PREF_KEY_USERNAME, userName);
            editor.apply();
        }

        if (userId == -1) {
            sendClient();
        }

        getClientList();
        getMessageList(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = newMessageEditText.getText().toString();
                userId = sharedPreferences.getInt(App.PREF_KEY_USERID, 0);
                sendMessage(new Message(msg, userId));
                newMessageEditText.setText("");

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                startActivityForResult(new Intent(this, SettingsActivity.class), PREFERENCES_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case PREFERENCES_REQUEST:
                sendClient();
                break;
        }
    }

    private void getClientList() {
        HttpServiceWrapper.getClientList(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        clientArray.add(new Client(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "I got " + clientArray.size() + " clients.");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't load the client list.", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void sendClient() {
        HttpServiceWrapper.sendClient(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "post success");
                try {
                    Client c = new Client(response);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(App.PREF_KEY_USERID, c.getClientId());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't send the client info.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMessageList(){
        getMessageList(false);
    }

    private void getMessageList(final boolean allMessages) {
        HttpServiceWrapper.getMessageList(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;
                if (allMessages)
                    messageArray.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        messageArray.add(new Message(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "I got " + messageArray.size() + " message.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't load the message list.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendMessage(final Message message) {

        message.setSent(false);
        messageArray.add(message);

        HttpServiceWrapper.sendMessage(this, message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "post success");
                messageArray.remove(message);
                JSONObject object = null;
                for (int i = 1; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        messageArray.add(new Message(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't send the new message.", Toast.LENGTH_LONG).show();
            }
        });
    }
}



/*
* JSONObject jsonParams = new JSONObject();
        jsonParams.put("notes", "Test api support");
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
*
* */

