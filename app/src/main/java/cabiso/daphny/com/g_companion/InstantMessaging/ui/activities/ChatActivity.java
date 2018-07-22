package cabiso.daphny.com.g_companion.InstantMessaging.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import cabiso.daphny.com.g_companion.InstantMessaging.Messaging;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.fragments.ChatFragment;
import cabiso.daphny.com.g_companion.InstantMessaging.utils.MessagingContants;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/21/2018.
 */

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(MessagingContants.ARG_RECEIVER, receiver);
        intent.putExtra(MessagingContants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(MessagingContants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {

        // set the toolbar
        setSupportActionBar(mToolbar);

        // set toolbar title
        mToolbar.setTitle(getIntent().getExtras().getString(MessagingContants.ARG_RECEIVER));

        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(MessagingContants.ARG_RECEIVER),
                        getIntent().getExtras().getString(MessagingContants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(MessagingContants.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Messaging.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Messaging.setChatActivityOpen(false);
    }
}
