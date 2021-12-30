package models;

public class UserAccountSettings {
    private long connections_count;
    private String display_name;
    private String image_one;
    private String image_three;
    private String image_two;
    private long packs_count;
    private long potentials_count;
    private long packs_following_count;
    private String user_id;

    public UserAccountSettings(){}

    public UserAccountSettings(long connections_count, String display_name, String image_one, String image_three, String image_two, long packs_count, long potentials_count, long packs_following_count, String user_id) {
        this.connections_count = connections_count;
        this.display_name = display_name;
        this.image_one = image_one;
        this.image_three = image_three;
        this.image_two = image_two;
        this.packs_count = packs_count;
        this.potentials_count = potentials_count;
        this.packs_following_count = packs_following_count;
        this.user_id = user_id;
    }

    public long getConnections_count() {
        return connections_count;
    }

    public void setConnections_count(long connections_count) {
        this.connections_count = connections_count;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getImage_one() {
        return image_one;
    }

    public void setImage_one(String image_one) {
        this.image_one = image_one;
    }

    public String getImage_three() {
        return image_three;
    }

    public void setImage_three(String image_three) {
        this.image_three = image_three;
    }

    public String getImage_two() {
        return image_two;
    }

    public void setImage_two(String image_two) {
        this.image_two = image_two;
    }

    public long getPacks_count() {
        return packs_count;
    }

    public void setPacks_count(long packs_count) {
        this.packs_count = packs_count;
    }

    public long getPotentials_count() {
        return potentials_count;
    }

    public void setPotentials_count(long potentials_count) {
        this.potentials_count = potentials_count;
    }

    public long getPacks_following_count() {
        return packs_following_count;
    }

    public void setPacks_following_count(long packs_following_count) {
        this.packs_following_count = packs_following_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "connections_count=" + connections_count +
                ", display_name='" + display_name + '\'' +
                ", image_one='" + image_one + '\'' +
                ", image_three='" + image_three + '\'' +
                ", image_two='" + image_two + '\'' +
                ", packs_count=" + packs_count +
                ", potentials_count=" + potentials_count +
                ", packs_following_count=" + packs_following_count +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
