package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.R;

import static cabiso.daphny.com.g_companion.R.mipmap.item;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Bottle_Recommend extends AppCompatActivity {

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

    public Bottle_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        addDiy = true;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        lv = (ListView) findViewById(R.id.recommendLvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait loading DIYs...");
//        progressDialog.show();
//
//        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.pending_layout, diyList);
//        lv.setAdapter(adapter);


//        final String priority = getIntent().getStringExtra("result_priority");
//        Log.e("PRIORITY_next ", "" + priority);
//
//        final String data = getIntent().getStringExtra("result_tag");
//        final String qtu = getIntent().getStringExtra("qty");
//        final String unt = getIntent().getStringExtra("unit");
//        final String[] items = data.split(" ");
//        final String[] qtys = qtu.split(" ");
//        final String[] uni = unt.split(" ");
        final Bundle extra = getIntent().getBundleExtra("dbmaterials");
        dbMaterials = (ArrayList<DBMaterial>) extra.getSerializable("dbmaterials");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
            for(int m = 0; m < dbMaterials.size(); m++){
                final int finalM = m;
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                                for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                                    DataSnapshot dbMaterialNode = postSnapshot;
                                    String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
                                    String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
                                    long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);
                                        if (dbMaterialName.equals(dbMaterials.get(finalM).getName())) {
                                            Log.e("dbMaterialNameCheck", dbMaterialName + " == " + item);
                                            if (dbMaterials.get(finalM).getQuantity() >= dbMaterialQuantity) {
                                                if (dbMaterials.get(finalM).getUnit().equals(dbMaterialUnit)) {
                                                    if (!exists(diYnames)) {
                                                        diyList.add(diYnames);

                                                        Collections.sort(diyList);
                                                        Collections.reverse(diyList);

                                                        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.pending_layout, diyList);
                                                        lv.setAdapter(adapter);
                                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                DIYnames selected_diy = (DIYnames) parent.getAdapter().getItem(position);
                                                                String name = (String) selected_diy.diyName;

                                                                Log.d("name:",selected_diy.diyName);
                                                                Intent intent = new Intent(Bottle_Recommend.this,Activity_View_Recommend.class);
                                                                intent.putExtra("name",name);

                                                                Bundle extra = new Bundle();
                                                                extra.putSerializable("dbmaterials", dbMaterials);
                                                                intent.putExtra("dbmaterials", extra);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(Bottle_Recommend.this, "WALAY NA MATCH!", Toast.LENGTH_SHORT).show();

                                                        final Dialog dialog = new Dialog(Bottle_Recommend.this);
                                                        dialog.setContentView(R.layout.new_alert_dialog);
                                                        TextView text = (TextView) dialog.findViewById(R.id.text);
                                                        text.setText("No DIY matched!");
                                                        ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                                        image.setImageResource(R.drawable.no_item_match_dialog);

                                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                        // if button is clicked, close the custom dialog
                                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(Bottle_Recommend.this, MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        dialog.show();
                                                    }
                                                }
                                            }
                                        }
                                    }
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
        Intent intent = new Intent(Bottle_Recommend.this, MainActivity.class);
        startActivity(intent);
    }


    public void onListFragmentInteractionListener(DatabaseReference ref) {
        Intent intent = new Intent(this, DIYDetailViewActivity.class);
        intent.putExtra("Community Ref", ref.toString());
        startActivity(intent);
    }
}