package cabiso.daphny.com.g_companion.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cabiso.daphny.com.g_companion.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
//        MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
//        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationActivity.this.registerReceiver(mMessageReceiver, new IntentFilter("updateNotification"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),intent.getAction(),Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("actLifeCycle", "ni sud sa pause");
    }
}
