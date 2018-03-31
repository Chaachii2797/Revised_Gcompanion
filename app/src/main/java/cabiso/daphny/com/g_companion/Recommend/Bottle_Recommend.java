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

        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.pending_layout, diyList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName(), Toast.LENGTH_SHORT).show();
                DIYnames selectedItem = adapter.getItem(position);

                Toast.makeText(getApplicationContext(), "chiii " + position, Toast.LENGTH_SHORT).show();

//                                //To-DO get you data from the ItemDetails Getter
//                                // selectedItem.getImage() or selectedItem.getName() .. etc
//                                // the  send the data using intent when opening another activity
                Intent intent = new Intent(Bottle_Recommend.this, DIYDataActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("diyName", diyList.get(position));
                intent.putExtras(bundle);
                intent.putExtra("pos", position);
                intent.putExtra("image", selectedItem.getDiyUrl().getBytes());
                intent.putExtra("name", selectedItem.getDiyName());

                Toast.makeText(getApplicationContext(), "piste " + selectedItem, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "piste@@@@ " + position, Toast.LENGTH_SHORT).show();
                Log.e("communityItem: ", "" + position);

                view.buildDrawingCache();
                Bitmap image = view.getDrawingCache();
                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", image);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });
        final String priority = getIntent().getStringExtra("result_priority");
        Log.e("PRIORITY_next ", "" + priority);

        final String data = getIntent().getStringExtra("result_tag");
        final String qtu = getIntent().getStringExtra("qty_unit");
        final String[] items = data.split(" ");
        final String[] qtys = qtu.split(" ");

        for (String firstLoopitem : items) {
            final String item = firstLoopitem;
            for(String firstLoopQty : qtys){
                final String quantity = firstLoopQty;
            Log.e("itemsMASO: ", "" + item);
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    progressDialog.show();
                    if (item != null) {
                        DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                        Log.e("DIYLength", String.valueOf(diYnames.diyName.length()));
                        DataSnapshot commu_snapshot = dataSnapshot.child("materials");
                        DataSnapshot qty_snapshot = dataSnapshot.child("quantity");
                        String[] splits = commu_snapshot.toString().split(",");
                        String[] split_qty = qty_snapshot.toString().split(",");
                        Log.e("diyListTAAS: ", "" + diYnames);
                        Log.e("split_qty: ", "" + split_qty);

                        if (!exists(diYnames)) {
                            Log.e("checkIfAlreadyExist: ", "" + diYnames);
                            Log.d(diYnames.getProductID(), "Does not exist");
                            diYnames.setMaterialMatches(counter(items, splits));

//                            if (diYnames.getMaterialMatches() > 0) {
                                for(int a = 0; a < splits.length; a++){
                                    String message = splits[a].substring(0, splits[a].length()).replaceAll("\\}", " ").replaceAll("=", " ")
                                            .replaceAll("\\{", " ").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials,", "")
                                            .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").replaceAll("quantity","").trim();
                                    messageProcedure.add(message);

                                    for(String splitted : split_qty){
                                        Log.e("splittedFromFOR: ", "" + splitted);
                                    }

                                    if (item.equalsIgnoreCase(message)) {
                                        Log.d("MATCHED! ", item+" == "+message);
                                        for(String splitted : split_qty){
                                            String num = splitted.substring(0, splitted.length()).replaceAll("\\}", "").replaceAll("=", "")
                                                    .replaceAll("\\{", "").replaceAll("[A-Z]", "").replaceAll("[a-z]", "").replaceAll("val", "")
                                                    .replaceAll("quantity", "").trim();
                                            Log.e("splitted ", splitted);
                                            Log.e("checingQuantity ", num);
                                            String qty_num_img_recog = quantity.substring(0, quantity.length()).replaceAll("[A-Z]", "")
                                                    .replaceAll("[a-z]", "");

                                            String qty_uni_img_recog = quantity.substring(0, quantity.length()).replaceAll("quantity", "")
                                                    .replaceAll("[0-9]", "").trim();
                                            Log.e("qty_uni_img_recog ", qty_uni_img_recog);

                                            String unitOfMeasure = splitted.substring(0, splitted.length()).replaceAll("\\}", "").replaceAll("=", "")
                                            .replaceAll("\\{", "").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials", "")
                                            .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").replaceAll("quantity", "").trim();
                                            Log.e("unitOfMeasure ", unitOfMeasure);

                                            int qty_nums = 0;
                                            int qty_final =0;
                                            try {
                                                qty_nums = Integer.valueOf(qty_num_img_recog);
                                                qty_final = Integer.valueOf(num);
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                            }
                                                Log.e("checkingQuantity ", qty_nums+" == "+qty_final);
                                                if (qty_final <= qty_nums) {
                                                    Log.e("checkingUnitABOVE ", qty_uni_img_recog+" == "+unitOfMeasure);
                                                    Log.e("qty_uni_img_recog ", unitOfMeasure);
                                                    Log.e("unitOfMeasureINSIDE ", qty_uni_img_recog);
                                                    Log.e("QuantityMATCHED! ", qty_nums + " == " + qty_final);
                                                    if(unitOfMeasure.equalsIgnoreCase(qty_uni_img_recog)) {
                                                        Log.e("checkingUnit ", unitOfMeasure+" == "+qty_uni_img_recog);
                                                        Log.e("QuantityMATCHED! ", qty_nums + " == " + qty_final);
                                                        for (int get = 0; get < diyList.size(); get++) {
                                                            if (diYnames.getProductID().equals(diyList.get(get).getProductID())) {
                                                                addDiy = false;
                                                            }
                                                        }
                                                        diyList.add(diYnames);
                                                        Collections.sort(diyList);
                                                        Collections.reverse(diyList);
                                                        Log.e("ItemAddition ", "added " + item + " item");
                                                    }
                                                }


                                        }
                                    }
                                }
                                adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.pending_layout, diyList);
                                lv.setAdapter(adapter);

                                Log.e("checkMatchedMaterials: ", "" + diYnames);
//                                for (int i = 0; i < split_qty.length; i++) {
//                                    for(int a=0; a< splits.length; a++){
//                                    Log.e("splits: ", "" + splits);
//                                    String message = split_qty[i].substring(0, split_qty[i].length()).replaceAll("\\}", " ").replaceAll("=", " ")
//                                            .replaceAll("\\{", " ").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials,", "")
//                                            .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").replaceAll("quantity","").trim();
//                                    messageProcedure.add(message);
//                                    Log.e("message: ", "" + message);
//
//                                    String num = splits[i].substring(0, splits[i].length()).replaceAll("\\}", "").replaceAll("=", "")
//                                            .replaceAll("\\{", "").replaceAll("[A-Z]", "").replaceAll("[a-z]", "").replaceAll("val", "")
//                                            .replaceAll("quantity", "").replaceAll("0", "").trim();
////                                    String num = split_qty[i].substring(0, split_qty[i].length());
//                                    Log.e("numbaaaaaaaaaaaaaaaa: ", "" + num);
//
//                                    String alpha = splits[i].substring(0, splits[i].length()).replaceAll("\\}", "").replaceAll("=", "")
//                                            .replaceAll("\\{", "").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials", "")
//                                            .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").replaceAll("quantity", "").trim();
////                                    String alpha = split_qty[i].substring(0, split_qty[i].length());
//                                    Log.e("alpha: ", "" + alpha);
//
//                                    //gikan ni sha sa pag image recog na quantity
//                                    String qty_num_img_recog = quantity.substring(0, quantity.length()).replaceAll("[A-Z]", "")
//                                            .replaceAll("[a-z]", "");
//                                    Log.e("qty_num_img_recog: ", "" + qty_num_img_recog);
//
//                                    //gikan ni sha sa pag image recog na unit
//                                    String qty_uni_img_recog = quantity.substring(0, quantity.length()).replaceAll("quantity", "").replaceAll("[0-9]", "").trim();
//                                    Log.e("qty_uni_img_recog: ", "" + qty_uni_img_recog);
//
//                                    try {
//                                        int qty_nums = Integer.valueOf(qty_num_img_recog);
//                                        int qty_final = Integer.valueOf(num);
//
//                                        Log.e("qty_nums: ", "" + qty_nums);
//                                        Log.e("qty_final: ", "" + qty_final);
//                                        String[] this_new = qty_uni_img_recog.split(" ");
//                                        Log.e("this_new", "" + this_new);
//                                        String[] new_string = new String[qty_uni_img_recog.split("").length];
//                                        Log.e("new_String", "" + new_string);
//                                        if (qty_final == qty_nums) {
//                                            Log.e("checkIfQtyMatched: ", "" + diYnames);
//                                            Log.e("forMatchingTAAS: ", "" + qty_num_img_recog);
////                                            if(qty_uni_img_recog.equals(alpha)){
//                                            Log.e("finalMatching: ", "" + item +" equalsIgnoreCase "+message);
//                                            if (item.equalsIgnoreCase(message)) {
//
////                                                for (int get = 0; get < diyList.size(); get++) {
////                                                    if (diYnames.getProductID().equals(diyList.get(get).getProductID())) {
////                                                        addDiy = false;
////                                                        Log.e("forMatchingInside: ", "" + qty_num_img_recog);
////                                                    }
////                                                }
//                                                diyList.add(diYnames);
//                                                Collections.sort(diyList);
//                                                Collections.reverse(diyList);
//                                                Log.e("diyList: ", "" + diYnames);
//                                                Log.e("forMatching: ", "" + qty_num_img_recog);
//                                            }
////                                        }
//                                        }
////                                        else if(!(qty_final <=qty_nums) || !(qty_uni_img_recog.equals(alpha))){
////                                            Toast.makeText(Bottle_Recommend.this, "WALAY NA MATCH!", Toast.LENGTH_SHORT).show();
////
////                                            final Dialog dialog = new Dialog(Bottle_Recommend.this);
////                                            dialog.setContentView(R.layout.new_alert_dialog);
////                                            TextView text = (TextView) dialog.findViewById(R.id.text);
////                                            text.setText("No DIY matched!");
////                                            ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
////                                            image.setImageResource(R.drawable.no_item_match_dialog);
////
////                                            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
////                                            // if button is clicked, close the custom dialog
////                                            dialogButton.setOnClickListener(new View.OnClickListener() {
////                                                @Override
////                                                public void onClick(View v) {
////                                                    Intent intent = new Intent(Bottle_Recommend.this, MainActivity.class);
////                                                    startActivity(intent);
////                                                }
////                                            });
////                                            dialog.show();
////                                        }
//                                    } catch (NumberFormatException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                }
                                Collections.sort(diyList, new MaterialsComparator());
                                progressDialog.dismiss();
//                            }

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