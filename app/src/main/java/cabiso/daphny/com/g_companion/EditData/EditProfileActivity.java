package cabiso.daphny.com.g_companion.EditData;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/13/2018.
 */

public class EditProfileActivity extends AppCompatActivity {

    private DatabaseReference editProfileReference;
    private String profileUserId, profileUserName, profileEmailAdd, profilePass, profileNumber, profileAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        Intent intent = getIntent();
        profileUserId = intent.getExtras().getString("profileUserId");
        profileUserName = intent.getExtras().getString("profileUserName");
        profileEmailAdd = intent.getExtras().getString("profileEmailAdd");
        profilePass = intent.getExtras().getString("profilePass");
        profileNumber = intent.getExtras().getString("profileNumber");
        profileAddress = intent.getExtras().getString("profileAddress");

        Log.e("profileUserIds", profileUserId);


        editProfileReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(profileUserId);


        final TextView username = (TextView) findViewById(R.id.name);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView password = (TextView) findViewById(R.id.password);
        final TextView phone = (TextView)findViewById(R.id.phone);
        final TextView address = (TextView) findViewById(R.id.address);


        final EditText profile_username = (EditText) findViewById(R.id.profile_name);
        profile_username.setText(profileUserName);

        final EditText profile_email = (EditText) findViewById(R.id.profile_email);
        profile_email.setText(profileEmailAdd);

        final EditText profile_password = (EditText) findViewById(R.id.profile_password);
        profile_password.setText(profilePass);

        final EditText profile_phone = (EditText)findViewById(R.id.profile_phone);
        profile_phone.setText(profileNumber);

        final EditText profile_address = (EditText) findViewById(R.id.profile_address);
        profile_address.setText(profileAddress);

        final ImageView profile_picture = (ImageView) findViewById(R.id.profile_picture);
        final Button save_edit_profile_btn = (Button) findViewById(R.id.profile_submit_edit);


        //save edit data
        save_edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> results = new HashMap<>();
                results.put("f_name", profile_username.getText().toString());
                results.put("email", profile_email.getText().toString());
                results.put("password", profile_password.getText().toString());
                results.put("contact_no", profile_phone.getText().toString());
                results.put("address", profile_address.getText().toString());

                editProfileReference.updateChildren(results);

                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });




    }
}
