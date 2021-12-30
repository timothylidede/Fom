package models;

public class ModelPack {

    private String pack_name, pack_id, alpha, day;
    private long members_count, timeend, timestart;

    private ModelPack(){}

    public ModelPack(String pack_name, String pack_id, String alpha, String day, long members_count, long timeend, long timestart) {
        this.pack_name = pack_name;
        this.pack_id = pack_id;
        this.alpha = alpha;
        this.day = day;
        this.members_count = members_count;
        this.timeend = timeend;
        this.timestart = timestart;
    }

    public String getPack_name() {
        return pack_name;
    }

    public void setPack_name(String pack_name) {
        this.pack_name = pack_name;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getMembers_count() {
        return members_count;
    }

    public void setMembers_count(long members_count) {
        this.members_count = members_count;
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
        return "ModelPack{" +
                "pack_name='" + pack_name + '\'' +
                ", pack_id='" + pack_id + '\'' +
                ", alpha='" + alpha + '\'' +
                ", day='" + day + '\'' +
                ", members_count=" + members_count +
                ", timeend=" + timeend +
                ", timestart=" + timestart +
                '}';
    }
}
