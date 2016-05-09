package com.example.yasmin.myapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yasmin on 5/8/2016.
 */
public class Message {
    int messageId;
    int clientId;
    String messageContent;
    String clientName;
    boolean isSent;

    public Message(String text, int client) {
        clientId = client;
        messageContent = text;
    }


    public Message(JSONObject object) throws JSONException {
        messageId = object.getInt("messageId");
        clientId = object.getInt("clientId");
        messageContent = object.getString("messageContent");
        clientName = object.getString("clientName");
        isSent = true;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }
}
