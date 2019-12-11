package com.example.naber;

public class GroupMessages {

    private String message, userID, time , date, name;

    public GroupMessages(){}

    public GroupMessages(String message, String userID, String time, String date, String name) {
        this.message = message;
        this.userID = userID;
        this.time = time;
        this.date = date;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
