package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.R;

public class Bottle_Recommend extends AppCompatActivity {

    private List<DIYrecommend> list;
    private ArrayList<String> diys = new ArrayList<String>();
    private RecommendDIYAdapter adapter;

    private ListView lv;
    public TextView name,material, procedure;
    private ImageView loadview;

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

        adapter = new RecommendDIYAdapter(this, R.layout.fragment_ui_items, diys);
        lv.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        final List<DIYrecommend> list = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DIY_Methods").child("category").child("bottle");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    diys.add(String.valueOf(postSnapshot.getValue()));
                    //Getting the data from snapshot
                    DIYrecommend diYrecommend = postSnapshot.getValue(DIYrecommend.class);
                    list.add(diYrecommend);

                    //Adding it to a string
                    String nm = "Name: "+diYrecommend.getDiyName();
                    String mat = "Material: "+diYrecommend.getDiymaterial();
                    String pro = "Procedure: "+diYrecommend.getDiyprocedure();

                    Log.d("NAME: "+diYrecommend.getDiyName()+"\n"+diYrecommend.getDiyprocedure()+
                            "\n"+diYrecommend.getDiymaterial(),"KUHAAAAAAAA");
                    name.setText(nm);
                    material.setText(mat);
                    procedure.setText(pro);
//                    list.add(recommend);

                    progressDialog.dismiss();
//                    System.out.println(name);
                    adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.fragment_ui_items, diys);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", databaseError.toException());
            }
        });


//        final List<DIYrecommend> diyList = new ArrayList<>();
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                diyList.clear();
//                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
//                    DIYrecommend recommend  = postSnapshot.getValue(DIYrecommend.class);
//                    Toast.makeText(getApplication(),"GET DIYS: "+diyList,Toast.LENGTH_SHORT).show();
//                    diyList.add(recommend);
//
//                    for(int a=0;a<diyList.size();a++){
//                        String nm =  "NAME: "+recommend.getDiyName();
//                        String mat = "MATERIAL: "+recommend.getDiymaterial();
//                        String pro = "PROCEDURE: "+recommend.getDiyprocedure();
//
//                            name.setText(nm);
//                            material.setText(mat);
//                            procedure.setText(pro);
//                    }
// }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("Error: ","The read failed!"+databaseError.getMessage());
//            }
//        });
    }
}
