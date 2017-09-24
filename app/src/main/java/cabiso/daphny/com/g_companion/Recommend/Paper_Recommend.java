package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Paper_Recommend extends AppCompatActivity {

    private ArrayList<DIYrecommend> diyList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private LinearLayoutManager mLinearLayoutManager;

    public Paper_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper__recommend);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        //init adapter
        adapter = new RecommendDIYAdapter(Paper_Recommend.this, R.layout.recommend_ui, diyList);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIY_Methods").child("category").child("paper");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    DIYrecommend img = snapshot.getValue(DIYrecommend.class);
                    String image = (String) map.get("0");
                    diyList.add(img);

                }
                //init adapter
                adapter = new RecommendDIYAdapter(Paper_Recommend.this, R.layout.recommend_ui, diyList);
                //set adapter for listview
                lv.setAdapter(adapter);

                //set adapter for listview
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Paper_Recommend.this, MainActivity.class);
        startActivity(intent);
    }
}