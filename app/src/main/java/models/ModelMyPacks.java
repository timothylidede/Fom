package models;

public class ModelMyPacks {
    private String user_id;
    private String pack_id;
    private String alpha;
    private long timeend, timestart;

    public ModelMyPacks(){}

    public ModelMyPacks(String user_id, String pack_id, String alpha, long timeend, long timestart) {
        this.user_id = user_id;
        this.pack_id = pack_id;
        this.alpha = alpha;
        this.timeend = timeend;
        this.timestart = timestart;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPack_id() {
        return pack_id;
    }

    public void setPack_id(String pack_id) {
        this.pack_id = pack_id;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    @Override
    public String toString() {
        return "ModelMyPacks{" +
                "user_id='" + user_id + '\'' +
                ", pack_id='" + pack_id + '\'' +
                ", alpha='" + alpha + '\'' +
                ", timeend=" + timeend +
                ", timestart=" + timestart +
                '}';
    }
}
