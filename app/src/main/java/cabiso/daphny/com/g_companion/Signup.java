package cabiso.daphny.com.g_companion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
