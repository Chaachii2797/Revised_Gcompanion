package cabiso.daphny.com.g_companion.Recommend;

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
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDataActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Bottle_Recommend extends AppCompatActivity {

    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();

    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private DatabaseReference userDatabaseReference;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    //ArrayList<CommunityItem> itemMaterial;
    private List<String> tags = new ArrayList<>();

    public Bottle_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_bottle);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();


        String intent = getIntent().getStringExtra("result_tag");
        Toast.makeText(this," "+intent,Toast.LENGTH_LONG).show();
        for(int i=0;i<intent.length();i++){
            Toast.makeText(this," "+intent,Toast.LENGTH_LONG).show();
        }

        final String data = getIntent().getStringExtra("result_tag");
        String [] items = data.split(" ");
        for(final String item : items){

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(userID);
            myRef.child(item).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        if(snapshot.hasChildren()){
                            if(item!=null){
                                progressDialog.dismiss();
                                DIYnames diYnames = snapshot.getValue(DIYnames.class);
                                diyList.add(diYnames);
                                Toast.makeText(Bottle_Recommend.this, "counts "+diyList.size(),Toast.LENGTH_LONG).show();
                            }
                        }
                        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.recommend_ui, diyList);
                        lv.setAdapter(adapter);


                        registerForContextMenu(lv);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName(), Toast.LENGTH_SHORT).show();
                                DIYnames selectedItem = adapter.getItem(position);


                                //To-DO get you data from the ItemDetails Getter
                                // selectedItem.getImage() or selectedItem.getName() .. etc
                                // the  send the data using intent when opening another activity
                                Intent intent = new Intent(Bottle_Recommend.this, DIYDataActivity.class);
                              //  String items = infoList.get(position).getVal();

                                //adapter.notifyDataSetChanged();
//                                Toast toast = Toast.makeText(Bottle_Recommend.this, items, Toast.LENGTH_SHORT);
//                                toast.show();
                                intent.putExtra("image", selectedItem.getDiyUrl().getBytes());
                                intent.putExtra("name", selectedItem.getDiyName());
                                //intent.putExtra("procedures", infoList.get(position));
                                // intent.putExtra("materials", selectedItem.getDiymaterial());

                                view.buildDrawingCache();
                                Bitmap image = view.getDrawingCache();
                                Bundle extras = new Bundle();
                                extras.putParcelable("imagebitmap", image);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                intent.putExtra("image", byteArray);
                                startActivity(intent);

                                Toast.makeText(Bottle_Recommend.this, "counts "+diyList.size(),Toast.LENGTH_LONG).show();
                                Log.e("counter bes: ",""+diyList.size());
                            }
                        });
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

//        String result_tag = null;
//        Bundle b=this.getIntent().getExtras();
//        String[] array=b.getStringArray(result_tag);
//        Toast.makeText(this," "+array,Toast.LENGTH_LONG).show();


        //sugooooooooooooooooooooooood//
//        final ArrayList<CommunityItem> infoList = new ArrayList<CommunityItem>();

//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(userID);
//        myRef.child("people").addChildEventListener(new ChildEventListener()  {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if (snapshot.hasChildren()) {
////
////                        Collections.sort(diyList);
////                        Collections.reverse(diyList);
//
//                        progressDialog.dismiss();
//
//                        Toast.makeText(Bottle_Recommend.this,"HEY!",Toast.LENGTH_SHORT).show();
//
//                        DIYnames img = dataSnapshot.getValue(DIYnames.class);
//                        diyList.add(img);
//
//                        CommunityItem mp = dataSnapshot.getValue(CommunityItem.class);
//                        infoList.add(mp);
//
//                        adapter = new RecommendDIYAdapter(Bottle_Recommend.this, R.layout.recommend_ui, diyList);
//                        lv.setAdapter(adapter);
//
////                        if(snapshot.getChildrenCount() == diyList.size()){
////                            for(int i=0; i<diyList.size();i++){
////                                Log.e("get "," "+diyList.get(i).getSold_items());
////                            }f
////                        }
//
//
//                        registerForContextMenu(lv);
//                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName(), Toast.LENGTH_SHORT).show();
//                                DIYnames selectedItem = adapter.getItem(position);
//
//
//
//                                //To-DO get you data from the ItemDetails Getter
//                                // selectedItem.getImage() or selectedItem.getName() .. etc
//                                // the  send the data using intent when opening another activity
//                                Intent intent = new Intent(Bottle_Recommend.this, DIYDataActivity.class);
//                                String items = infoList.get(position).getVal();
//
//                                CommunityItem mat = (CommunityItem) parent.getItemAtPosition(position);
//
//
//                                //intent.putExtra("CATEGORY", mat);
//                                Bundle b = new Bundle();
//
//                                b.putString("Area", items);
//                                intent.putExtras(b);
//
//                                adapter.notifyDataSetChanged();
//                                Toast toast = Toast.makeText(Bottle_Recommend.this, items, Toast.LENGTH_SHORT);
//                                toast.show();
//                                intent.putExtra("image",selectedItem.getDiyUrl().toString());
//                                intent.putExtra("name",selectedItem.getDiyName());
//                                intent.putExtra("procedures", infoList.get(position));
//                               // intent.putExtra("materials", selectedItem.getDiymaterial());
//
//
//                                view.buildDrawingCache();
//                                Bitmap image = view.getDrawingCache();
//                                Bundle extras = new Bundle();
//                                extras.putParcelable("imagebitmap", image);
//
//                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                                byte[] byteArray = stream.toByteArray();
//                                intent.putExtra("image", byteArray);
//                                startActivity(intent);
//                            }
//                        });
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        /////laaaaaaaaaaaaaaaaaast///
    }

    public void sort_to_recommend(){
        DatabaseReference myRef = database.getReference("dy_by_tags");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Bottle_Recommend.this, MainActivity.class);
        startActivity(intent);
    }
}