package cabiso.daphny.com.g_companion.Model;

import android.location.Location;

/**
 * Created by cicctuser on 4/8/2018.
 */

public class User_Profile {
    String address;
    String contact_no;
    String f_name;
    String l_name;
    String email;
    String password;
    String userID;
    String firebaseToken;
    String android_id;
    String userProfileUrl;
    Float userRating;
    String role;
    String report_status;
    String access_token;
    double current_lng;
    double current_lat;


    public User_Profile() {
        current_lat = 0;
        current_lng = 0;
    }

    public User_Profile(String address, String contact_no, String f_name, String l_name, String email, String password,
                        String userID, String firebaseToken, String userProfileUrl, Float userRating, String role, String report_status) {
        this.address = address;
        this.contact_no = contact_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.password = password;
        this.userID = userID;
        this.firebaseToken = firebaseToken;
        this.android_id = android_id;
        this.userProfileUrl = userProfileUrl;
        this.userRating = userRating;
        this.role = role;
        this.report_status = report_status;
        this.current_lat = 0;
        this.current_lng = 0;
    }

    public Location getLocation(){
        if(current_lng > 0 && current_lat > 0) {
            Location mylocation = new Location("myLocation");
            mylocation.setLatitude(current_lat);
            mylocation.setLongitude(current_lng);
            return mylocation;
        }
        return null;
    }


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAddress() {
        return address;

    }

    public User_Profile setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getContact_no() {
        return contact_no;
    }

    public User_Profile setContact_no(String contact_no) {
        this.contact_no = contact_no;
        return this;
    }

    public String getF_name() {
        return f_name;
    }

    public User_Profile setF_name(String f_name) {
        this.f_name = f_name;
        return this;
    }

    public String getL_name() {
        return l_name;
    }

    public User_Profile setL_name(String l_name) {
        this.l_name = l_name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User_Profile setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public User_Profile setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User_Profile setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public Float getUserRating() {
        return userRating;
    }

    public void setUserRating(Float userRating) {
        this.userRating = userRating;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReport_status() {
        return report_status;
    }

    public void setReport_status(String report_status) {
        this.report_status = report_status;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }
}
