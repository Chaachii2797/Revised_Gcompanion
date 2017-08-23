package cabiso.daphny.com.g_companion.Maps;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cabiso.daphny.com.g_companion.Model.ApplyUnitData;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/20/2017.
 */

public class ViewSearchedDIYS extends AppCompatActivity{

    TextView mtvTitle, mtvPrice, mtvDesc, mtvAddress, mtvLocation, mtvCond, mtvBed, mtvBath, mtvLName, mtvLContact, mtvLEmail;
    FloatingActionButton mApply;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mApartmentsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_searched_apartments);

        mtvTitle = (TextView) findViewById(R.id.tvTitle);
        mtvDesc = (TextView) findViewById(R.id.tvDescription);
        mtvLocation = (TextView) findViewById(R.id.tvLocation);
        mtvCond = (TextView) findViewById(R.id.tvCondition);
        mtvBath = (TextView) findViewById(R.id.tvNumofBaths);
        mtvBed = (TextView) findViewById(R.id.tvNumofBeds);
        mtvAddress = (TextView) findViewById(R.id.tvMyApartmentAddress);
        mtvPrice = (TextView) findViewById(R.id.tvPrice);
        mtvLName = (TextView) findViewById(R.id.tvALandlordName);
        mtvLContact = (TextView) findViewById(R.id.tvLandlordContact);
        mtvLEmail = (TextView) findViewById(R.id.tvLandlordEmail);
        mApply = (FloatingActionButton) findViewById(R.id.applyApartment);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mApartmentsDatabaseReference = mFirebaseDatabase.getReference("Data").child("ApplyApartment");

        Intent i = getIntent();
        final Bundle data = i.getExtras();

        mtvTitle.setText(data.getString("Title"));
        mtvDesc.setText(data.getString("Desc"));
        mtvLocation.setText("Location: " + data.getString("Location"));
        mtvCond.setText("Condition: " + data.getString("Cond"));
        mtvBed.setText("Number of Bedroom: " + String.valueOf(data.get("NumBeds")));
        mtvBath.setText("Number of Bathroom: " + String.valueOf(data.get("NumBath")));
        mtvAddress.setText("Address: " + data.getString("Address"));
        mtvPrice.setText("Php " + String.valueOf(data.get("Price")));
        mtvLName.setText("Name: " + data.getString("LandLordName"));
        mtvLEmail.setText("Email: " + data.getString("Email"));
        mtvLContact.setText("Contact: " + data.getString("Contact"));

        mApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ViewSearchedDIYS.this);
                dialog.setTitle("Confirm Application")
                        .setMessage("Do you want to send application to this apartment?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                ApplyUnitData applyUnitData = new ApplyUnitData();
                                applyUnitData.setApartmentID(data.getString("ID"));
                                applyUnitData.setTenantName(data.getString("TenantName"));
                                applyUnitData.setTenantEmail(data.getString("TenantEmail"));
                                mApartmentsDatabaseReference.child("Application").setValue(applyUnitData);
                                Toast.makeText(ViewSearchedDIYS.this, "Application Sent! Wait for confirmation", Toast.LENGTH_SHORT).show();
                                mApply.setClickable(false);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                Toast.makeText(ViewSearchedDIYS.this, "Application canceled", Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = getIntent();
        Bundle data = i.getExtras();

        switch (item.getItemId()) {
            case R.id.ViewMaps:
                Intent x = new Intent(ViewSearchedDIYS.this, SearchApartmentsMaps.class);
                x.putExtra("Address", data.getString("Address"));
                x.putExtra("Location", data.getString("Location"));
                startActivity(x);
        }

        return super.onOptionsItemSelected(item);
    }

}
