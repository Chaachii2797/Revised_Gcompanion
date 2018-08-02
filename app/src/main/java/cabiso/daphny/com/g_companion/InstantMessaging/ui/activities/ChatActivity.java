package cabiso.daphny.com.g_companion.InstantMessaging.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Date;
import java.util.GregorianCalendar;

import cabiso.daphny.com.g_companion.DateTime;
import cabiso.daphny.com.g_companion.DateTimePicker;
import cabiso.daphny.com.g_companion.InstantMessaging.Messaging;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.fragments.ChatFragment;
import cabiso.daphny.com.g_companion.InstantMessaging.utils.MessagingContants;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/21/2018.
 */

public class ChatActivity extends AppCompatActivity implements DateTimePicker.OnDateTimeSetListener{

    private Toolbar mToolbar;
    private ImageButton addPlan;


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
        addPlan = (ImageButton) findViewById(R.id.btnAddPlan);
    }

    private void init() {

        // set the toolbar
        setSupportActionBar(mToolbar);

        // set toolbar title
        mToolbar.setTitle(getIntent().getExtras().getString(MessagingContants.ARG_RECEIVER));

        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Clicked add plan button", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(ChatActivity.this, AddCalendarEventActivity.class);
//                startActivity(intent);

                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                calIntent.setType("vnd.android.cursor.item/event");
                calIntent.putExtra(CalendarContract.Events.TITLE, "G-Companion Meet-up");
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "At the mall");
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Get my DIY.");

                GregorianCalendar calDate = new GregorianCalendar(2018, 7, 1);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDate.getTimeInMillis());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDate.getTimeInMillis()+60*60*1000);
                calIntent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
                calIntent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

                startActivity(calIntent);

            }
        });

        //Calendar and Time
//        addPlan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(ChatActivity.this, "Clicked add plan button", Toast.LENGTH_SHORT).show();
//
//                // Create a SimpleDateTimePicker and Show it
//                SimpleDateTimePicker simpleDateTimePicker = SimpleDateTimePicker.make(
//                        "Set Date & Time for Meet-up",
//                        new Date(),
//                        ChatActivity.this,
//                        getSupportFragmentManager()
//                );
//                // Show It baby!
//                simpleDateTimePicker.show();
//
//            }
//        });


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
    public void DateTimeSet(Date date) {

        // This is the DateTime class we created earlier to handle the conversion
        // of Date to String Format of Date String Format to Date object
        DateTime mDateTime = new DateTime(date);
        // Show in the LOGCAT the selected Date and Time
        Log.e("TEST_TAG","Date and Time selected: " + mDateTime.getDateString());
        Toast.makeText(this, "Meet-up date and time: " + mDateTime.getDateString(), Toast.LENGTH_SHORT).show();

        EditText mETxtMessage = (EditText) findViewById(R.id.edit_text_message);
        mETxtMessage.setText(mDateTime.getDateString());
        Log.e("dateTime", " " + mDateTime.getDateString());


        //send data to ViewRelatedDIYS using intent
        Intent intent = new Intent(this, ChatFragment.class);
        intent.putExtra("dateTime", mDateTime.getDateString());
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
