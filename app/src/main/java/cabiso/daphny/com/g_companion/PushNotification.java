package cabiso.daphny.com.g_companion;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cicctuser on 9/16/2018.
 */

public class PushNotification {
    private static final String TAG = "PhoneAuthActivity";
    private String mUrl = "http://api.sportify.pw/gcompanion/push/notification";    //Request URL
    private String mMethod; //POST/GET
    private String mMessage;
    private String mTitle;
    private String mAccessToken;
    private Context mContext;
    private static RequestQueue requestQueue;
    public PushNotification(Context context){
        this.mMethod = "";
        this.mTitle = "";
        this.mMessage = "";
        this.mTitle = "";
        this.mAccessToken = "";
        this.mContext = context;
        if(this.requestQueue == null){
            this.requestQueue = Volley.newRequestQueue(context);
        }
    }

    public PushNotification url(String url){
        this.mUrl = url;
        return this;
    }

    public PushNotification method(String method){
        mMethod = method;
        return this;
    }

    public PushNotification message(String message){
        mMessage = message;
        return this;
    }

    public PushNotification title(String title){
        mTitle = title;
        return this;
    }

    public PushNotification accessToken(String accessToken){
        mAccessToken = accessToken;
        return this;
    }

    public void send(){
        RequestQueue queue = Volley.newRequestQueue(this.mContext);
        StringRequest postRequest = new StringRequest(Request.Method.POST, mUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if(error!=null) {
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", mAccessToken);
                params.put("message", mMessage);
                params.put("title",mTitle);

                return params;
            }
        };
        queue.add(postRequest);
//        }
    }

}
