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

import cabiso.daphny.com.g_companion.DIYDataActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/27/2017.
 */

public class Cup_Recommend extends AppCompatActivity {

    private ArrayList<DIYrecommend> diyList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    public Cup_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cup_recommend);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        DatabaseReference myRef = database.getReference("DIYs_By_Users");
        myRef.child("cup").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.hasChildren()) {

//                        Collections.sort(diyList);
//                        Collections.reverse(diyList);
                        progressDialog.dismiss();
                        Log.e("datasnaphot: ", " " + dataSnapshot.getChildrenCount());
                        Log.e("datasnaphot: ", " " + dataSnapshot.toString());

                        DIYrecommend img = snapshot.getValue(DIYrecommend.class);
                        diyList.add(img);
                        Log.d("LOGGING: " + img.getDiyName(), "");

                        DIYrecommend temp;
                        DIYrecommend temp2 = new DIYrecommend();

                        adapter = new RecommendDIYAdapter(Cup_Recommend.this, R.layout.recommend_ui, diyList);
                        //set adapter for listview
                        lv.setAdapter(adapter);

                        Toast.makeText(getApplicationContext(), "KUHAA: " + img.getDiyName(), Toast.LENGTH_SHORT).show();
                        if (snapshot.getChildrenCount() == diyList.size()) {




                          /*  for(int j =0; j < diyList.size(); j++){
                                for(int i = 0; i < diyList.size(); i++){
                                    if(i ==0){
                                        temp2 = diyList.get(i+1);
                                    }
                                    if(diyList.get(i).getSold_items() > temp2.getSold_items()){
                                        temp = diyList.get(+1);
                                        temp2 = diyList.get(i);


                                    }
                                }
                            }*/





//                            for (int i = 0; i < diyList.size(); i++) {
//                                Log.e("daphny ", " " + diyList.get(i).getSold_items());
//                            }
                        }
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                  Intent intent = new Intent(Bottle_Recommend.this, DIYDataActivity.class);
                                Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName() +
                                        diyList.get(position).diymaterial + diyList.get(position).diyprocedure +
                                        diyList.get(position).diyImageUrl, Toast.LENGTH_SHORT).show();
                                DIYrecommend selectedItem = adapter.getItem(position);
                                //To-DO get you data from the ItemDetails Getter
                                // selectedItem.getImage() or selectedItem.getName() .. etc
                                // the  send the data using intent when opening another activity
                                Intent intent = new Intent(Cup_Recommend.this, DIYDataActivity.class);
                                //  intent.putExtra("image",selectedItem.getDiyImageUrl().toString());
                                // intent.putExtra("name",selectedItem.getDiyName());
                                intent.putExtra("procedures", selectedItem.getDiyprocedure());
                                intent.putExtra("materials", selectedItem.getDiymaterial());

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

    public void sort_to_recommend() {
        DatabaseReference myRef = database.getReference("Sold_Items");

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Cup_Recommend.this, MainActivity.class);
        startActivity(intent);
    }
}