package models;

public class ModelUserNotification {

    private String sender_id, timestamp, notification;

    public ModelUserNotification(){}

    public ModelUserNotification(String sender_id, String timestamp, String notification) {
        this.sender_id = sender_id;
        this.timestamp = timestamp;
        this.notification = notification;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "ModelUserNotification{" +
                "sender_id='" + sender_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", notification='" + notification + '\'' +
                '}';
    }
}
