package com.example.isakaxel.labb4android.Model;

/**
 * Created by loyde on 2016-01-10.
 */
public class Message {
    private long id;
    private String from;
    private String to;
    private String msg;

    public Message(long id, String from, String to, String msg){
        this.id = id;
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
