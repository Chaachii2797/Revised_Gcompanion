package cabiso.daphny.com.g_companion.InstantMessaging.core.core.logout;

/**
 * Created by Lenovo on 7/21/2018.
 */

public interface LogoutContract {

    interface View {
        void onLogoutSuccess(String message);

        void onLogoutFailure(String message);
    }

    interface Presenter {
        void logout();
    }

    interface Interactor {
        void performFirebaseLogout();
    }

    interface OnLogoutListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
