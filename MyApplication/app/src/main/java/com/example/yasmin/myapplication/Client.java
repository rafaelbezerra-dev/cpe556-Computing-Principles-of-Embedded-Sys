package com.example.yasmin.myapplication;

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

    public int getClientId() {
        return clientId;
    }

    public String getClientName(){
        return clientName;
    }
}
