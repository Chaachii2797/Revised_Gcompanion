package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDataActivity;
import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.MaterialsComparator;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Bottle_Recommend extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference communityReference;

    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<QuantityItem> qty_list = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();

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


        final String priority = getIntent().getStringExtra("result_priority");
        Log.e("PRIORITY_next ", "" + priority);

        final String data = getIntent().getStringExtra("result_tag");
        final String qtu = getIntent().getStringExtra("qty");
        final String unt = getIntent().getStringExtra("unit");
        final String[] items = data.split(" ");
        final String[] qtys = qtu.split(" ");
        final String[] uni = unt.split(" ");

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");

        for (String firstLoopitem : items) {
            final String item = firstLoopitem.toLowerCase();
            for(final String firstLoopQty : qtys){
                final String quantity = firstLoopQty;
                for(String firstLoopUnit : uni) {
                    final String qty_uni_img_recog = firstLoopUnit;
                    myRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (item != null) {
                                Log.e("recogItem", item);
                                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                                long dbItemCount = dataSnapshot.child("materials").getChildrenCount();
                                String num = firstLoopQty.substring(0, firstLoopQty.length()).replaceAll("\\}", "").replaceAll("=", "")
                                        .replaceAll("\\{", "").replaceAll("[A-Z]", "").replaceAll("[a-z]", "").replaceAll("val", "")
                                        .replaceAll("quantity", "").replaceAll("0", "").trim();

                                Log.e("recogItemValidation", "BEFORE TRY CATCH");
                                Log.e("recogUnit", qty_uni_img_recog);
                                try {
                                    int numberQty = Integer.valueOf(num);
                                    Log.e("recogItemValidation", "AFTER TRY CATCH");
                                    Log.e("recogUnit111", qty_uni_img_recog);
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                                        //                                for (int i = 0; i < dbItemCount ;i++){
                                        DataSnapshot dbMaterialNode = postSnapshot;
                                        Log.e("recogUnit222", qty_uni_img_recog);
                                        String dbMaterialName = dbMaterialNode.child("name").getValue(String.class).toLowerCase();
                                        String dbMaterialUnit = dbMaterialNode.child("unit").getValue(String.class);
                                        long dbMaterialQuantity = dbMaterialNode.child("quantity").getValue(Long.class);

                                        Log.e("dbItemName", dbMaterialName + " == " + item);
                                        Log.e("dbItemUnit", dbMaterialUnit + " == " + qty_uni_img_recog);
                                        Log.e("dbItemQuantity", dbMaterialQuantity + " == " + numberQty);
                                        if (dbMaterialName.equals(item)) {
                                            Log.e("dbMaterialNameCheck", dbMaterialName + " == " + item);
                                            if (numberQty >= dbMaterialQuantity) {
                                                Log.e("dbMaterialQtyCheck", dbMaterialQuantity + " == " + numberQty);

                                                if (qty_uni_img_recog.equals(dbMaterialUnit)) {
                                                    Log.e("dbMaterialUnitCheck", dbMaterialUnit + " == " + qty_uni_img_recog);
                                                    if (!exists(diYnames)) {
                                                        diyList.add(diYnames);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                } catch (NumberFormatException e) {

                                } catch (NullPointerException e) {

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
        }
        Collections.sort(diyList);
        Collections.reverse(diyList);
        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.pending_layout, diyList);
        lv.setAdapter(adapter);
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

//    private boolean matched(QuantityItem quantityItems){
//        boolean flag = false;
//        for(QuantityItem quantityItem :quan)
//    }

    private int counter(String[] items, String[] materials) {
        int count = 0;
        for (String item : items) {
            for (String mat : materials) {
                String message = mat.substring(0, mat.length()).replaceAll("\\}", " ").replaceAll("=", " ")
                        .replaceAll("\\{", " ").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials,", "")
                        .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").trim();
//                int mess_int = Integer.parseInt(message);
//                Log.e("mess_int", String.valueOf(mess_int));

                if (item.equals(message)) {
                    count++;
                    Log.e("damn", message);
                }
            }
        }
        return count;
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