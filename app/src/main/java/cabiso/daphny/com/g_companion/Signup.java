package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.User_Profile;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private String TAG;
    private String userID;
    private Button signup;
    private EditText email, password, lname, fname, contact, address;
    private TextView login;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference databaseReference;

    private Integer THRESHOLD = 2;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreference session;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("userdata");

        fname = (EditText) findViewById(R.id.et_fName);
        lname = (EditText) findViewById(R.id.et_lName);
        email = (EditText) findViewById(R.id.et_Email);
        contact = (EditText) findViewById(R.id.et_Contact);
        address = (EditText) findViewById(R.id.et_Address);
        password = (EditText) findViewById(R.id.et_Password);

        signup = (Button) findViewById(R.id.btn_signup);
        login = (TextView) findViewById(R.id.tv_signup);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
//        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
//
//// get editor to edit in file
//        editor = sharedPreferences.edit();


    }
    public void register(final User_Profile user_profile){
        final String em = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();

        if(TextUtils.isEmpty(em) || TextUtils.isEmpty(pass)){
            Toast.makeText(this,"Please enter valid email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(em, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(Signup.this,"Registration Error!", Toast.LENGTH_SHORT).show();
                        }else{
                            FirebaseUser user = task.getResult().getUser();
                            Log.e("SignUpUID",user.getUid());
                            user_profile.setUserID(user.getUid());
                            databaseReference.child(user.getUid()).setValue(user_profile);
                            mAuth.signOut();
                            Toast.makeText(Signup.this, "Successfully registered a new user", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == signup){
            signup.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    String f_name = fname.getText().toString();
                    String l_name = lname.getText().toString();
                    String emails = email.getText().toString();
                    String contct = contact.getText().toString();
                    String add = address.getText().toString();
                    String pass = password.getText().toString();

                    if(f_name.isEmpty() && l_name.isEmpty()) {
                        fname.setError("First Name cannot be empty!");
                        lname.setError("Last Name cannot be empty!");
                    }else if(emails.isEmpty()){
                        email.setError("Email Address cannot be empty!");
                    }
                    else if(contct.isEmpty()){
                        contact.setError("Contact Number cannot be empty!");
                    }else if(add.isEmpty()){
                        address.setError("Address cannot be empty!");
                    }else if(pass.isEmpty()){
                        password.setError("Password Number cannot be empty!");
                    }else{
                        // as now we have information in string. Lets stored them with the help of editor
//                        editor.putString("First Name", f_name);
//                        editor.putString("Last Name", l_name);
//                        editor.putString("Email",emails);
//                        editor.putString("txtPassword",pass);
//                        editor.putString("txtContact",contct);
//                        editor.putString("txtAddress",add);
////                        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        editor.commit();

                        // after saving the value open next activity
//                        Intent ob = new Intent(Signup.this, Login.class);
                        User_Profile user_profile = new User_Profile(add, contct, f_name, l_name, emails, pass, userID);
                        register(user_profile);
//                        startActivity(ob);
                    }   // commit the values
                }
            });
        }else if(v==login){
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }
}
