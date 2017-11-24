package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Bottle_Recommend extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference communityReference;

    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();

    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    // ImageButton star;

    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    //ArrayList<CommunityItem> itemMaterial;
    private List<String> tags = new ArrayList<>();
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
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();


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
        final String[] items = data.split(" ");
        for (final String item : items) {
            Log.e("itemsMASO: ", "" + item);
            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
            myRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (item != null) {
                        DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                        DataSnapshot commu_snapshot = dataSnapshot.child("materials");
                        String[] splits = commu_snapshot.toString().split(",");

                        if (!exists(diYnames)) {
                            Log.d(diYnames.getProductID(), "Does not exist");
                            String messageProd = "";
                            int[] has;
                            int count = 0;
                            diYnames.setMaterialMatches(counter(items, splits));
                            if (diYnames.getMaterialMatches() > 0) {
                                for (int i = 0; i < splits.length; i++) {
                                    Log.e("splits: ", "" + splits);
                                    String message = splits[i].substring(0, splits[i].length()).replaceAll("\\}", " ").replaceAll("=", " ")
                                            .replaceAll("\\{", " ").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials,", "")
                                            .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").trim();
                                    messageProd = " " + message;
                                    messageProcedure.add(message);

                                    Log.d("DiyMess", message);

                                    if (item.equalsIgnoreCase(message)) {
                                        //  if(diYnames.getProductID().equals(diYnames.getProductID())) {
//                            if(splits[i].contains(message)){
                                        Log.e("messageProd: ", "" + messageProd);
                                        Log.e("messageeeeeeees: ", "" + message);
                                        Log.e("counting: ", "" + count++);

                                        for (int get = 0; get < diyList.size(); get++) {
                                            Log.d("naayParehas", "inside");
                                            if (diYnames.getProductID().equals(diyList.get(get).getProductID())) {
                                                addDiy = false;

                                                Log.d("countExist", String.valueOf(diYnames.getMaterialMatches()));

                                                Log.d("naayParehas", diyList.get(get).toString());

//                                    String toMoveUp = String.valueOf(init);
//                                    while(diyList.indexOf(toMoveUp)!=0){
//                                        int til = diyList.indexOf(toMoveUp);
//                                        Collections.swap(diyList, til, til-1);
//                                    }
//                                    Log.d("currDIY", diyList.toString());
//                                    diyList.add(0, diyList.get(init));
//                                    diyList.remove(diyList.get(init+1));
//                                    Log.d("ListDiy", diyList.toString());
//                                    adapter.notifyDataSetChanged();
                                            }
                                        }
                                        /*if (addDiy) {
                                            diyList.add(diYnames);
                                        } else {
                                            addDiy = true;
                                        }*/
                                        diyList.add(diYnames);
                                        Collections.sort(diyList);
                                        Collections.reverse(diyList);

                                        String commu = commu_snapshot.getValue().toString().replaceAll("\\}", "").replaceAll("=", "")
                                                .replaceAll("\\{", "").replaceAll("DataSnapshot", "").replaceAll("\\[", "").replaceAll("val", "")
                                                .replaceAll(",", "").replaceAll("\\]", "");
                                        check.add(commu);
                                        boolean matches = false;
                                        for (int j = 0; j < commu.length(); j++) {

                                        }

                                        Log.d("countItem", String.valueOf(count));
                                        Log.e("commuuu: ", " " + commu);
                                        Log.e("commu_snapshot: ", " " + commu_snapshot);
                                        // }
                                    }
                                }
                                Collections.sort(diyList, new MaterialsComparator());
                                progressDialog.dismiss();
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

//        }
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


    private int counter(String[] items, String[] materials) {
        int count = 0;
        for (String item : items) {
            for (String mat : materials) {
                String message = mat.substring(0, mat.length()).replaceAll("\\}", " ").replaceAll("=", " ")
                        .replaceAll("\\{", " ").replaceAll("DataSnapshot", "").replaceAll("key", "").replaceAll("materials,", "")
                        .replaceAll("value", "").replaceAll("val", "").replaceAll("[0-9]", "").trim();
                if (item.equals(message)) {
                    count++;
                }
            }
        }
        return count;
    }


    public void sort_to_recommend() {
        DatabaseReference myRef = database.getReference("dy_by_tags");
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