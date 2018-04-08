package cabiso.daphny.com.g_companion.Model;

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

    public User_Profile() {
    }

    public User_Profile(String address, String contact_no, String f_name, String l_name, String email, String password, String userID) {
        this.address = address;
        this.contact_no = contact_no;
        this.f_name = f_name;
        this.l_name = l_name;
        this.email = email;
        this.password = password;
        this.userID = userID;
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
}