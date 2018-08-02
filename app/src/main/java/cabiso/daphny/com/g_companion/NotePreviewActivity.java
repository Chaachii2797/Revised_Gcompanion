package cabiso.daphny.com.g_companion;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Lenovo on 7/29/2018.
 */

public class NotePreviewActivity extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_preview_activity);

        TextView note = (TextView) findViewById(R.id.note);

        Intent intent = getIntent();

//        final String get_name = getIntent().getStringExtra("EVENT");
//        note.setText(get_name);
//        Log.e("get_name", "" + get_name);


        if (intent != null) {
            Object event = intent.getParcelableExtra(AddCalendarEventActivity.EVENT);
            if(event instanceof MyEventDay){

                MyEventDay myEventDay = (MyEventDay)event;
//                getSupportActionBar().setTitle(getFormattedDate(myEventDay.getCalendar().getTime()));
                note.setText("Event for today: " + " " + myEventDay.getNote().toUpperCase());
                Log.e("myEventDay",myEventDay.getNote());
                return;
            }
            if(event instanceof EventDay){
                EventDay eventDay = (EventDay)event;
                getSupportActionBar().setTitle(getFormattedDate(eventDay.getCalendar().getTime()));
            }
        }
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMMyyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }


}
