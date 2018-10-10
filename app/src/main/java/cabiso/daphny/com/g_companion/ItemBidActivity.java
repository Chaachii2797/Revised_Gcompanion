package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.BookmarkAdapter;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by Lenovo on 10/9/2018.
 */

public class ItemBidActivity extends AppCompatActivity {

    private ArrayList<DIYnames> bookmrkList = new ArrayList<>();

    private ListView lv;
    private BookmarkAdapter adapter;
    private ProgressDialog progressDialog;

    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference userdataReference;
    private DatabaseReference bookmarkReference;

    private ArrayList<DBMaterial> dbMaterials;
    int count;

    private DatabaseReference soldReference;
    final ArrayList<String> keyList = new ArrayList<>();
    final ArrayList<String> keyListss = new ArrayList<>();

    public ItemBidActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        lv = (ListView) findViewById(R.id.bookmarkLv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();


        bookmarkReference = FirebaseDatabase.getInstance().getReference().child("ItemsBid").child(userID);

        bookmarkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    final DIYnames img = snapshot.getValue(DIYnames.class);
                    Log.e("imggg", String.valueOf(img));

                    bookmrkList.add(img);

                    //init adapter
                    adapter = new BookmarkAdapter(ItemBidActivity.this, R.layout.bookmark_item, bookmrkList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final DIYnames itemRef = adapter.getItem(position);

                            Toast.makeText(ItemBidActivity.this, "Clicked: " + " " + itemRef.getDiyName(), Toast.LENGTH_SHORT).show();

                            //send data to ViewRelatedDIYS using intent
                            Intent intent = new Intent(ItemBidActivity.this, ViewRelatedDIYS.class);
                            intent.putExtra("Nname", itemRef.diyName);
                            Log.e("Nname", itemRef.diyName);
                            startActivity(intent);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
