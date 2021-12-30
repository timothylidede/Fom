package models;

public class User {
    private long age;
    private String email;
    private String user_id;
    private long year;

    public User(){}

    public User(long age, String email, String user_id, long year) {
        this.age = age;
        this.email = email;
        this.user_id = user_id;
        this.year = year;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", year=" + year +
                '}';
    }
}
