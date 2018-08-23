package cabiso.daphny.com.g_companion.notifications;

/**
 * Created by cicctuser on 8/22/2018.
 */
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Acer on 15/08/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String API_KEY = "1abdd08c349f4895b3f1162a7f2daa9d";
    private String URL;
    private String userToken;
    private Context context;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference mDatabaseReference;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        URL = "http://api.sportify.pw//api/access_token?api_key=" + API_KEY;
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.
//        GlobalVariable globalVariable = (GlobalVariable) this.getApplication();
//        final String userToken = globalVariable.getUserToken();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_token", token);
        mDatabaseReference.child("userdata").child(userID).updateChildren(result);
    }

    public String getUserToken() {
        return userToken;
    }

    public MyFirebaseInstanceIDService(String userToken, Context context) {
        this.userToken = userToken;
        this.context = context;
    }

    public MyFirebaseInstanceIDService() {

    }
}
