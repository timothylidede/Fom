package models;

public class ModelPackMembers {

    private String role;
    private String timestamp;
    private String uid;
    private String pack_id;

    public ModelPackMembers(){}

    public ModelPackMembers(String role, String timestamp, String uid, String pack_id) {
        this.role = role;
        this.timestamp = timestamp;
        this.uid = uid;
        this.pack_id = pack_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPack_id() {
        return pack_id;
    }

    public void setPack_id(String pack_id) {
        this.pack_id = pack_id;
    }

    @Override
    public String toString() {
        return "ModelPackMembers{" +
                "role='" + role + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", uid='" + uid + '\'' +
                ", pack_id='" + pack_id + '\'' +
                '}';
    }
}
