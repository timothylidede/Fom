package models;

public class ModelPackNotification {
    private String pack_id, timestamp, sender_id, notification;

    public ModelPackNotification(){}

    public ModelPackNotification(String pack_id, String timestamp, String sender_id, String notification) {
        this.pack_id = pack_id;
        this.timestamp = timestamp;
        this.sender_id = sender_id;
        this.notification = notification;
    }

    public String getPack_id() {
        return pack_id;
    }

    public void setPack_id(String pack_id) {
        this.pack_id = pack_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "ModelPackNotification{" +
                "pack_id='" + pack_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sender_id='" + sender_id + '\'' +
                ", notification='" + notification + '\'' +
                '}';
    }
}
