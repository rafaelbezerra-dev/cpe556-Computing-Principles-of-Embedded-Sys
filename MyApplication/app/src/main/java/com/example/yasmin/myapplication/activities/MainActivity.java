package com.example.yasmin.myapplication.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//import org.json.*;
import com.example.yasmin.myapplication.entities.Client;
import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.R;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    ArrayList<Client> clientArray = new ArrayList<Client>();
    ArrayList<Message> messageArray = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getClientList();
        getMessageList();
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

        setContentView(R.layout.activity_main);



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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.d("My Application Client GET ", "Goodbye World :(");
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

                for(int i = 0; i < response.length(); i++){
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
                Log.d("My Application Message GET ", "Goodbye World :(");
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

