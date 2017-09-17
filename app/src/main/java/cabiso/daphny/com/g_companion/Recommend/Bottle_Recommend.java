package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Bottle_Recommend extends AppCompatActivity {

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

    //private FirebaseRecyclerAdapter<DIYitem, HomePageActivityViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    public Bottle_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        //init adapter
        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.recommend_ui, diyList);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIY_Methods").child("category");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //fetch images from firebase
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    HashMap<String, Long> m = (HashMap<String, Long>) snapshot.getValue();
//                    DIYrecommend img = snapshot.getValue(DIYrecommend.class);
//                    Log.d("test", String.valueOf(m.get("diyName")));
//                    Log.d("NAME: "+m.get("diyName"), "");
//                    String codeValue = img.getDiyName();
//                    Log.d("VALUE: "+codeValue,"");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DIYrecommend img = snapshot.getValue(DIYrecommend.class);
                    diyList.add(img);
                }
                //init adapter
                adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.recommend_ui, diyList);
                //set adapter for listview
                lv.setAdapter(adapter);

                //set adapter for listview
                lv.setAdapter(adapter);
//                    lv.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        //     recyclerView = (RecyclerView) findViewById(R.id.show_diy_recycler_view);
        //    recyclerView.setLayoutManager(new LinearLayoutManager(HomePageActivity.this));
        //  Toast.makeText(HomePageActivity.this, "Wait! Fetching data....", Toast.LENGTH_SHORT).show();

    }
}