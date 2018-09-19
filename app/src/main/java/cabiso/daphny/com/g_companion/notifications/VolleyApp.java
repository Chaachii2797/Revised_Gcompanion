package cabiso.daphny.com.g_companion.notifications;

/**
 * Created by cicctuser on 9/17/2018.
 */

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Basilan on 9/1/2017.
 */

public class VolleyApp extends Application {

    public static final String TAG = VolleyApp.class.getSimpleName();

    private static VolleyApp sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = new VolleyApp();
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyApp getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
//        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        String errorMsg;
        if (!isNetworkConnected()) {
//            errorMsg = getResources().getString("No Internet");
            Toast.makeText(this, "No Internet", Toast.LENGTH_LONG).show();
        }else{
            VolleyLog.d("Adding request to queue: %s", request.getUrl());
            request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 5, 1.0f));
            getRequestQueue().add(request);
        }


    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            Log.d("naCancel","Na Cancel Na");
            mRequestQueue.cancelAll(tag);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
