package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class Item_Activity extends AppCompatActivity {

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
    public Item_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_);

            recyclerView = (RecyclerView) findViewById(R.id.list);

            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userID = mFirebaseUser.getUid();

            lv = (ListView) findViewById(R.id.lvView);
            if(lv!=null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait loading DIYs.....");
            progressDialog.show();

            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("marketplace");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductInfo img = snapshot.getValue(ProductInfo.class);
                        if (img.getOwnerUserID().toString().equals(userID)) {
                            diyList.add(img);
                        }
                    }
                    //init adapter
                    adapter = new Items_Adapter(Item_Activity.this, R.layout.recommend_ui, diyList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
//                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ProductInfo itemRef = adapter.getItem(position);
//                            adapter.remove(adapter.getItem(position));
//                            adapter.notifyDataSetChanged();
                            Toast toast = Toast.makeText(Item_Activity.this, itemRef.title
                                    + "\n" + itemRef.ownerUserID + "\n" + itemRef.price + "\n" + itemRef.desc + "\n"
                                    + itemRef.getProductPictureURLs().get(0) + "\n" , Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(Item_Activity.this, "Wala pa kay tinda na add!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Item_Activity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lvView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_for_myitems, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.sold_item:
                int listPosition = info.position;
                final int count =lv.getAdapter().getCount();
                itemReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);
                String title = diyList.get(listPosition).title;
                String description = diyList.get(listPosition).desc;
                String price = diyList.get(listPosition).price;
                String negotiable = diyList.get(listPosition).negotiable;
                List productPictureURLs = diyList.get(listPosition).productPictureURLs;
                ProductInfo product = new ProductInfo(title, description, price, negotiable,productPictureURLs, userID);
                String upload = itemReference.push().getKey();
                itemReference.child(upload).setValue(product);

        }
        return super.onContextItemSelected(item);
    }
}