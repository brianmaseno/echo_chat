package com.example.helloworld.Models;

import java.util.Map;

public class Message {
    public String message;
    public String time;

    public Map timeSent;
    public String userId;

    public Message(String message, String time, Map timeSent, String userId) {
        this.message = message;
        this.time = time;
        this.timeSent = timeSent;
        this.userId = userId;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Map timeSent) {
        this.timeSent = timeSent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
