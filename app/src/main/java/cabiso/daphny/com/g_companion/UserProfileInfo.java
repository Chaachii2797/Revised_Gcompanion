package cabiso.daphny.com.g_companion;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class UserProfileInfo {
    public String address;
    public String email;
    public String username;
    public String phone;
    public int userRating;

    UserProfileInfo(){

    }

    UserProfileInfo(String username, String email, String phone, String address, int userRating){
        this.address = address;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.userRating = userRating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

}
