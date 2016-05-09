package com.example.yasmin.myapplication.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

//import org.json.*;
import com.example.yasmin.myapplication.adapters.MessageRowAdapter;
import com.example.yasmin.myapplication.entities.Client;
import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.R;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String PREF_KEY_USERNAME = "username";
    public static final String PREF_KEY_LAST_SEQNUM = "last_seq_num";

    private ArrayList<Client> clientArray;
    private ArrayList<Message> messageArray;
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
        userName = sharedPreferences.getString(PREF_KEY_USERNAME, "anonymous");

        getClientList();
        getMessageList();

    }

    private void getClientList() {
        String url = "http://155.246.135.76:8000/clients";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("rsz", "8");
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

    private void getMessageList(){
        String url = "http://155.246.135.76:8000/messages";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("rsz", "8");
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;
                messageArray.clear();
                for(int i = 0; i < response.length(); i++){
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
}



/*
* JSONObject jsonParams = new JSONObject();
        jsonParams.put("notes", "Test api support");
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
*
* */

