package com.example.yasmin.myapplication;

/**
 * Created by Yasmin on 5/8/2016.
 */
public class Message {
    int messageId;
    int clientId;
    String messageContent;

    public Message(String text, int client) {
        clientId = client;
        messageContent = text;
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
}
