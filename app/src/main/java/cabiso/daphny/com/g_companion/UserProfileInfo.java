package cabiso.daphny.com.g_companion;

/**
 * Created by Lenovo on 8/22/2017.
 */

class UserProfileInfo {
    public String address;
    public String email;
    public String username;
    public String phone;

    UserProfileInfo(){

    }

    UserProfileInfo(String username, String email, String phone, String address){
        this.address = address;
        this.email = email;
        this.username = username;
        this.phone = phone;
    }
}
