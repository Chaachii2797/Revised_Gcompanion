package cabiso.daphny.com.g_companion.InstantMessaging.core.core.logout;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Lenovo on 7/21/2018.
 */

public class LogoutInteractor implements LogoutContract.Interactor {

    private LogoutContract.OnLogoutListener mOnLogoutListener;

    public LogoutInteractor(LogoutContract.OnLogoutListener onLogoutListener) {
        mOnLogoutListener = onLogoutListener;
    }

    @Override
    public void performFirebaseLogout() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            mOnLogoutListener.onSuccess("Successfully logged out!");
        } else {
            mOnLogoutListener.onFailure("No user logged in yet!");
        }
    }
}
