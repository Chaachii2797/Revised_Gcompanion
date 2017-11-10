package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

public class Bookmark_Activity extends AppCompatActivity {
    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseReference;
    private DatabaseReference categoryReference;

    private ImageButton heart, star;
    private int count=0;

    public Bookmark_Activity(){
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);
//        if(lv!=null) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        heart = (ImageButton) findViewById(R.id.heartu);
        star = (ImageButton) findViewById(R.id.staru);

        database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("bookmarks").child(userID);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                progressDialog.dismiss();

                Toast.makeText(Bookmark_Activity.this, "HEY!", Toast.LENGTH_SHORT).show();
                DIYnames img = dataSnapshot.getValue(DIYnames.class);
                diyList.add(img);
                Toast.makeText(Bookmark_Activity.this, "HEY!"+img.getDiyName(), Toast.LENGTH_SHORT).show();
                adapter = new RecommendDIYAdapter(Bookmark_Activity.this, R.layout.recommend_ui, diyList);
                lv.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
