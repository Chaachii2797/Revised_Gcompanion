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

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait loading DIYs...");

        final Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");
        Log.e("FROMIMAGE", String.valueOf(dbMaterials));
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);

                if(!userID.equals(diYnames.getUser_id())){
                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {

                        DataSnapshot dbMaterialNode = postSnapshot;
                        String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
                        Log.e("dataSnapshotMaterial",dbMaterialName+" --- "+diYnames.getDiyName());
                        String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
                        Log.e("DBMATERIALS",dbMaterialUnit);
                        long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);
                        diYnames.addDbMaterial(new DBMaterial().setName(dbMaterialName).setUnit(dbMaterialUnit).setQuantity((int) dbMaterialQuantity));
                        diYnames.incrementTotalMaterial(dataSnapshot.child("materials").getChildrenCount());
                        for(int m = 0; m < dbMaterials.size(); m++) {
                            String dbMaterialsItem = dbMaterials.get(m).getName()+" ("+dbMaterials.get(m).getQuantity()+" "+dbMaterials.get(m).getUnit()+")";
                            Log.e("scanedMaterial",dbMaterialsItem+" --- "+dbMaterialName+" ("+dbMaterialQuantity+" "+dbMaterialUnit+")"+" = "+diYnames.getDiyName());
                            if (dbMaterialName.equals(dbMaterials.get(m).getName())) {
//                                diYnames.incrementScore();
                                if (dbMaterials.get(m).getQuantity() >= dbMaterialQuantity) {
                                    if (dbMaterials.get(m).getUnit().equals(dbMaterialUnit)) {
                                            diYnames.incrementScore();
                                        if (!exists(diYnames)) {
                                            Log.e("dbMaterialNameCheck3", dbMaterialName + " == " + dbMaterials.get(m).getName()+" ~ "+ diYnames.getMatchScore()
                                                    +" "+diYnames.getDiyName());
                                            diyList.add(diYnames);
                                            sortDiyList(diyList);
                                            adapter = new RecommendDIYAdapter(Recommendation.this, R.layout.pending_layout, diyList);
                                            lv.setAdapter(adapter);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    Toast.makeText(Recommendation.this, "YOUR MAKING YOUR OWN ITEM!", Toast.LENGTH_SHORT).show();
                }
                Log.e("DIYMatchScore",diYnames.getMatchScore()+" === "+diYnames.getDiyName());
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

    private void sortDiyList(ArrayList<DIYnames> diyList){
        float matchScoreRate;
        for(int b = 0 ; b < diyList.size(); b++){
            matchScoreRate = ((float)diyList.get(b).getMatchScore()/(float)diyList.get(b).getTotalMaterial())*100;
            Log.e("AAAAformula","("+diyList.get(b).getMatchScore()+"/"+diyList.get(b).getTotalMaterial()+ ") *100 = "+matchScoreRate + " "
                    +diyList.get(b).getDiyName());
            Log.e("SIIIIZE", diyList.get(b).getDbMaterialCount()+""+" "+diyList.get(b).getDiyName());
            diyList.get(b).setMatchScoreRate((int) matchScoreRate);
            if(matchScoreRate < 60){
                diyList.remove(b);
//                Log.e("matchScoreRateB",diyList.get(x).getMatchScoreRate()+" "+ diyList.get(x).getDiyName());
            }
        }
        for(int x = 0; x < diyList.size(); x++){
            matchScoreRate = ((float)diyList.get(x).getMatchScore()/(float)diyList.get(x).getTotalMaterial())*100;
            Log.e("BBBformula","("+diyList.get(x).getMatchScore()+"/"+diyList.get(x).getTotalMaterial()+ ") *100 = "+diyList.get(x).getMatchScore() + " "
                    +diyList.get(x).getDiyName());
            Log.e("SIIIIZE", diyList.get(x).getDbMaterialCount()+""+" "+diyList.get(x).getDiyName());
//
            for(int y = 0; y < (diyList.size()-x)-1; y++){
//                    diyList.get(y).setMatchScoreRate((int) matchScoreRate);
//                    float matchScoreRateA = ((float)diyList.get(y).getMatchScore()/(float)diyList.get(y).getTotalMaterial())*100;
//                    Log.e("Aformula","("+diyList.get(y).getMatchScore()+"/"+diyList.get(y).getTotalMaterial()+ ") *100 = "+matchScoreRateA);
//                    float matchScoreRateB = ((float)diyList.get(y+1).getMatchScore()/(float)diyList.get(y+1).getTotalMaterial())*100;
//                    Log.e("Bformula","("+diyList.get(y+1).getMatchScore()+"/"+diyList.get(y+1).getTotalMaterial()+ ") *100 = "+matchScoreRateB);
//                    diyList.get(y).setMatchScoreRate((int) matchScoreRateA);
//                    diyList.get(y+1).setMatchScoreRate((int) matchScoreRateB);
                if(diyList.get(y).getMatchScoreRate() < diyList.get(y+1).getMatchScoreRate()){
                    DIYnames holder = diyList.get(y);
                    diyList.set(y,diyList.get(y+1));
                    diyList.set(y+1,holder);
                    Log.e("DIYLISTLOOP",diyList.get(y).getDiyName());
//                        Log.e("removedUP",diyList.remove(x).getDiyName());
                }
            }
        }
        //getting 60% match rate items only

    }

//    private ArrayList<DIYnames> materialMatching(ArrayList<DIYnames> diyNames, ArrayList<DBMaterial> dbMaterials){
//        ArrayList<Integer> scores = new ArrayList<>();
//        for(int x = 0; x < diyNames.size(); x++){
//            ArrayList<DBMaterial> diyNameMaterialHolder = diyNames.get(x).getMaterialMatches();
//            for(int diyNameMaterialIndex = 0; diyNameMaterialIndex < diyNameMaterialHolder.size(); diyNameMaterialIndex++){
//                for(int materialRef = 0; materialRef < dbMaterials; materialRef++){
//                    if(diyNameMaterialHolder.get(diyNameMaterialIndex).get)
//                }
//            }
//            try {
//                scores.get( x );
//            } catch ( IndexOutOfBoundsException e ) {
//            }
//        }
//        return diyNames;
//    }

    private boolean exists(DIYnames diYnames) {
        boolean flag = false;
        for (DIYnames diyName : diyList) {
            if (diyName.getProductID().equals(diYnames.getProductID())) {
                flag = true;
            }
        }

        return flag;
    }


    @Override
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