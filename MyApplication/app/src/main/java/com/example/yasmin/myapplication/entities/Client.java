package com.example.yasmin.myapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yasmin on 5/8/2016.
 */
public class Client {
    int clientId;
    String clientName;

    public Client(String name, int id) {
        clientId = id;
        clientName = name;
    }

    public Client(JSONObject object) throws JSONException {
        clientId = object.getInt("clientId");
        clientName = object.getString("clientName");
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName(){
        return clientName;
    }

}
