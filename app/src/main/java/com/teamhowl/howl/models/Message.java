package com.teamhowl.howl.models;

import java.sql.Date;

public class Message {

    public static final int SENT = 1;
    public static final int RECIEVED = 2;

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
        this.timeSent = timeSent;
        this.timeReceived = new Date(0, 0, 0);
    }

    // TODO remove Mock
    public Message(String text, int type){

        this.text = text;
        this.timeSent = new Date(0, 0, 0);
        this.timeReceived = new Date(0, 0, 0);
        this.type = type;
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
