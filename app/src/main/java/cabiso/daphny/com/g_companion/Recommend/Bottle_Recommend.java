package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYActivity;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.UploadDIYAdapter;

public class Bottle_Recommend extends AppCompatActivity {

    public TextView name,material, procedure;
    private RecommendDIYAdapter adapter;
    private ImageView loadview;
    private FirebaseStorage storage;
    private ListView lv;

    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);

        name = (TextView)findViewById(R.id.tvDiyName);
        material = (TextView)findViewById(R.id.tvDiyMaterial);
        procedure = (TextView)findViewById(R.id.tvDiyProcedure);
        loadview = (ImageView)findViewById(R.id.imgView);
        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIY_Methods").child("category").child("bottle");

        final List<DIYrecommend> diyList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                diyList.clear();
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    DIYrecommend recommend  = postSnapshot.getValue(DIYrecommend.class);
                    Toast.makeText(getApplication(),"GET DIYS: "+diyList,Toast.LENGTH_SHORT).show();
                    diyList.add(recommend);

                    String nm =  "NAME: "+recommend.getDiyName();
                    String mat = "MATERIAL: "+recommend.getDiymaterial();
                    String pro = "PROCEDURE: "+recommend.getDiyprocedure();
                    String image = recommend.getImage_URL();

                    name.setText(nm);
                    material.setText(mat);
                    procedure.setText(pro);
                    //init adapter
                    adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.fragment_ui_items, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error: ","The read failed!"+databaseError.getMessage());
            }
        });
    }
}
