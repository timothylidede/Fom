package models;

public class ModelPotentials {

    private String display_name;
    private String username;
    private String image_one;
    private String image_two;
    private String image_three;
    private String user_id;

    public ModelPotentials(){}

    public ModelPotentials(String display_name, String username, String image_one, String image_two, String image_three, String user_id) {
        this.display_name = display_name;
        this.username = username;
        this.image_one = image_one;
        this.image_two = image_two;
        this.image_three = image_three;
        this.user_id = user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage_one() {
        return image_one;
    }

    public void setImage_one(String image_one) {
        this.image_one = image_one;
    }

    public String getImage_two() {
        return image_two;
    }

    public void setImage_two(String image_two) {
        this.image_two = image_two;
    }

    public String getImage_three() {
        return image_three;
    }

    public void setImage_three(String image_three) {
        this.image_three = image_three;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "ModelPotentials{" +
                "display_name='" + display_name + '\'' +
                ", username='" + username + '\'' +
                ", image_one='" + image_one + '\'' +
                ", image_two='" + image_two + '\'' +
                ", image_three='" + image_three + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
