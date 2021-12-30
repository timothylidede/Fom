package models;

public class ModelPosts {
    private String place_name, place_latitude, place_longitude, date, time, post_id
            ,timestamp, bite, pack_id, like_count, likes;

    private long timestart, timeend, count;

    public ModelPosts(){}

    public ModelPosts(String place_name, String place_latitude, String place_longitude, String date, String time, String post_id, String timestamp, String bite, String pack_id, String like_count, String likes, long timestart, long timeend, long count) {
        this.place_name = place_name;
        this.place_latitude = place_latitude;
        this.place_longitude = place_longitude;
        this.date = date;
        this.time = time;
        this.post_id = post_id;
        this.timestamp = timestamp;
        this.bite = bite;
        this.pack_id = pack_id;
        this.like_count = like_count;
        this.likes = likes;
        this.timestart = timestart;
        this.timeend = timeend;
        this.count = count;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_latitude() {
        return place_latitude;
    }

    public void setPlace_latitude(String place_latitude) {
        this.place_latitude = place_latitude;
    }

    public String getPlace_longitude() {
        return place_longitude;
    }

    public void setPlace_longitude(String place_longitude) {
        this.place_longitude = place_longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBite() {
        return bite;
    }

    public void setBite(String bite) {
        this.bite = bite;
    }

    public String getPack_id() {
        return pack_id;
    }

    public void setPack_id(String pack_id) {
        this.pack_id = pack_id;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ModelPosts{" +
                "place_name='" + place_name + '\'' +
                ", place_latitude='" + place_latitude + '\'' +
                ", place_longitude='" + place_longitude + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", post_id='" + post_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", bite='" + bite + '\'' +
                ", pack_id='" + pack_id + '\'' +
                ", like_count='" + like_count + '\'' +
                ", likes='" + likes + '\'' +
                ", timestart=" + timestart +
                ", timeend=" + timeend +
                ", count=" + count +
                '}';
    }
}
