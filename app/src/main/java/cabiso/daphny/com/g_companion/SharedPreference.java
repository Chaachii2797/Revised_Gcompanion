package cabiso.daphny.com.g_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Lenovo on 2/3/2018.
 */

public class SharedPreference {

    SharedPreferences sharepreferences;

    public static SharedPreference instance = null;

    public static SharedPreference getInstance()
    {

        if (instance == null) {
            synchronized (SharedPreference.class) {
                instance = new SharedPreference();
            }
        }
        return instance;
    }
    public void saveISLogged_IN(Context context, Boolean isLoggedin) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharepreferences.edit();
        editor.putBoolean("IS_LOGIN", isLoggedin);
        editor.commit();
    }

    public boolean getISLogged_IN(Context context) {
        sharepreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharepreferences.getBoolean("IS_LOGIN", false);
    }
}