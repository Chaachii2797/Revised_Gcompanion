package cabiso.daphny.com.g_companion.InstantMessaging.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Lenovo on 7/21/2018.
 */

@IgnoreExtraProperties
public class User {

    public String userID;
    public String email;
    public String firebaseToken;

    public User() {
    }

    public User(String userID, String email, String firebaseToken) {
        this.userID = userID;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }


}
