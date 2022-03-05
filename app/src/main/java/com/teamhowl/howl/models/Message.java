package com.teamhowl.howl.models;

import java.util.Date;

public class Message {

    public final int SENT = 1;
    public final int RECIEVED = 2;

    private String text;
    private Date timeSent;
    private Date timeReceived;
    private int type;

    public Message(String text, Date timeSent, Date timeReceived){

        this.text = text;
        this.timeSent = timeSent;
        this.timeReceived = timeReceived;
        this.type = RECIEVED;
    }

    public Message(String text, Date timeSent){

        this.text = text;
        this.timeSent = new Date();
        this.timeReceived = timeReceived;
    }

    public String getText(){
        return text;
    }

    public Date getTimeSent(){
        return timeSent;
    }

    public Date getReceived(){
        return timeReceived;
    }

    public int getType(){
        return type;
    }

}
