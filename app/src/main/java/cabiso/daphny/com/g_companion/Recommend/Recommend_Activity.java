package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.UploadItems;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.UploadDIYAdapter;

public class Recommend_Activity extends AppCompatActivity {

    public TextView name,material;
    private List<DIYrecommend> diyList;
    private RecommendDIYAdapter adapter;
    String nm, mtrial;

    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_);

        name = (TextView)findViewById(R.id.tvDiyName);
        material = (TextView)findViewById(R.id.tvDiyMaterial);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

//        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("bottle");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("DIY_Methods").child("category");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    DIYrecommend recommend = postSnapshot.getValue(DIYrecommend.class);
                    String nm = "Procedure: "+recommend.getDiyprocedure();
                    String mtrial = "Material: "+recommend.getDiymaterial();

                    name.setText(nm);
                    material.setText(mtrial);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error: ","The read failed!"+databaseError.getMessage());
            }
        });

//        Query query = databaseReference.child("bottle");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    DIYrecommend img = snapshot.getValue(DIYrecommend.class);
//                    diyList.add(img);
//                }
//                adapter = new RecommendDIYAdapter(Recommend_Activity.this, R.layout.fragment_ui_items, diyList);
//                //set adapter for listview
//                lv.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
