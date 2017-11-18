package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 9/23/2017.
 */

public class DIYDataActivity extends AppCompatActivity {
    private ArrayList<CommunityItem> infoList = new ArrayList<>();
    private ListView lv;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    private TextView diy_name, diy_materials, diy_procedures;
    private ImageView diy_image;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_diy_data);

        progressDialog = new ProgressDialog(this);

        //  diy_name = (TextView) findViewById(R.id.diy_name);
        diy_image = (ImageView) findViewById(R.id.diy_image);
        diy_materials = (TextView) findViewById(R.id.diy_materials);
        diy_procedures = (TextView) findViewById(R.id.diy_procedures);

        diy_procedures.setMovementMethod(new ScrollingMovementMethod());
        diy_materials.setMovementMethod(new ScrollingMovementMethod());

        final int mPostId = getIntent().getIntExtra("pos", 0);
        Log.e("mpostID", "" + mPostId);

        getIntent().getStringExtra("image");
        int imageID = getIntent().getIntExtra("image", 0);

        getIntent().getStringExtra("materials");
        String prod = getIntent().getStringExtra("procedures");

        Toast.makeText(getApplicationContext(), "PROCEDURES: "+ prod,Toast.LENGTH_SHORT).show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            byte[] byteArray = extras.getByteArray("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            diy_image.setImageBitmap(bmp);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);

//                    diyInfo.get(mPostId).getVal();
//                diy_name.setText(diyInfo.diyName);

                    CommunityItem item = dataSnapshot.getValue(CommunityItem.class);

                    if (item != null) {
                        Log.e("SnapItem", "not null");
                        Log.e("SnapMaterial", "" + item);
                        Log.e("SnapDataSnap", "" + snapshot.child("materials").getValue());

                        diy_materials.setText(snapshot.child("materials").toString());
                        diy_procedures.setText(snapshot.child("procedures").toString());
                    } else {
                        Log.e("SNAPSHOT: NULL??", "");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
