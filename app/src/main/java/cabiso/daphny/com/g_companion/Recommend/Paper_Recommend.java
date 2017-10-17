package cabiso.daphny.com.g_companion.Recommend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

import cabiso.daphny.com.g_companion.DIYDataActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Paper_Recommend extends AppCompatActivity {

    private ArrayList<DIYrecommend> diyList = new ArrayList<>();
    private ListView lv;
    private ImageView loadview;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private LinearLayoutManager mLinearLayoutManager;

    public Paper_Recommend() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper__recommend);

            recyclerView = (RecyclerView) findViewById(R.id.list);

            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userID = mFirebaseUser.getUid();

            lv = (ListView) findViewById(R.id.lvView);
            if(lv!=null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();

            //init adapter
            adapter = new RecommendDIYAdapter(Paper_Recommend.this, R.layout.recommend_ui, diyList);
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("DIY_Methods").child("category").child("paper");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        DIYrecommend img = snapshot.getValue(DIYrecommend.class);
                        String image = (String) map.get("0");
                        diyList.add(img);

                    }
                    //init adapter
                    adapter = new RecommendDIYAdapter(Paper_Recommend.this, R.layout.recommend_ui, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                        Intent intent = new Intent(Bottle_Recommend.this, DIYDataActivity.class);
                            Toast.makeText(getApplicationContext(), diyList.get(position).getDiyName() +
                                    diyList.get(position).diymaterial + diyList.get(position).diyprocedure +
                                    diyList.get(position).diyImageUrl, Toast.LENGTH_SHORT).show();

                            DIYrecommend selectedItem = adapter.getItem(position);
                            //To-DO get you data from the ItemDetails Getter
                            // selectedItem.getImage() or selectedItem.getName() .. etc
                            // the  send the data using intent when opening another activity
                            Intent intent = new Intent(Paper_Recommend.this, DIYDataActivity.class);
                            //  intent.putExtra("image",selectedItem.getDiyImageUrl().toString());
                            //   intent.putExtra("name",selectedItem.getDiyName());
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }else{
            Toast.makeText(Paper_Recommend.this, "No available DIY for this category!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Paper_Recommend.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Paper_Recommend.this, MainActivity.class);
        startActivity(intent);
    }
}