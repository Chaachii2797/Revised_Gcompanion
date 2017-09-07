package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.R;

public class Bottle_Recommend extends AppCompatActivity {

    public TextView name,material, procedure;
    private List<DIYrecommend> diyList;
    private RecommendDIYAdapter adapter;
    private ArrayList<String> diys;

    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);

        name = (TextView)findViewById(R.id.tvDiyName);
//        material = (TextView)findViewById(R.id.tvDiyMaterial);
//        procedure = (TextView)findViewById(R.id.tvProcedure);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

//        databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("bottle");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIY_Methods").child("category").child("bottle");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            String getDiy = "DIYS: ";
                    diys = new ArrayList<String>();

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    diys.add(String.valueOf(postSnapshot.getValue()));
                    Toast.makeText(getApplication(),"GET DIYS: "+diys,Toast.LENGTH_SHORT).show();
                    Log.e("GET DIYS: "+diys, "KUHAAAAAAa");
                    for(int i=0;i<diys.size();i++){
                        getDiy +="\n"+diys.get(i);
                    }
                }
                name.setText(getDiy);

//                    DIYrecommend recommend = dataSnapshot.getValue(DIYrecommend.class);
//                    String nm = "Name: "+recommend.getDiyName();
//                    Log.d("NAME: "+nm," ");
//                    String mtrial = "Material: "+recommend.getDiymaterial();
//                    String proc = "Procedure: "+recommend.getDiyprocedure();
//                    String id = dataSnapshot.getKey();
//                    Toast.makeText(getApplication(),"ID: "+id,Toast.LENGTH_SHORT).show();
//
//                    name.setText(nm);
//                    material.setText(mtrial);
//                    procedure.setText(proc);



                progressDialog.dismiss();
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
