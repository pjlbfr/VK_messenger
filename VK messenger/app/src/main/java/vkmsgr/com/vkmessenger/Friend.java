package vkmsgr.com.vkmessenger;

/**
 * Created by Zodiakaio on 22.02.2018.
 */

public class Friend {

    private int id;
    private String fullName;
    private String urlPhoto;
    private int sex;
    private int online;

    public Friend(int id, String fullName, String urlPhoto, int sex, int online) {
        this.id = id;
        this.fullName = fullName;
        this.urlPhoto = urlPhoto;
        this.sex = sex;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}
