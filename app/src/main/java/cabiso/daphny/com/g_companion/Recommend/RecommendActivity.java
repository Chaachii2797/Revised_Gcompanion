package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.UploadItems;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.UploadDIYAdapter;

public class RecommendActivity extends AppCompatActivity {

    private List<UploadItems> diyList;
    private ListView lv;
    private UploadDIYAdapter adapter;
    private ProgressDialog progressDialog;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    //private FirebaseRecyclerAdapter<DIYitem, HomePageActivityViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        diyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_view_upload_diy);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //fetch images from firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UploadItems img = snapshot.getValue(UploadItems.class);
//                    diyList.add(img);
                    Query query = databaseReference.orderByChild("paper").equalTo("Egg Cartoon Lamp");

                }
                //init adapter
                adapter = new UploadDIYAdapter(RecommendActivity.this, R.layout.fragment_ui_items, diyList);
                //set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
