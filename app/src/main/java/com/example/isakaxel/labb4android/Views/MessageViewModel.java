package com.example.isakaxel.labb4android.Views;

/**
 * Created by alf on 1/8/16.
 */
public class MessageViewModel {
    private long id;
    private String from;
    private String to;
    private String msg;

    public MessageViewModel(long id, String from, String to, String msg){
        this.id = id;
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMsg() {
        return msg;
    }

    public long getId() {
        return id;
    }
}
