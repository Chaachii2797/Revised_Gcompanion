package cabiso.daphny.com.g_companion.InstantMessaging;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Messaging extends AppCompatActivity {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        Messaging.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

}
