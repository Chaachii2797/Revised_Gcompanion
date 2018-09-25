package cabiso.daphny.com.g_companion.InstantMessaging.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import cabiso.daphny.com.g_companion.InstantMessaging.utils.MessagingContants;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/21/2018.
 */

public class ChatSplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_splash);

        mHandler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                // check if user is already logged in or not
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // if logged in redirect the user to user listing activity
                    UserListingActivity.startActivity(ChatSplashActivity.this);
//                    Intent intent = new Intent(ChatSplashActivity.this,ChatActivity.class);
//                    startActivity(intent);
                } else {
                    // otherwise redirect the user to login activity
                    LoginActivity.startIntent(ChatSplashActivity.this);
                }
                finish();
            }
        };

        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
    }*/
}
