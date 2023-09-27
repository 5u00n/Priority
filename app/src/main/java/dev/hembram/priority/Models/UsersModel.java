package dev.hembram.priority.Models;

public class UsersModel {

    String u_name,date_joined,user_uid, profile_img_url;

    public UsersModel( String user_uid,String u_name, String date_joined, String profile_img_url) {
        this.u_name = u_name;
        this.date_joined = date_joined;
        this.user_uid = user_uid;
        this.profile_img_url = profile_img_url;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getProfile_img_url() {
        return profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }
}
