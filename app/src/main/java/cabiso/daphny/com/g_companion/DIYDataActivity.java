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
import java.util.List;

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

    private DIYnames diYnamesModel;

    private TextView diy_name, diy_materials, diy_procedures;
    private ImageView diy_image;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_diy_data);

        String diyReferenceString = getIntent().getStringExtra("Community Ref");

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceString);

        diYnamesModel = new DIYnames();

        if(getIntent().getExtras().getSerializable("diyName")!=null){
            diYnamesModel = (DIYnames) getIntent().getExtras().getSerializable("diyName");
            if(diYnamesModel!=null){
                Log.d("ViewRecommend", "not null");
            }else{
                Log.d("ViewRecommend", "null");
            }
        }

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
                if(dataSnapshot == null){
                    Log.d("dataSnap","null");
                }else{
                    Log.d("dataSnap","not null");
                }
                DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
//                diy_name.setText(diyInfo.diyName);
                CommunityItem item = dataSnapshot.getValue(CommunityItem.class);

                if(item!=null){
                    String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
                    Log.e("messageProd", ""+splitsMat);

                    String messageMat = "";
                    List<String> messageMaterials = new ArrayList<String>();
                    for(int i = 0; i<splitsMat.length; i++){
                        Log.d("splitVal", splitsMat[i].substring(5,splitsMat[i].length()-1));
                        String message = i+1 +".) "+ splitsMat[i].substring(5,splitsMat[i].length()-1).replaceAll("\\}", "")
                                .replaceAll("=", "");
                        messageMat+="\n"+message;
                        messageMaterials.add(message);

                        Log.d("messageProd", messageMat);
                    }

                    String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                    Log.e("splits", ""+splits);

                    String messageProd = "";
                    List<String> messageProcedure = new ArrayList<String>();
                    for(int i = 0; i<splits.length; i++){
                        Log.d("splitVal", splits[i].substring(5,splits[i].length()-1));
                        String message = i+1 +".) "+ splits[i].substring(5,splits[i].length()-1).replaceAll("\\}", "").replaceAll("=", "");
                        messageProd+="\n"+message;
                        messageProcedure.add(message);

                        Log.d("messageProd", messageProd);
                    }

                    Log.d("MessageProcedure", messageProcedure.toString());

                    diy_materials.setText(messageMat);
                    diy_procedures.setText(messageProd);

                    Log.d("SnapItem", "not null");
                    Log.d("SnapMaterial", ""+item);
                    Log.d("SnapDataSnap", ""+dataSnapshot.child("materials").getValue());
                }else{
                    Log.d("SnapItem", "null");
                }

//                    if (item != null) {
//                        Log.e("SnapItem", "not null");
//                        Log.e("SnapMaterial", "" + item);
//                        Log.e("SnapDataSnap", "" + snapshot.child("materials").getValue());
//
//                        diy_materials.setText(snapshot.child("materials").toString());
//                        diy_procedures.setText(snapshot.child("procedures").toString());
//                    } else {
//                        Log.e("SNAPSHOT: NULL??", "");
//                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
