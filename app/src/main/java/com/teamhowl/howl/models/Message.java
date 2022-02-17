package com.teamhowl.howl.models;

import java.util.Date;

public class Message {

    private String text;
    private Date timeSent;
    private Date timeReceived;

    public Message(String text, Date timeSent, Date timeReceived){

        this.text = text;
        this.timeSent = timeSent;
        this.timeReceived = timeReceived;
    }

    public String getText(){ return text; }
    public Date getTimeSent(){ return timeSent; }
    public Date getReceived(){ return timeReceived; }

}
