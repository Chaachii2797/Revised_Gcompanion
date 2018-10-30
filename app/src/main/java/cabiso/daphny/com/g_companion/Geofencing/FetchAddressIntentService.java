package cabiso.daphny.com.g_companion.Geofencing;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * Created by cicctuser on 10/6/2018.
 */

public class FetchAddressIntentService extends IntentService {

    private static final String TAG = "FetchAddressIntent";
    protected ResultReceiver mReceiver;


    public FetchAddressIntentService(){
        super("FetchAddressIntentService");
    }

    public FetchAddressIntentService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        // Get the receiver passed to this service through an extra
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);

            //TODO: testing rani
            Log.d("localityTest",addresses.get(0).getLocality());

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "Error";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Error";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Error";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage,"none");
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Success");
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments),addresses.get(0).getLocality());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, String locality) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putString(Constants.RESULT_DATA_KEY2,locality);
        mReceiver.send(resultCode, bundle);
    }

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                "com.google.android.gms.location.sample.locationaddress";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String RESULT_DATA_KEY2 = PACKAGE_NAME +
                ".RESULT_DATA_KEY2";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }

}
