package models;

public class ModelPackMembersImages {
    private String uid;
    private String image_one;

    public ModelPackMembersImages(){}

    public ModelPackMembersImages(String uid, String image_one) {
        this.uid = uid;
        this.image_one = image_one;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage_one() {
        return image_one;
    }

    public void setImage_one(String image_one) {
        this.image_one = image_one;
    }

    @Override
    public String toString() {
        return "ModelPackMembersImages{" +
                "uid='" + uid + '\'' +
                ", image_one='" + image_one + '\'' +
                '}';
    }
}
