package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import cabiso.daphny.com.g_companion.Model.User_Profile;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private Button login;
    private Button withGoogle;
    private SignInButton signInButton;
    private EditText email;
    private EditText password;
    private TextView signup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mFirebaseUser;
    private String userID;

    private boolean isEmailValid;
    private boolean isPasswordValid;

    public static final String PREFS_NAME = "MyPrefsFile";
    DatabaseReference userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        userInfo = FirebaseDatabase.getInstance().getReference().child("userdata");

//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); // 0 - for private mode
//        SharedPreferences.Editor editor = settings.edit();
//
//        //Set "hasLoggedIn" to true
//        editor.putBoolean("hasLoggedIn", true);
//
//        // Commit the edits!
//        editor.commit();
//
//        //Kani kay mo deretso sa main. Pero ika logout gg! mo stop besh!
//        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
//        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", true);
//
//        if (hasLoggedIn) {
//            //Go directly to main activity.
//            Intent intent = new Intent(Login.this, MainActivity.class);
//            startActivity(intent);
//            this.finish();
//        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.etPassword);
        signup = (TextView) findViewById(R.id.tvSignup);
        login = (Button) findViewById(R.id.btn_signup);
        withGoogle = (Button) findViewById(R.id.btnWithGoogle);
        withGoogle.setOnClickListener(this);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        login.setEnabled(false); // default state should be disabled mBtnLogin.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Log.e("USERID", userID+"");
                    startActivity(intent);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    String token= FirebaseInstanceId.getInstance().getToken();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("userdata");
                    ref.child(user.getUid()).child("access_token").setValue(token);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void validatePassword(String text) {
        isPasswordValid = !text.isEmpty();
    }

    private void validateEmail(String text) {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void updateLoginButtonState() {
        if (isEmailValid && isPasswordValid) {
            login.setEnabled(true);
        } else {
            login.setEnabled(false);
        }
    }

//////If naa siya, naa gihapon ang log in everytime, pero ika back muadto sa main dayon
//    @Override
//    protected void onStop(){
//        super.onStop();
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
////Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
//        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", true);
//
//        if(hasLoggedIn)
//        {
//            //Go directly to main activity.
//            Intent intent = new Intent(Login.this,MainActivity.class);
//            startActivity(intent);
//            this.finish();
//
//        }
//    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if(signInIntent!=null){
            startActivityForResult(signInIntent, RC_SIGN_IN);
            if(signInIntent.equals(RC_SIGN_IN)){
                Intent intent = new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(this,"na Login sha!.. PRETTY",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"WALA na Login sha!.. BATIG NAWNG",Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
        }
    }

    public void signin(){
        final String em = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        showProgressDialog();

        if(TextUtils.isEmpty(em) && TextUtils.isEmpty(pass)){
            Log.d(TAG,"Empty field!");
            Intent intent = new Intent(Login.this, Login.class);
            startActivity(intent);
        }else{
            mAuth.signInWithEmailAndPassword(em, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG,"Signin with email: onComplete" + task.isSuccessful());
                            if(!task.isSuccessful()){
                                Log.d(TAG,"Signin email: failed!" + task.getException());
                                Toast.makeText(Login.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                            }
                            else if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                Toast.makeText(Login.this, "Logging in...", Toast.LENGTH_SHORT).show();
                                startActivity(intent);




                            }
                            else{
                                hideProgressDialog();
                            }
                        }
                    });
        }

        if(!isNetworkOn(getBaseContext())) {
            Toast.makeText(getBaseContext(), "No network connection", Toast.LENGTH_SHORT).show();
        } else {
            // do login
        }

    }

    public boolean isNetworkOn(@NotNull Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    @Override
    public void onClick(View v) {
        if (v==login){
//            Intent intent = new Intent(Login.this, MainActivity.class);
//            startActivity(intent);

            userInfo.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.e("dataSnap", dataSnapshot.getKey());
                    for(DataSnapshot datas : dataSnapshot.getChildren()){
                        Log.e("datss", datas.getKey());
                        User_Profile userss = dataSnapshot.getValue(User_Profile.class);
                        Log.e("userrs", userss.getF_name());

                        if(userss.getEmail().equals(email.getText().toString())){
                            Log.e("userName", userss.getEmail() + " = " + email.getText().toString());
                            if(userss.getPassword().equals(password.getText().toString())){
                                Log.e("userPass", userss.getPassword() + " = " + password.getText().toString());

                                if(userss.getReport_status().equalsIgnoreCase("Unblock")){
                                    Log.e("userStatuss", userss.getReport_status());

                                    signin();

                                } else{
                                    Toast.makeText(Login.this, "You have been blocked by the system for violating some policies.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }else if(v==signup){
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        }else if(v==withGoogle){
            signInGoogle();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(R.style.ProgressBar);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}