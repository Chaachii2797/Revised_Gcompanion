package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;


/**
 * Created by Lenovo on 7/31/2017.
 */

public class Recommendation extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference communityReference;

    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<QuantityItem> qty_list = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();

    private ArrayList<DBMaterial> dbMaterials;

    private ListView lv;
    private View messageView;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ProgressBar mprogressBar;


    // ImageButton star;

    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    //ArrayList<CommunityItem> itemMaterial;
    private List<String> tags = new ArrayList<>();
    //    private List<Integer> numberList = new ArrayList<>();
    private Activity context;


    List<String> messageProcedure = new ArrayList<String>();
    List<String> arrayList_num_qty = new ArrayList<String>();
    List<String> arrayList_qty = new ArrayList<String>();
    List<String> check = new ArrayList<String>();

    private User_Profile user;
    Boolean addDiy;

    public Recommendation() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);
        lv = (ListView) findViewById(R.id.recommendLvView);

        addDiy = true;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        lv = (ListView) findViewById(R.id.recommendLvView);
        messageView = (View) findViewById(R.id.showMessageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait loading DIYs...");

        final Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");
        Log.e("FROMIMAGE", String.valueOf(dbMaterials));
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        adapter = new RecommendDIYAdapter(Recommendation.this, R.layout.pending_layout, diyList);
        lv.setAdapter(adapter);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userdata");
        userRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User_Profile.class);

                Recommend recommend = new Recommend(dbMaterials,user);
                recommend.setRecommendedItems(diyList).setAdapter(adapter).setListView(lv).setMessageView(messageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIYnames selected_diy = (DIYnames) parent.getAdapter().getItem(position);
                String name = (String) selected_diy.diyName;

                Log.d("name:",selected_diy.diyName);
                Intent intent = new Intent(Recommendation.this,Activity_View_Recommend.class);
                intent.putExtra("name",name);

                Bundle extra = new Bundle();
                extra.putSerializable("dbmaterials", dbMaterials);
                intent.putExtra("dbmaterials", extra);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(Recommendation.this, MainActivity.class);
        startActivity(intent);
    }


    public void onListFragmentInteractionListener(DatabaseReference ref) {
        Intent intent = new Intent(this, DIYDetailViewActivity.class);
        intent.putExtra("Community Ref", ref.toString());
        startActivity(intent);
    }
}