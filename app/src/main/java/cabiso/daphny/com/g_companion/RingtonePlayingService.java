package cabiso.daphny.com.g_companion;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Random;

/**
 * Created by Lenovo on 7/27/2018.
 */

public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // fetch the extra string from the alarm on/alarm off values
        String state = intent.getExtras().getString("extra");

        // fetch the whale choice integer values
        Integer alarm_sound_choice = intent.getExtras().getInt("sound_choice");

        Log.e("Ringtone extra is ", state);
        Log.e("Sound choice is ", alarm_sound_choice.toString());


        // notification
        // set up the notification service
        NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // set up an intent that goes to the Main Activity
        Intent intent_main_activity = new Intent(this.getApplicationContext(), CalendarActivity.class);
        // set up a pending intent
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0,
                intent_main_activity, 0);

        // make the notification parameters
        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("ALARM!")
                .setContentText("Click me!")
                .setSmallIcon(R.drawable.new_logo_white)
                .setContentIntent(pending_intent_main_activity)
                .setAutoCancel(true)
                .build();


        // this converts the extra strings from the intent
        // to start IDs, values 0 or 1
        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                break;
            default:
                startId = 0;
                break;
        }


        // if else statements

        // if there is no music playing, and the user pressed "alarm on"
        // music should start playing
        if (!this.isRunning && startId == 1) {
            Log.e("there is no music, ", "and you want start");

            this.isRunning = true;
            this.startId = 0;



            notify_manager.notify(0, notification_popup);




            if (alarm_sound_choice == 0) {
                // play a randomly picked audio file

                int minimum_number = 1;
                int maximum_number = 20;

                Random random_number = new Random();
                int sound_number = random_number.nextInt(maximum_number + minimum_number);
                Log.e("random number is ", String.valueOf(sound_number));


                if (sound_number == 1) {
                    media_song = MediaPlayer.create(this, R.raw.aliens);
                    media_song.start();
                } else if (sound_number == 2) {
                    // create an instance of the media player
                    media_song = MediaPlayer.create(this, R.raw.chikka);
                    // start the ringtone
                    media_song.start();
                } else if (sound_number == 3) {
                    media_song = MediaPlayer.create(this, R.raw.dunes);
                    media_song.start();
                } else if (sound_number == 4) {
                    media_song = MediaPlayer.create(this, R.raw.hair);
                    media_song.start();
                } else if (sound_number == 5) {
                    media_song = MediaPlayer.create(this, R.raw.here);
                    media_song.start();
                }
                 else if (sound_number == 6) {
                    media_song = MediaPlayer.create(this, R.raw.holahola);
                    media_song.start();
                } else if (sound_number == 7) {
                    media_song = MediaPlayer.create(this, R.raw.kokobop);
                    media_song.start();
                } else if (sound_number == 8) {
                    media_song = MediaPlayer.create(this, R.raw.littlethings);
                    media_song.start();
                } else if (sound_number == 9) {
                    media_song = MediaPlayer.create(this, R.raw.newromantics);
                    media_song.start();
                } else if (sound_number == 10) {
                    media_song = MediaPlayer.create(this, R.raw.orchestra);
                    media_song.start();
                }
                else if (sound_number == 11) {
                    media_song = MediaPlayer.create(this, R.raw.oriental);
                    media_song.start();
                } else if (sound_number == 12) {
                    media_song = MediaPlayer.create(this, R.raw.peligro);
                    media_song.start();
                } else if (sound_number == 13) {
                    media_song = MediaPlayer.create(this, R.raw.piano);
                    media_song.start();
                } else if (sound_number == 14) {
                    media_song = MediaPlayer.create(this, R.raw.rain);
                    media_song.start();
                }
//                else if (sound_number == 15) {
//                    media_song = MediaPlayer.create(this, R.raw.salute);
//                    media_song.start();
//                }
//                else if (sound_number == 16) {
//                    media_song = MediaPlayer.create(this, R.raw.scarstoyourbeautiful);
//                    media_song.start();
//                } else if (sound_number == 17) {
//                    media_song = MediaPlayer.create(this, R.raw.superman);
//                    media_song.start();
//                } else if (sound_number == 18) {
//                    media_song = MediaPlayer.create(this, R.raw.talk);
//                    media_song.start();
//                } else if (sound_number == 19) {
//                    media_song = MediaPlayer.create(this, R.raw.welcometonewyork);
//                    media_song.start();
//                }
                else if (sound_number == 20) {
                    media_song = MediaPlayer.create(this, R.raw.touch);
                    media_song.start();
                }
                else {
                    media_song = MediaPlayer.create(this, R.raw.touch);
                    media_song.start();
                }

            }
            else if (alarm_sound_choice == 1) {
                // create an instance of the media player
                media_song = MediaPlayer.create(this, R.raw.aliens);
                // start the ringtone
                media_song.start();
            }
            else if (alarm_sound_choice == 2) {
                // create an instance of the media player
                media_song = MediaPlayer.create(this, R.raw.chikka);
                // start the ringtone
                media_song.start();
            }
            else if (alarm_sound_choice == 3) {
                media_song = MediaPlayer.create(this, R.raw.dunes);
                media_song.start();
            }
            else if (alarm_sound_choice == 4) {
                media_song = MediaPlayer.create(this, R.raw.hair);
                media_song.start();
            }
            else if (alarm_sound_choice == 5) {
                media_song = MediaPlayer.create(this, R.raw.here);
                media_song.start();
            }
            else if (alarm_sound_choice == 6) {
                media_song = MediaPlayer.create(this, R.raw.holahola);
                media_song.start();
            }
            else if (alarm_sound_choice == 7) {
                media_song = MediaPlayer.create(this, R.raw.kokobop);
                media_song.start();
            }
            else if (alarm_sound_choice == 8) {
                media_song = MediaPlayer.create(this, R.raw.littlethings);
                media_song.start();
            }
            else if (alarm_sound_choice == 9) {
                media_song = MediaPlayer.create(this, R.raw.newromantics);
                media_song.start();
            }
            else if (alarm_sound_choice == 10) {
                media_song = MediaPlayer.create(this, R.raw.orchestra);
                media_song.start();
            }
            else if (alarm_sound_choice == 11) {
                media_song = MediaPlayer.create(this, R.raw.oriental);
                media_song.start();
            }
            else if (alarm_sound_choice == 12) {
                media_song = MediaPlayer.create(this, R.raw.peligro);
                media_song.start();
            }
            else if (alarm_sound_choice == 13) {
                media_song = MediaPlayer.create(this, R.raw.piano);
                media_song.start();
            }
            else if (alarm_sound_choice == 14) {
                media_song = MediaPlayer.create(this, R.raw.rain);
                media_song.start();
            }
//            else if (alarm_sound_choice == 15) {
//                media_song = MediaPlayer.create(this, R.raw.salute);
//                media_song.start();
//            }
//            else if (alarm_sound_choice == 16) {
//                media_song = MediaPlayer.create(this, R.raw.scarstoyourbeautiful);
//                media_song.start();
//            }
//            else if (alarm_sound_choice == 17) {
//                media_song = MediaPlayer.create(this, R.raw.superman);
//                media_song.start();
//            }
//            else if (alarm_sound_choice == 18) {
//                media_song = MediaPlayer.create(this, R.raw.talk);
//                media_song.start();
//            }
//            else if (alarm_sound_choice == 19) {
//                media_song = MediaPlayer.create(this, R.raw.welcometonewyork);
//                media_song.start();
//            }
            else if (alarm_sound_choice == 20) {
                media_song = MediaPlayer.create(this, R.raw.touch);
                media_song.start();
            }
            else {
                media_song = MediaPlayer.create(this, R.raw.touch);
                media_song.start();
            }



        }


        // if there is music playing, and the user pressed "alarm off"
        // music should stop playing
        else if (this.isRunning && startId == 0) {
            Log.e("there is music, ", "and you want end");

            // stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        }


        // these are if the user presses random buttons
        // just to bug-proof the app
        // if there is no music playing, and the user pressed "alarm off"
        // do nothing
        else if (!this.isRunning && startId == 0) {
            Log.e("there is no music, ", "and you want end");

            this.isRunning = false;
            this.startId = 0;

        }

        // if there is music playing and the user pressed "alarm on"
        // do nothing
        else if (this.isRunning && startId == 1) {
            Log.e("there is music, ", "and you want start");

            this.isRunning = true;
            this.startId = 1;

        }


        // can't think of anything else, just to catch the odd event
        else {
            Log.e("else ", "somehow you reached this");

        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy () {
        // Tell the user we stopped.
        Log.e("on Destroy called", "ta da");

        super.onDestroy();
        this.isRunning = false;


    }
}
