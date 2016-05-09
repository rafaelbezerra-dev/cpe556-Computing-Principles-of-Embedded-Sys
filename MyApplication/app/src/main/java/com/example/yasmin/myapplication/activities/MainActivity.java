package com.example.yasmin.myapplication.activities;

import android.app.Activity;
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
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String DEFAULT_HOST_URI = "http://155.246.76.25:8000";
    public static final String DEFAULT_ENCODING = "application/json";

    public static final String PREF_KEY_USERID = "userid";
    public static final String PREF_KEY_USERNAME = "username";
    public static final String PREF_KEY_LAST_SEQNUM = "last_seq_num";

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
       /* client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("My Application POST ", "Hello World!");
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d("My Application POST ", "Goodbye World :(");
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });*/

        setContentView(R.layout.layt__main);
        messageListView = (ListView) findViewById(R.id.layt_main_lst_messages);
        newMessageEditText = (EditText) findViewById(R.id.layt_main_message);
        sendButton = (ImageButton) findViewById(R.id.layt_main_btn_send);

        clientArray = new ArrayList<>();
        messageArray = new ArrayList<>();

        messageRowAdapter = new MessageRowAdapter(this, messageArray);
        messageListView.setAdapter(messageRowAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastMessageId = sharedPreferences.getInt(PREF_KEY_LAST_SEQNUM, 0);
        userId = sharedPreferences.getInt(PREF_KEY_USERID, -1);
        if (sharedPreferences.contains(PREF_KEY_USERNAME))
            userName = sharedPreferences.getString(PREF_KEY_USERNAME, "anonymous");
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            userName = "anonymous";
            editor.putString(PREF_KEY_USERNAME, userName);
            editor.apply();
        }

        if (userId == -1) {
            postClient();
        }

        getClientList();
        getMessageList();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = newMessageEditText.getText().toString();
                userId = sharedPreferences.getInt(PREF_KEY_USERID, 0);
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
                // TODO: Update the user name in the server
                break;
        }
    }

    private void getClientList() {
        String url = DEFAULT_HOST_URI + "/clients";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
//        params.put("rsz", "8");
        client.get(url, params, new JsonHttpResponseHandler() {
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
                Log.d(TAG, "My Application Client GET: Goodbye World :(");
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });

    }

    private void getMessageList() {
        String url = DEFAULT_HOST_URI + "/messages";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
//        params.put("rsz", "8");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;
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
                Log.d(TAG, "My Application Message GET: Goodbye World :(");
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
    }

    private void postClient() {
        String uri = DEFAULT_HOST_URI + "/clients";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");

        StringEntity entity = null;
        try {
            userName = sharedPreferences.getString(PREF_KEY_USERNAME, "anonymous");
            JSONObject jsonParams = new Client(userName).serialize();
            entity = new StringEntity(jsonParams.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.post(this, uri, entity, DEFAULT_ENCODING, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, "post success");
                        try {
                            Client c = new Client(response);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(PREF_KEY_USERID, c.getClientId());
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Log.d(TAG, "post failed -> Status " + statusCode);
                    }
                }

        );
    }

    private void sendMessage(final Message message) {

        message.setSent(false);
        messageArray.add(message);

        String uri = DEFAULT_HOST_URI + "/messages";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");

        StringEntity entity = null;
        try {
            JSONObject jsonParams = message.serialize();
            entity = new StringEntity(jsonParams.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.post(this, uri, entity, DEFAULT_ENCODING, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "post success");

                try {
                    Message msg = new Message(response);
                    message.setMessageId(msg.getMessageId());
                    message.setSent(true);
                } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d(TAG, "post failed -> Status " + statusCode);
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

