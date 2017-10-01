package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Sold_Activity extends AppCompatActivity {

    private ArrayList<ProductInfo> diyList = new ArrayList<>();
    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference itemReference;
    public Sold_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);
        if(lv!=null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();


            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();

            myRef.child("Sold_Items").orderByChild("title").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.e(String.valueOf(snapshot.getRef()), snapshot.getChildrenCount() + "");
                        ProductInfo img = snapshot.getValue(ProductInfo.class);

                        if(img.getOwnerUserID().equals(userID)){
                            diyList.add(img);
                        }
                    }
                    //init adapter
                    adapter = new Items_Adapter(Sold_Activity.this, R.layout.recommend_ui, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            ProductInfo itemRef = adapter.getItem(position);
                            Toast toast = Toast.makeText(Sold_Activity.this, itemRef.title
                                    + "\n" + itemRef.ownerUserID + "\n" + itemRef.price + "\n" + itemRef.desc + "\n"
                                    + itemRef.getProductPictureURLs().get(0) + "\n" + count, Toast.LENGTH_SHORT);
                            toast.show();


                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(Sold_Activity.this, "Wala pa kay tinda na add!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Sold_Activity.this,MainActivity.class);
            startActivity(intent);
        }
    }
}