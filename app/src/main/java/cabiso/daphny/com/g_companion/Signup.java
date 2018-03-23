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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    private String TAG;
    private Button signup;
    private EditText email, password, username, address;
    private TextView login;
    private FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreference session;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        username = (EditText) findViewById(R.id.userName);
        address = (EditText) findViewById(R.id.userAddress);
        login = (TextView) findViewById(R.id.tvLogin);
        signup = (Button) findViewById(R.id.btnLogin);

        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
// get editor to edit in file
        editor = sharedPreferences.edit();

        signup.setOnClickListener(new View.OnClickListener() {

            public void onClick (View v) {
                String name = username.getText().toString();
                String emails = email.getText().toString();
                String pass = password.getText().toString();
                String add = address.getText().toString();

                if(username.getText().length()<=0){
                    Toast.makeText(Signup.this, "Enter name", Toast.LENGTH_SHORT).show();
                }
                else if( email.getText().length()<=0){
                    Toast.makeText(Signup.this, "Enter email", Toast.LENGTH_SHORT).show();
                }
                else if( password.getText().length()<=0){
                    Toast.makeText(Signup.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                else if( address.getText().length()<=0){
                    Toast.makeText(Signup.this, "Enter Address", Toast.LENGTH_SHORT).show();
                }
                else{
                    // as now we have information in string. Lets stored them with the help of editor
                    editor.putString("Name", name);
                    editor.putString("Email",emails);
                    editor.putString("txtPassword",pass);
                    editor.putString("txtAddress",add);

                    editor.commit();}   // commit the values

                // after saving the value open next activity
                Intent ob = new Intent(Signup.this, Login.class);
                startActivity(ob);

            }
        });

//        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
//                getSupportFragmentManager().findFragmentById(R.id.userAddress);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                Log.e(TAG, "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.e(TAG, "An error occurred: " + status);
//            }
//        });

    }
    public void register(){
        String em = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String name = username.getText().toString().trim();
        String add = address.getText().toString().trim();

        if(TextUtils.isEmpty(em) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(name) || TextUtils.isEmpty(add)){
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
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                        }
                    }
                });
    }



    @Override
    public void onClick(View v) {
        if(v == signup){
            register();
        }else if(v==login){
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }
}
