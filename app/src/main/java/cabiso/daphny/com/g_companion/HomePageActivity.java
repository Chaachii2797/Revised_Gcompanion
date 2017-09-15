package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYDetails;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class HomePageActivity extends AppCompatActivity {

    private List<DIYDetails> diyList;
    private ListView lv;
    private DIYListAdapter adapter;
    private ProgressDialog progressDialog;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    //private FirebaseRecyclerAdapter<DIYitem, HomePageActivityViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public HomePageActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        diyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_view_diy);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("category").child("bottle");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //fetch images from firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DIYDetails img = snapshot.getValue(DIYDetails.class);
                    diyList.add(img);
                }
                //init adapter
                adapter = new DIYListAdapter(HomePageActivity.this, R.layout.homepage_item, diyList);
                //set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        //     recyclerView = (RecyclerView) findViewById(R.id.show_diy_recycler_view);
        //    recyclerView.setLayoutManager(new LinearLayoutManager(HomePageActivity.this));
        //  Toast.makeText(HomePageActivity.this, "Wait! Fetching data....", Toast.LENGTH_SHORT).show();

    }
}