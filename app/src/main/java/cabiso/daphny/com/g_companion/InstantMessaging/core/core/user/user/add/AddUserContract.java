package cabiso.daphny.com.g_companion.InstantMessaging.core.core.user.user.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Lenovo on 7/21/2018.
 */

public interface AddUserContract {

    interface View {
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
